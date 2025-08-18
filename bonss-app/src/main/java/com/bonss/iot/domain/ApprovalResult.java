package com.bonss.iot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// 审批结果内容
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalResult {
    private Long familyId;        // 家庭ID
    private Integer approved;       // 是否通过
    private String reason;          // 拒绝原因（可选）
    private String approverName;    // 审批人姓名
    private String content; // 审批内容
    private Date approveTime;     // 审批时间
}
