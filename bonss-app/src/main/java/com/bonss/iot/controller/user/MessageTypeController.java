package com.bonss.iot.controller.user;

import com.bonss.common.core.domain.AjaxResult;
import com.bonss.iot.domain.vo.MessageTypeVo;
import com.bonss.iot.domain.MessageType;
import com.bonss.iot.service.IMessageTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/message/type")
public class MessageTypeController {
    @Autowired
    private IMessageTypeService messageTypeService;

    /**
     * 获取消息类型树
     * @return
     */
    @GetMapping("/listTree")
    public AjaxResult listTree() {
        List<MessageTypeVo> list = messageTypeService.listTree();
        return AjaxResult.success(list);
    }

    /**
     * 新增消息类型
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody MessageType messageType) {
        boolean result = messageTypeService.addMessageType(messageType);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 修改消息类型
     */
    @PostMapping("/update")
    public AjaxResult update(@RequestBody MessageType messageType) {
        boolean result = messageTypeService.updateMessageType(messageType);
        return result ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 删除消息类型
     */
    @DeleteMapping("/delete")
    public AjaxResult delete(Long id){
        boolean result = messageTypeService.removeMessageType(id);
        return result ? AjaxResult.success() : AjaxResult.error();
    }


}
