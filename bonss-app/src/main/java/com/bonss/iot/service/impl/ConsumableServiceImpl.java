package com.bonss.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.SecurityUtils;
import com.bonss.system.domain.ConsumableType;
import com.bonss.system.mapper.ConsumableMapper;
import com.bonss.iot.service.IConsumableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class ConsumableServiceImpl implements IConsumableService {

    @Autowired
    private ConsumableMapper consumableMapper;


    /**
     * 给产品绑定添加耗材
     *
     * @param productId      产品id
     * @param consumableType 耗材实体类
     */
    @Override
    public void bindConnection(Long productId, ConsumableType consumableType) {
        consumableType.setProductId(productId);
        int insert = consumableMapper.insert(consumableType);
        if (insert <= 0) {
            log.error("产品添加耗材失败");
            throw new ServiceException("添加耗材失败");
        }


    }

    /**
     * 获取产品下的所有耗材
     *
     * @param productId      产品id
     * @param pageQuery      分页查询参数
     * @param consumableType 耗材实体
     * @return 返回分页查询结果
     */
    @Override
    public TableDataInfo listConsumables(Long productId, PageQuery pageQuery, ConsumableType consumableType) {
        Page<ConsumableType> page = pageQuery.build();
        consumableType.setProductId(productId);
        LambdaQueryWrapper<ConsumableType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(consumableType.getName() != null, ConsumableType::getName, consumableType.getName())
                .eq(ConsumableType::getProductId, consumableType.getProductId())
                .eq(consumableType.getType() != null, ConsumableType::getType, consumableType.getType())
                .eq(consumableType.getSpec() != null, ConsumableType::getSpec, consumableType.getSpec())
                .eq(ConsumableType::getDelFlag, "0");

        Page<ConsumableType> consumableTypePage = consumableMapper.selectPage(page, wrapper);
        return new TableDataInfo(consumableTypePage.getRecords(), consumableTypePage.getTotal());
    }


    /**
     * 修改产品的耗材属性
     *
     * @param productId      产品id
     * @param consumableType 耗材实体
     */
    @Override
    public void updateConsumables(Long productId, ConsumableType consumableType) {
        consumableType.setProductId(productId);
        consumableType.setUpdateBy(SecurityUtils.getAdminUserName());
        int update = consumableMapper.updateById(consumableType);
        if (update <= 0) {
            log.error("修改产品耗材属性失败");
            throw new ServiceException("修改失败");
        }
    }

    @Override
    public void deleteConsumables(Long productId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            log.error("传入修改的id集合不能为空");
            throw new ServiceException("传入修改的id不能未空");
        }
        UpdateWrapper<ConsumableType> wrapper = new UpdateWrapper<>();
        wrapper.lambda().in(ConsumableType::getId, ids)
                .set(ConsumableType::getUpdateBy, SecurityUtils.getAdminUserName())
                .set(ConsumableType::getDelFlag, "2");
        int update = consumableMapper.update(null, wrapper);
        if (update <= 0) {
            log.error("删除产品耗材失败");
            throw new ServiceException("删除失败");
        }

    }
}
