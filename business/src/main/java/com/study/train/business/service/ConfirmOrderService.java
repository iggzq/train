package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.*;
import com.study.train.business.enums.ConfirmOrderStatusEnum;
import com.study.train.business.enums.RedisKeyPreEnum;
import com.study.train.business.enums.SeatTypeEnum;
import com.study.train.business.mapper.ConfirmOrderMapper;
import com.study.train.business.req.ConfirmOrderQueryReq;
import com.study.train.business.req.ConfirmOrderReq;
import com.study.train.business.req.ConfirmOrderSaveReq;
import com.study.train.business.req.ConfirmOrderTicketReq;
import com.study.train.business.resp.ConfirmOrderQueryResp;
import com.study.train.common.context.LoginMemberHolder;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.req.MemberTicketReq;
import com.study.train.common.resp.PageResp;
import com.study.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.apache.seata.core.context.RootContext;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private LoginMemberHolder loginMemberHolder;

    @Resource
    ConfirmOrderMapper confirmOrderMapper;

    @Resource
    DailyTrainTicketService dailyTrainTicketService;

    @Resource
    DailyTrainStationCarriageService dailyTrainStationCarriageService;

    @Resource
    private DailyTrainStationSeatService dailyTrainStationSeatService;

    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;

    @Resource
    private PaymentService paymentService;

    @Resource
    private RedissonClient redissonClient;


    public void save(ConfirmOrderSaveReq confirmOrderSaveReq) {
        DateTime now = new DateTime();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(confirmOrderSaveReq, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }

    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrders = confirmOrderMapper.selectByExample(confirmOrderExample);

        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrders);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<ConfirmOrderQueryResp> confirmOrderQueryResps = BeanUtil.copyToList(confirmOrders, ConfirmOrderQueryResp.class);
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(confirmOrderQueryResps);

        return pageResp;
    }

    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    public Long getExpireTime(ConfirmOrderReq confirmOrderReq) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.createCriteria().andMemberIdEqualTo(loginMemberHolder.getId()).andTrainCodeEqualTo(confirmOrderReq.getTrainCode()).andDateEqualTo(confirmOrderReq.getDate());
        Date createTime = confirmOrderMapper.selectByExample(confirmOrderExample).get(0).getCreateTime();
        System.out.println(createTime);
        LocalDateTime now = LocalDateTimeUtil.now();
        System.out.println(now);
        Instant instant = createTime.toInstant();
        LocalDateTime localCreateTime = LocalDateTime.ofInstant(instant, TimeZone.getTimeZone("GMT+8").toZoneId());
        System.out.println(localCreateTime);
        localCreateTime = localCreateTime.plusMinutes(15);
        System.out.println(localCreateTime);

        return LocalDateTimeUtil.between(now, localCreateTime).getSeconds();
    }


    //    @GlobalTransactional
    public void saveConfirm(ConfirmOrderReq req) {
        LOG.info("seata全局事务ID: {}", RootContext.getXID());
        // 添加分布式锁
        LOG.info("saveConfirm抢锁开始");
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER.getKey() + req.getTrainCode() + ":" + req.getDate();
        RLock lock = null;
        try {
            lock = redissonClient.getLock(lockKey);
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                LOG.info("{} 成功拿锁", Thread.currentThread().getName());
            } else {
                LOG.info("{} 抢锁失败,有其他消费线程正在处理，不做任何处理", Thread.currentThread().getName());
                return;
            }

            // 此处省略了业务校验，如车次是否存在
            // 1.检查该乘客是否已经下过单,每个日期的每个车次用户只能下一张单

            while (true) {
                // 取确认订单表的记录，同日期车次，状态是I，分页处理，每次取N条
                ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
                confirmOrderExample.setOrderByClause("id asc");
                ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
                criteria.andDateEqualTo(req.getDate())
                        .andTrainCodeEqualTo(req.getTrainCode())
                        .andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
                PageHelper.startPage(1, 100);
                List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExampleWithBLOBs(confirmOrderExample);

                if (CollUtil.isEmpty(confirmOrderList)) {
                    LOG.info("没有需要处理的订单，结束循环");
                    break;
                } else {
                    LOG.info("本次处理{}条订单", confirmOrderList.size());
                }
                // 一条一条的卖
                confirmOrderList.forEach(confirmOrder -> {
                    try {
                        sell(confirmOrder);
                    } catch (BusinessException e) {
                        if (e.getBusinessExceptionEnum().equals(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR)) {
                            LOG.info("本订单余票不足，继续售卖下一个订单");
                            confirmOrder.setStatus(ConfirmOrderStatusEnum.EMPTY.getCode());
                            updateStatus(confirmOrder);
                        } else {
                            throw e;
                        }
                    }
                });
            }

        } catch (InterruptedException e) {
            LOG.error("购票异常", e);
        } finally {
            LOG.info("{} 购票流程结束，释放锁!", Thread.currentThread().getName());
            if (null != lock && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 更新状态
     */
    public void updateStatus(ConfirmOrder confirmOrder) {
        ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
        confirmOrderForUpdate.setId(confirmOrder.getId());
        confirmOrderForUpdate.setUpdateTime(new Date());
        confirmOrderForUpdate.setStatus(confirmOrder.getStatus());
        confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);
    }

    /**
     * 售票
     */
    private void sell(ConfirmOrder confirmOrder) {
        // 构造ConfirmOrderDoReq
        ConfirmOrderReq req = new ConfirmOrderReq();
        req.setMemberId(confirmOrder.getMemberId());
        req.setDate(confirmOrder.getDate());
        req.setTrainCode(confirmOrder.getTrainCode());
        req.setStart(confirmOrder.getStart());
        req.setEnd(confirmOrder.getEnd());
        req.setDailyTrainTicketId(confirmOrder.getDailyTrainTicketId());
        req.setTickets(JSON.parseArray(confirmOrder.getTickets(), ConfirmOrderTicketReq.class));
        req.setLogId("");

        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

        // 将订单设置成处理中，避免重复处理
        LOG.info("将确认订单更新成处理中，避免重复处理，confirm_order.id: {}", confirmOrder.getId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.PENDING.getCode());
        updateStatus(confirmOrder);

        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();

        List<ConfirmOrderTicketReq> tickets = req.getTickets();

        // 查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录：{}", dailyTrainTicket);

        // 预扣减余票数量，并判断余票是否足够和返回应支付金额
        Float totalMoney = reduceTicketNum(req, dailyTrainTicket);

        // 创建最终选座对象，用以保存选座结果
        List<DailyTrainStationSeat> finalSeatList = new ArrayList<>();
        for (ConfirmOrderTicketReq ticket : tickets) {
            if (StrUtil.isBlank(ticket.getSeatPosition())) {
                // 4.1 没有选座，进入自动选座逻辑
                LOG.info("本张票没有选座");
                getSeat(finalSeatList, date, trainCode, ticket.getSeatTypeCode(), null, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
            } else {
                // 4.2 有选座，进入指定选座逻辑
                LOG.info("本张票有选座");
                getSeat(finalSeatList, date, trainCode, ticket.getSeatTypeCode(), ticket.getSeatPosition(), dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
            }
        }

        LOG.info("最终选座结果：{}", finalSeatList);

        // 5.车次减去已购票数并增添用户购票结果
        try {
            List<MemberTicketReq> memberTicketReqs = afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, tickets, confirmOrder, totalMoney);
            // 6.设置支付过期时间,若订单未支付，则恢复余票
            paymentService.setPaymentStatusWithExpiration(String.valueOf(confirmOrder.getId()), confirmOrder, 5, finalSeatList, dailyTrainTicket, req, memberTicketReqs);
        } catch (Exception e) {
            LOG.error("保存购票信息失败", e);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
        }


    }


    private void getSeat(List<DailyTrainStationSeat> finalSeatList, Date date, String reqTrainCode, String reqCarriageType, String reqSeatPosition, Integer startIndex, Integer endIndex) {
        // 1.创建座位列表，用于保存选座结果
        List<DailyTrainStationSeat> getSeatList = new ArrayList<>();
        // 2.查询所有符合条件的车厢,因为分一等车厢，二等车厢
        List<DailyTrainStationCarriage> dailyTrainStationCarriages = dailyTrainStationCarriageService.selectBySeatType(date, reqTrainCode, reqCarriageType);
        LOG.info("共查询{}个符合条件的车厢", dailyTrainStationCarriages.size());
        // 3.遍历所有符合条件的车厢，从车厢中选座位
        for (DailyTrainStationCarriage dailyTrainStationCarriage : dailyTrainStationCarriages) {
            LOG.info("开始从车厢{}选座", dailyTrainStationCarriage.getIndex());
            List<DailyTrainStationSeat> dailyTrainStationSeats = dailyTrainStationSeatService.selectByCarriage(date, reqTrainCode, dailyTrainStationCarriage.getIndex());
            // 4.遍历车厢中的座位，判断座位是否可以出售，若可以，则加入到getSeatList中
            // 5.根据用户选择的座位类型来决定i的初始值和跳转
            int start = 0;
            int jump = 1;
            if (reqCarriageType.equals(SeatTypeEnum.YDZ.getKey())) {
                if (ObjectUtil.isNotNull(reqSeatPosition)) {
                    jump = 4;
                    start = switch (reqSeatPosition) {
                        case "A" -> 0;
                        case "C" -> 1;
                        case "D" -> 2;
                        case "F" -> 3;
                        default -> start;
                    };
                }
            } else if (reqCarriageType.equals(SeatTypeEnum.EDZ.getKey())) {
                if (ObjectUtil.isNotNull(reqSeatPosition)) {
                    jump = 5;
                    start = switch (reqSeatPosition) {
                        case "A" -> 0;
                        case "B" -> 1;
                        case "C" -> 2;
                        case "D" -> 3;
                        case "F" -> 4;
                        default -> start;
                    };
                }
            }

            // 6.遍历车厢中的座位，判断座位是否可以出售，若可以，则加入到getSeatList中
            for (; start < dailyTrainStationSeats.size(); start += jump) {
                // 7.获取车厢中的座位对象
                DailyTrainStationSeat dailyTrainSeat = dailyTrainStationSeats.get(start);
                String col = dailyTrainSeat.getCol();
                // 8.
                boolean alreadySellFlag = false;
                for (DailyTrainStationSeat finalSeat : finalSeatList) {
                    if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadySellFlag = true;
                        break;
                    }
                }
                if (alreadySellFlag) {
                    LOG.info("座位{}已被出售", dailyTrainSeat.getCarriageSeatIndex());
                    continue;
                }

                if (StrUtil.isBlank(reqSeatPosition)) {
                    LOG.info("有选座");
                } else {
                    if (!reqSeatPosition.equals(col)) {
                        continue;
                    }
                }
                boolean canSellFlag = canSell(dailyTrainSeat, startIndex, endIndex);
                if (canSellFlag) {
                    LOG.info("可以选座");
                    getSeatList.add(dailyTrainSeat);
                } else {
                    continue;
                }
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    private boolean canSell(DailyTrainStationSeat dailyTrainStationSeat, Integer startIndex, Integer endIndex) {
        String sell = dailyTrainStationSeat.getSell();
        String sellPart = sell.substring(startIndex - 1, endIndex - 1);
        if (Integer.parseInt(sellPart) > 0) {
            LOG.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainStationSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            String sellPosition = sellPart.replaceAll("0", "1");
            sellPosition = StrUtil.fillBefore(sellPosition, '0', endIndex - 1);
            sellPosition = StrUtil.fillAfter(sellPosition, '0', sell.length());
            int newSell = NumberUtil.binaryToInt(sellPosition) | NumberUtil.binaryToInt(sell);
            String newSellStr = NumberUtil.getBinaryStr(newSell);
            newSellStr = StrUtil.fillBefore(newSellStr, '0', sell.length());
            dailyTrainStationSeat.setSell(newSellStr);
            LOG.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}", dailyTrainStationSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, sellPosition, newSell);
            return true;
        }

    }

    private Float reduceTicketNum(ConfirmOrderReq confirmOrderReq, DailyTrainTicket dailyTrainTicket) {
        float totalMoney = 0f;
        for (ConfirmOrderTicketReq ticketReq : confirmOrderReq.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getKey, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    totalMoney += dailyTrainTicket.getYdzPrice().floatValue();
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    totalMoney += dailyTrainTicket.getEdzPrice().floatValue();
                    dailyTrainTicket.setYdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    totalMoney += dailyTrainTicket.getRwPrice().floatValue();
                    dailyTrainTicket.setYdz(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    totalMoney += dailyTrainTicket.getYwPrice().floatValue();
                    dailyTrainTicket.setYdz(countLeft);
                }
            }
        }

        return totalMoney;
    }

    /**
     * 查询前面有几个人在排队
     */
    public Integer queryLineCount(Long id) {
        ConfirmOrder confirmOrder = confirmOrderMapper.selectByPrimaryKey(id);
        ConfirmOrderStatusEnum statusEnum = EnumUtil.getBy(ConfirmOrderStatusEnum::getCode, confirmOrder.getStatus());
        int result = switch (statusEnum) {
            case PENDING -> 0; // 待支付
            case SUCCESS -> -1; // 成功
            case FAILURE -> -2; // 失败
            case EMPTY -> -3; // 无票
            case CANCEL -> -4; // 取消
            case INIT -> 999; // 需要查表得到实际排队数量
        };

        if (result == 999) {
            // 排在第几位，下面的写法：where a=1 and (b=1 or c=1) 等价于 where (a=1 and b=1) or (a=1 and c=1)
            ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
            confirmOrderExample.or().andDateEqualTo(confirmOrder.getDate())
                    .andTrainCodeEqualTo(confirmOrder.getTrainCode())
                    .andCreateTimeLessThan(confirmOrder.getCreateTime())
                    .andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
            confirmOrderExample.or().andDateEqualTo(confirmOrder.getDate())
                    .andTrainCodeEqualTo(confirmOrder.getTrainCode())
                    .andCreateTimeLessThan(confirmOrder.getCreateTime())
                    .andStatusEqualTo(ConfirmOrderStatusEnum.PENDING.getCode());
            return Math.toIntExact(confirmOrderMapper.countByExample(confirmOrderExample));
        } else {
            return result;
        }
    }



}
