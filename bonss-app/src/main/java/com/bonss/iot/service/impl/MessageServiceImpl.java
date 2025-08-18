package com.bonss.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonss.common.enums.MessageStatus;
import com.bonss.common.exception.ServiceException;
import com.bonss.iot.domain.Message;
import com.bonss.iot.domain.MessageType;
import com.bonss.iot.domain.vo.CommonMessageVo;
import com.bonss.iot.domain.vo.TypeMessageVo;
import com.bonss.iot.service.IMessageService;
import com.bonss.iot.mapper.MessageMapper;
import com.bonss.iot.mapper.MessageTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageTypeMapper messageTypeMapper;

    @Override
    public List<CommonMessageVo> listAll(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiverId, userId);
        List<Message> messages = messageMapper.selectList(queryWrapper);

        return messages.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean readAll(Long userId,int type) {
        if (userId != null) {
            LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Message::getReceiverId, userId);
            queryWrapper.eq(Message::getMessageType, type);
            List<Message> messages = messageMapper.selectList(queryWrapper);
            for (Message message : messages) {
                message.setStatus(MessageStatus.READ.getCode());
                message.setReadTime(new Date());
                messageMapper.updateById(message);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean clear(Long userId,int type) {
        if (userId != null) {
            LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Message::getReceiverId, userId);
            queryWrapper.eq(Message::getMessageType, type);
            List<Message> messages = messageMapper.selectList(queryWrapper);
            for (Message message : messages) {
                messageMapper.deleteById(message);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<CommonMessageVo> listByType(Long userId, int type) {
        if (userId == null) {
            return Collections.emptyList();
        }
        if (type == 0) {
            return listAll(userId);
        }
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiverId, userId);
        queryWrapper.eq(Message::getMessageType, type);
        List<Message> messages = messageMapper.selectList(queryWrapper);
        return messages.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeMessageVo> typeData(Long userId) {
        if(userId == null){
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getReceiverId, userId);
        List<Message> messages = messageMapper.selectList(queryWrapper);
        List<CommonMessageVo> messageVos = messages.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        // 按 type 分组
        Map<String, List<CommonMessageVo>> groupedByType = messageVos.stream()
                .collect(Collectors.groupingBy(CommonMessageVo::getMessageType));

        // 构造 TypeMessageVo 列表
        return groupedByType.entrySet().stream()
                .map(entry -> new TypeMessageVo(entry.getKey(), entry.getValue().size(), entry.getValue().get(0).getImgUrl(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private CommonMessageVo convertToVo(Message message) {

        MessageType tyName = getTyName(message.getMessageType());
        MessageType subType = getTyName(message.getMessageSubType());

        return CommonMessageVo.builder()
                .id(message.getId())
                .messageType(tyName.getName())
                .messageSubType(subType.getName())
                .title(message.getTitle())
                .content(message.getContent())
                .imgUrl(tyName.getImgUrl())
                .senderType(message.getSenderType())
                .receiverId(message.getReceiverId())
                .receiverType(message.getReceiverType())
                .status(message.getStatus())
                .pushTime(message.getPushTime())
                .readTime(message.getReadTime())
                .createTime(message.getCreateTime())
                .updateTime(message.getUpdateTime())
                .build();
    }

    private MessageType getTyName(int messageType){
        LambdaQueryWrapper<MessageType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageType::getCode, messageType);
        if(messageTypeMapper.selectOne(queryWrapper)==null){
            throw new ServiceException("没有该类型消息");
        }
        return messageTypeMapper.selectOne(queryWrapper);
    }
}
