package com.study.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.util.JWTutil;
import com.study.train.common.util.SnowUtil;
import com.study.train.member.domain.Member;
import com.study.train.member.domain.MemberExample;
import com.study.train.member.dto.MemberLoginDTO;
import com.study.train.member.dto.MemberRegisterDTO;
import com.study.train.member.dto.MemberSendCodeDTO;
import com.study.train.member.mapper.MemberMapper;
import com.study.train.member.resp.MemberLoginResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class MemberService {

    private static final Logger LOG = LoggerFactory.getLogger(MemberService.class);


    @Resource
    private MemberMapper memberMapper;

    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public Long register(MemberRegisterDTO memberRegisterDTO) {
        String mobile = memberRegisterDTO.getMobile();
        Member members = selectByMobile(mobile);

        if (!ObjectUtil.isNull(members)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }

        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }

    public String sendCode(MemberSendCodeDTO memberSendCodeDTO) {
        String mobile = memberSendCodeDTO.getMobile();
        Member members = selectByMobile(mobile);

        if (ObjectUtil.isNull(members)) {
            LOG.info("手机号不存在，插入一条数据");
            Member member = new Member();
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        } else {
            LOG.info("手机号存在，不插入数据");
        }

        //生成验证码
        String code = RandomUtil.randomString(4);
        LOG.info("生成短信验证码：{}", code);

        //保存短信记录表；手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间

        //对接短信通道，发送短信

        return code;
    }

    public MemberLoginResp login(MemberLoginDTO memberLoginDTO) {
        String mobile = memberLoginDTO.getMobile();
        String code = memberLoginDTO.getCode();
        Member member = selectByMobile(mobile);

        if (ObjectUtil.isNull(member)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }

        //检验手机验证码
        if (!"8888".equals(code)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }

        MemberLoginResp memberLoginResp = BeanUtil.copyProperties(member, MemberLoginResp.class);



        String token =  JWTutil.createToken(memberLoginResp.getId(), memberLoginResp.getMobile());
        memberLoginResp.setJWTtoken(token);


        return memberLoginResp;

    }

    private Member selectByMobile(String mobile) {
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> members = memberMapper.selectByExample(memberExample);

        if (CollectionUtils.isEmpty(members)) {
            return null;
        } else {
            return members.get(0);
        }
    }

}
