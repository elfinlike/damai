package com.bonss.iot.controller.user;

import com.bonss.common.annotation.Log;
import com.bonss.common.core.controller.BaseController;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.enums.BusinessType;
import com.bonss.iot.domain.vo.CommonMessageVo;
import com.bonss.iot.domain.vo.TypeMessageVo;
import com.bonss.iot.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/app/message")
public class MessageController extends BaseController {

    @Autowired
    private IMessageService messageService;

    /**
     * 分页获取对应用户的消息
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        List<CommonMessageVo> list = messageService.listAll(getUserId());
        return getDataTable(list);
    }

    /**
     * 全部已读(只对选择类别的的消息进行操作)
     */
    @GetMapping("/readAll")
    @Log(title = "消息", businessType = BusinessType.UPDATE)
    public AjaxResult readAll(int type) {
        return messageService.readAll(getUserId(),type) ? success() : error();
    }

    /**
     * 清空消息(只对选择类别的的消息进行操作)
     */
    @DeleteMapping("/clear")
    @Log(title = "消息", businessType = BusinessType.DELETE)
    public AjaxResult clear(int type) {
        return messageService.clear(getUserId(), type) ? success() : error();
    }

    /**
     * 分类获取消息
     */
    @GetMapping("/listByType")
    public TableDataInfo listByType(int type) {
        startPage();
        List<CommonMessageVo> list = messageService.listByType(getUserId(), type);
        return getDataTable(list);
    }

    /**
     * 分类数据
     */
    @GetMapping("/typeData")
    public AjaxResult typeData() {
        List<TypeMessageVo> list = messageService.typeData(getUserId());
        return AjaxResult.success(list);
    }
}

