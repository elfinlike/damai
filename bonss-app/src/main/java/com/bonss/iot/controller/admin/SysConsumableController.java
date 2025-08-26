package com.bonss.iot.controller.admin;


import com.bonss.common.annotation.Log;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.enums.BusinessType;
import com.bonss.common.enums.OperatorType;
import com.bonss.iot.service.IConsumableService;
import com.bonss.system.domain.ConsumableType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/product/{productId}")
@Slf4j
public class SysConsumableController {

    @Autowired
    private IConsumableService consumableService;


    /**
     * 获取产品耗材种类列表
     */
    @GetMapping("/consumables")
    public TableDataInfo list(@PathVariable Long productId, PageQuery pageQuery,ConsumableType consumableType){
        return consumableService.listConsumables(productId,pageQuery,consumableType);
    }


    /**
     * 给产品添加耗材
     * @param productId 产品id
     */
    @PostMapping("/consumable/bind")
    @Log(title = "产品添加耗材",businessType = BusinessType.INSERT,operatorType = OperatorType.MANAGE)
    public AjaxResult bind(@PathVariable Long productId ,@RequestBody ConsumableType consumableType){
        log.info(consumableType.toString());
        consumableService.bindConnection(productId,consumableType);
        return AjaxResult.success();
    }

    /**
     * 修改产品和耗材之间的属性
     * @param productId
     * @param consumableType
     */
    @PutMapping("/consumable/connection")
    @Log(title = "修改产品耗材",businessType = BusinessType.UPDATE,operatorType = OperatorType.MANAGE)
    public AjaxResult updateTime(@PathVariable Long productId,
                                 @RequestBody ConsumableType consumableType){
        consumableService.updateConsumables(productId,consumableType);
        return AjaxResult.success();
    }

    /**
     * 删除产品和耗材之间的绑定关系
     * @param productId
     * @param ids 耗材id
     * @return
     */
    @DeleteMapping("/consumable/connection/{ids}")
    public AjaxResult deleteConnection(@PathVariable Long productId,@PathVariable List<Long> ids){
        consumableService.deleteConsumables(productId,ids);
        return AjaxResult.success();
    }
}
