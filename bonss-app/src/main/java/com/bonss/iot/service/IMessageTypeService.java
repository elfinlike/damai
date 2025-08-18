package com.bonss.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonss.iot.domain.vo.MessageTypeVo;
import com.bonss.iot.domain.MessageType;

import java.util.List;

public interface IMessageTypeService extends IService<MessageType> {
    List<MessageTypeVo> listTree();

    boolean addMessageType(MessageType messageType);

    boolean updateMessageType(MessageType messageType);

    boolean removeMessageType(Long id);
}
