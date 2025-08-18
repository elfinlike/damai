package com.bonss.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private Long id;
    private String nickName;
    private String phonenumber;
    private Integer gender;       // 0-未知，1-男，2-女
    private LocalDate birth;
    private Integer userType;     // 用户属性：0-app用户，1-后台用户
    private LocalDateTime createTime;
}
