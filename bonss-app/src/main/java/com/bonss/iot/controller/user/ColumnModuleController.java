package com.bonss.iot.controller.user;

import com.bonss.common.core.domain.AjaxResult;
import com.bonss.iot.service.IColumnModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 活动栏目关系操作
 *
 * @author ywg
 */

@RestController
@RequestMapping("/relationColumModule")
public class ColumnModuleController {
    @Autowired
    private IColumnModuleService service;

    /**
     * 返回活动全部的栏目，通过活动ID
     */
    @GetMapping
    public AjaxResult listRelationColumModule(@RequestParam(required = true) Long moduleId) {
        return AjaxResult.success(service.listByModuleId(moduleId));
    }
}
