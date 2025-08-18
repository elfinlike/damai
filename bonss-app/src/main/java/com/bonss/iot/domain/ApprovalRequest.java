package com.bonss.iot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// 审批申请内容
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalRequest {
    private Long familyId;        // 家庭ID
    private String familyName;      // 家庭名称
    private Long applicantId;     // 申请人ID
    private String applicantName;   // 申请人姓名
    private String title; // 申请标题
    private String content;  // 申请内容
    private Date createTime;      // 申请时间
}
