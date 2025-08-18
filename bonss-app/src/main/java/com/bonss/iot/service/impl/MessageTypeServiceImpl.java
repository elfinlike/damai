package com.bonss.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonss.common.exception.ServiceException;
import com.bonss.iot.domain.MessageType;
import com.bonss.iot.domain.vo.MessageTypeVo;
import com.bonss.iot.mapper.MessageTypeMapper;
import com.bonss.iot.service.IMessageTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageTypeServiceImpl extends ServiceImpl<MessageTypeMapper, MessageType> implements IMessageTypeService {
    @Autowired
    private MessageTypeMapper messageTypeMapper;


    @Override
    public List<MessageTypeVo> listTree() {
        // 获取所有消息类型数据
        List<MessageType> messageTypes = messageTypeMapper.selectList(null);

        // 构建 parentId 到子节点的映射
        Map<Integer, List<MessageType>> childrenMap = new HashMap<>();
        for (MessageType type : messageTypes) {
            childrenMap.computeIfAbsent(type.getParentCode(), k -> new ArrayList<>()).add(type);
        }

        // 查找根节点（parentCode == null 或者 parentCode == 0）
        List<MessageTypeVo> rootNodes = new ArrayList<>();
        for (MessageType type : messageTypes) {
            if (type.getParentCode() == null || type.getParentCode() == 0) {
                rootNodes.add(convertToVo(type, childrenMap, null));
            }
        }
        return rootNodes;
    }

    @Override
    public boolean addMessageType(MessageType messageType) {
        LambdaQueryWrapper<MessageType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageType::getCode, messageType.getCode());
        if (messageTypeMapper.selectOne(queryWrapper) == null) {
            return messageTypeMapper.insert(messageType) > 0;
        } else {
            throw new ServiceException("消息类型已存在");
        }
    }

    @Override
    public boolean updateMessageType(MessageType messageType) {
        LambdaQueryWrapper<MessageType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageType::getName, messageType.getName());
        if (messageTypeMapper.selectOne(queryWrapper) == null) {
            return messageTypeMapper.updateById(messageType) > 0;
        } else {
            throw new ServiceException("消息类型已存在");
        }
    }

    @Override
    public boolean removeMessageType(Long id) {
        // 获取当前节点
        MessageType currentType = messageTypeMapper.selectById(id);
        if (currentType == null) {
            return false; // 节点不存在
        }

        // 查询是否有子节点（即 parent_code = 当前节点的 code）
        Integer currentCode = currentType.getCode();
        List<MessageType> children = messageTypeMapper.selectList(new QueryWrapper<MessageType>().eq("parent_code", currentCode));

        if (children != null && !children.isEmpty()) {
            throw new ServiceException("当前节点有子节点，不允许删除");
        }

        // 无子节点，允许删除
        return messageTypeMapper.deleteById(id) > 0;
    }


    private MessageTypeVo convertToVo(MessageType type, Map<Integer, List<MessageType>> childrenMap, Integer parentCode) {
        MessageTypeVo vo = new MessageTypeVo();

        vo.setId(type.getId());
        vo.setParentName(type.getName());
        vo.setParentCode(parentCode);
        vo.setDescription(type.getDescription());
        vo.setChildren(new ArrayList<>());

        vo.setCreateBy(type.getCreateBy());
        vo.setCreateTime(type.getCreateTime());
        vo.setUpdateBy(type.getUpdateBy());
        vo.setUpdateTime(type.getUpdateTime());
        vo.setRemark(type.getRemark());

        List<MessageType> children = childrenMap.getOrDefault(type.getCode(), Collections.emptyList());
        for (MessageType child : children) {
            vo.getChildren().add(convertToVo(child, childrenMap, type.getCode()));
        }

        return vo;
    }


}
