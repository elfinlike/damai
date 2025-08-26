package com.bonss.iot.controller.admin;


import com.bonss.common.annotation.Log;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.enums.BusinessType;
import com.bonss.common.enums.OperatorType;
import com.bonss.iot.service.ITutorialService;
import com.bonss.system.domain.DTO.TutorialDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/tutorial/{productId}")
public class SysTutorialController {

    @Autowired
    private ITutorialService tutorialService;

    /**
     * 分页查询图文教程
     *
     * @param productId 产品id
     * @return 返回分页查询结果
     */
    @GetMapping
    @Log(title = "查询图文教程", businessType = BusinessType.GET, operatorType = OperatorType.MANAGE)
    public TableDataInfo list(@PathVariable Integer productId,
                              PageQuery pageQuery) {
        return tutorialService.list(productId, pageQuery);
    }


    /**
     * @param productId 产品id
     * @param id        图文教程id
     * @return 返回具体结果
     */
    @GetMapping("/{id}")
    @Log(title = "查询图文教程", businessType = BusinessType.GET, operatorType = OperatorType.MANAGE)
    public AjaxResult detail(@PathVariable Long productId, @PathVariable Long id) {
        return AjaxResult.success(tutorialService.getDetail(productId, id));
    }

    /**
     * 新增图文教程
     *
     * @param productId   产品id
     * @param tutorialDTO 图文教程实体
     * @return 返回添加结果
     */
    @PostMapping
    @Log(title = "添加图文教程", businessType = BusinessType.INSERT, operatorType = OperatorType.MANAGE)
    public AjaxResult create(@PathVariable Integer productId, @RequestBody TutorialDTO tutorialDTO) {
        tutorialService.addTutorials(productId, tutorialDTO);
        return AjaxResult.success();
    }

    /**
     * 修改图文教程
     *
     * @param productId   产品id
     * @param id          图文教程id
     * @param tutorialDTO 图文教程实体传参
     * @return 返回处理结果
     */
    @PutMapping("/{id}")
    @Log(title = "修改图文教程", businessType = BusinessType.UPDATE, operatorType = OperatorType.MANAGE)
    public AjaxResult update(@PathVariable Integer productId,
                             @PathVariable Integer id,
                             @RequestBody TutorialDTO tutorialDTO) {
        tutorialService.updateTutorials(id, tutorialDTO);
        return AjaxResult.success();
    }


    /**
     * 删除图文教程
     *
     * @param productId 产品id
     * @param ids       需要删除的图文教程id集合
     * @return 返回处理结果
     */
    @DeleteMapping("/{ids}")
    @Log(title = "删除图文教程", businessType = BusinessType.DELETE, operatorType = OperatorType.MANAGE)
    public AjaxResult detele(@PathVariable Integer productId,
                             @PathVariable List<Integer> ids) {
        tutorialService.deleteTutorials(ids);
        return AjaxResult.success();
    }

}
