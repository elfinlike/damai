package com.bonss.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bonss.iot.domain.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
