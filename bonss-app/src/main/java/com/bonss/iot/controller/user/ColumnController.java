package com.bonss.iot.controller.user;

import com.bonss.common.annotation.Anonymous;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.iot.service.IColumnService;
import com.bonss.iot.param.SysColumnParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 栏目操作
 *
 * @author yanwenguang
 */
@RestController
@RequestMapping("/column")
public class ColumnController {
    @Autowired
    private IColumnService service;

    /**
     * 返回全部的栏目信息(非树状结构), 首页
     */
    @Anonymous
    @GetMapping("/all/index")
    public AjaxResult listAllWechatIndex(){
        return AjaxResult.success(service.listAllWechatLiveColumn());
    }

    /**
     * 返回全部的栏目信息(非树状结构), 云视频
     */
    @Anonymous
    @GetMapping("/all/video")
    public AjaxResult listAllWechatVideo(){
        return AjaxResult.success(service.listAllWechatVideoColumn());
    }

    /**
     * 处理修改栏目信息
     */
    @PutMapping("/modifyColumn")
    public AjaxResult modifyColumn(SysColumnParam sysColumnParam, @RequestParam(value = "file",required = false) MultipartFile file, @RequestParam(value = "productFile",required = false) MultipartFile productFile) {
        service.modify(sysColumnParam,file,productFile);
        return AjaxResult.success();
    }

    /**
     * 新增栏目
     */
    @PostMapping("/add")
    public AjaxResult addColumn(SysColumnParam sysColumnParam, @RequestParam(value = "productFile",  required = false) MultipartFile productFile, @RequestParam(value = "file") MultipartFile file) {
        service.addColumn(sysColumnParam,file,productFile);
        return AjaxResult.success();
    }

    /**
     * 删除栏目，已经分配直播的栏目无法检查
     */
    @DeleteMapping("/{ids}")
    public AjaxResult deleteColumn(@PathVariable Long[] ids) {
        service.delete(ids);
        return AjaxResult.success();
    }

    /**
     * 返回栏目的信息
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult listAllColumn() {
        return AjaxResult.success(service.listAllColumnInfo());
    }

    /**
     * 返回云视频栏目的信息
     */
    @Anonymous
    @GetMapping("/list/video")
    public AjaxResult listAllVideoColumn() {
        return AjaxResult.success(service.listAllVideoColumnInfo());
    }


    /**
     * 返回栏目指定id的栏目信息
     */
    @Anonymous
    @GetMapping("/select/{id}")
    public AjaxResult selectColumnById(@PathVariable Long id) {
        return AjaxResult.success(service.selectColumnById(id));
    }

}
