package com.bonss.iot.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyVo {
    /**
     * 二维码
     */
    private String qrCode;

    /**
     * 家庭ID
     */
    private Long familyId;
}

