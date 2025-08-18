package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统栏目和活动关联 sys_column_module
 *
 * @author bonss
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysColumnModule {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 栏目ID
     */
    private Long columnId;
    /**
     * 活动ID
     */
    private Long moduleId;

}
