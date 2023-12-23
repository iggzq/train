package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.common.util.SnowUtil;
import com.study.train.business.domain.ConfirmOrder;
import com.study.train.business.domain.ConfirmOrderExample;
import com.study.train.common.resp.PageResp;
import com.study.train.business.dto.ConfirmOrderQueryDTO;
import com.study.train.business.dto.ConfirmOrderSaveDTO;
import com.study.train.business.mapper.ConfirmOrderMapper;
import com.study.train.business.resp.ConfirmOrderQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    ConfirmOrderMapper confirmOrderMapper;

    public void save(ConfirmOrderSaveDTO confirmOrderSaveDTO) {
        DateTime now = new DateTime();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(confirmOrderSaveDTO, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        }else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }

    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryDTO confirmOrderQueryDTO) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
        PageHelper.startPage(confirmOrderQueryDTO.getPage(), confirmOrderQueryDTO.getSize());
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

    public void delete(Long id){
        confirmOrderMapper.deleteByPrimaryKey(id);
    }
}
