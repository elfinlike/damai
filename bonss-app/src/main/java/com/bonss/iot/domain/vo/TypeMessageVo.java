package com.bonss.iot.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeMessageVo {
    String type;
    int count;
    String imgUrl;
    List<CommonMessageVo> messageVos;
}
