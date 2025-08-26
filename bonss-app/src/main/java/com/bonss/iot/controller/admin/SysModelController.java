package com.bonss.iot.controller.admin;


import com.bonss.common.annotation.Log;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.enums.BusinessType;
import com.bonss.common.enums.OperatorType;
import com.bonss.iot.service.IModelService;
import com.bonss.system.domain.DTO.ModelDTO;
import com.bonss.system.domain.ProductModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/model")
@Slf4j
public class SysModelController {

    @Autowired
    private IModelService modelService;

    /**
     * 添加型号
     * @param modelDTO 产品型号实体
     * @return 操作结果
     */
    @PostMapping
    @Log(title = "添加产品型号",businessType = BusinessType.INSERT,operatorType = OperatorType.MANAGE)
    public AjaxResult addModel(@RequestBody ModelDTO modelDTO){
        log.info(modelDTO.toString());
        modelService.addModel(modelDTO);
        return AjaxResult.success();
    }

    /**
     * 查看具体的型号信息
     * @param modelId 型号id
     * @return 型号信息
     */
    @GetMapping("/{modelId}")
    @Log(title = " 查看型号详情",businessType = BusinessType.GET,operatorType = OperatorType.MANAGE)
    public AjaxResult getDetail(@PathVariable Long modelId){
        return AjaxResult.success(modelService.getDetail(modelId));
    }

    /**
     * 分页查询
     */
    @GetMapping("/list")
    @Log(title = "分页查询型号",businessType = BusinessType.GET,operatorType = OperatorType.MANAGE)
    public TableDataInfo listModels(PageQuery pageQuery,ProductModel productModel){
        return modelService.listModels(pageQuery,productModel);
    }

    /**
     * 修改产品型号
     * @param modelDTO 产品型号实体
     * @return 返回操作结果
     */
    @PutMapping
    @Log(title = "修改产品型号",businessType = BusinessType.UPDATE,operatorType = OperatorType.MANAGE)
    public AjaxResult updateModel(@RequestBody ModelDTO modelDTO){
        modelService.updateModel(modelDTO);
        return AjaxResult.success();
    }

    /**
     * 批量删除型号
     * @param ids 型号id集合
     * @return 返回处理结果
     */
    @DeleteMapping("/{ids}")
    @Log(title = "删除产品型号",businessType = BusinessType.DELETE,operatorType = OperatorType.MANAGE)
    public AjaxResult deleteModels(@PathVariable List<Long> ids){
        modelService.deleteModels(ids);
        return AjaxResult.success();
    }

}
