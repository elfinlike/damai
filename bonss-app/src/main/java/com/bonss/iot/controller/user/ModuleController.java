package com.bonss.iot.controller.user;

import com.bonss.common.annotation.Anonymous;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.iot.service.IModuleService;
import com.bonss.system.domain.SysModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 模块操作
 *
 * @author ywangjr
 */

@RestController
@RequestMapping("/module")
public class ModuleController {

    @Autowired
    private IModuleService moduleService;

    /**
     * 根据栏目获取所有的模块信息, 微信小程序端用
     */
    @Anonymous
    @GetMapping("/all/{columnId}")
    public AjaxResult selectAllModule(@PathVariable Long columnId) {
        return AjaxResult.success(moduleService.selectAllModule(columnId));
    }

    /**
     * 获取所有的模块信息
     */
    @GetMapping("/list")
    public AjaxResult selectListModule() {
        return AjaxResult.success(moduleService.listAll());
    }

    /**
     * 修改模块信息
     */
    @PutMapping("")
    public AjaxResult modifyModule(SysModule param, @RequestParam(value = "file", required = false) MultipartFile file) {
        moduleService.updateModule(param, file);
        return AjaxResult.success();
    }

    /**
     * 删除模块
     */
    @DeleteMapping("/{ids}")
    public AjaxResult deleteModule(@PathVariable Long[] ids) {
        moduleService.deleteModule(ids);
        return AjaxResult.success();
    }

    /**
     * 新增模块
     */
    @PostMapping("")
    public AjaxResult addModule(SysModule param, @RequestParam(value = "file") MultipartFile file) {
        moduleService.addModule(param, file);
        return AjaxResult.success();
    }

    /**
     * 根据模块id查询模块信息
     */
    @GetMapping("/{id}")
    public AjaxResult getModule(@PathVariable Long id) {
        return AjaxResult.success(moduleService.getById(id));
    }

    /**
     * 根据栏目ID查询下面所有的活动
     */
    @Anonymous
    @GetMapping("/byColumnId")
    public AjaxResult getModuleByColumnId(Long columnId) {
        return AjaxResult.success(moduleService.selectModuleByColumnId(columnId));
    }
}
