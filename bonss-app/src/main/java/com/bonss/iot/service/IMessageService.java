package com.bonss.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonss.iot.domain.vo.CommonMessageVo;
import com.bonss.iot.domain.vo.TypeMessageVo;
import com.bonss.iot.domain.Message;

import java.util.List;

public interface IMessageService extends IService<Message> {
    List<CommonMessageVo> listAll(Long userId);

    boolean readAll(Long userId,int type);

    boolean clear(Long userId,int type);

    List<CommonMessageVo> listByType(Long userId, int type);

    List<TypeMessageVo> typeData(Long userId);
}
