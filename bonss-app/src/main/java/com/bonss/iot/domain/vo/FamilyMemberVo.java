package com.bonss.iot.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyMemberVo {
    private Long userId;
    private String phone;
    private String avatar;
    private String nickName;
    private String userName;
    private String sex;
    private String FamilyRole;
    private Date joinTime;
    private Integer status;
}
