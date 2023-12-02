package com.study.train.${module}.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.util.SnowUtil;
import com.study.train.${module}.domain.${Domain};
import com.study.train.${module}.domain.${Domain}Example;
import com.study.train.${module}.dto.PageDTO;
import com.study.train.${module}.dto.${Domain}QueryDTO;
import com.study.train.${module}.dto.${Domain}SaveDTO;
import com.study.train.${module}.mapper.${Domain}Mapper;
import com.study.train.${module}.resp.${Domain}QueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ${Domain}Service {

    private static final Logger LOG = LoggerFactory.getLogger(${Domain}Service.class);

    @Resource
    ${Domain}Mapper ${domain}Mapper;

    public void save(${Domain}SaveDTO ${domain}SaveDTO) {
        DateTime now = new DateTime();
        ${Domain} ${domain} = BeanUtil.copyProperties(${domain}SaveDTO, ${Domain}.class);
        if (ObjectUtil.isNull(${domain}.getId())) {
            ${domain}.setMemberId(LoginMemberContext.getId());
            ${domain}.setId(SnowUtil.getSnowflakeNextId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.insert(${domain});
        }else {
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.updateByPrimaryKey(${domain});
        }

    }

    public PageDTO<${Domain}QueryResp> queryList(${Domain}QueryDTO ${domain}QueryDTO) {
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();
        if (ObjectUtil.isNotNull(${domain}QueryDTO.getMemberId())) {
            criteria.andMemberIdEqualTo(${domain}QueryDTO.getMemberId());
        }
        PageHelper.startPage(${domain}QueryDTO.getPage(), ${domain}QueryDTO.getSize());
        List<${Domain}> ${domain}s = ${domain}Mapper.selectByExample(${domain}Example);

        PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}s);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<${Domain}QueryResp> ${domain}QueryResps = BeanUtil.copyToList(${domain}s, ${Domain}QueryResp.class);
        PageDTO<${Domain}QueryResp> pageDTO = new PageDTO<>();
        pageDTO.setTotal(pageInfo.getTotal());
        pageDTO.setData(${domain}QueryResps);

        return pageDTO;
    }

    public void delete(Long id){
        ${domain}Mapper.deleteByPrimaryKey(id);
    }
}
