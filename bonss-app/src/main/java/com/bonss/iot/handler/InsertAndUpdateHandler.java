package com.bonss.iot.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.bonss.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class InsertAndUpdateHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 使用严格模式填充，只有当字段有@TableField注解时才会填充
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());

        String username = "system";
        try {
            username = SecurityUtils.getUsername();
            if (username == null) {
                username = "system";
            }
        } catch (Exception e) {
            username = "system";
        }
        this.strictInsertFill(metaObject, "createBy", String.class, username);
        this.strictInsertFill(metaObject, "updateBy", String.class, username);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 使用严格模式填充
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());

        String username = "system";
        try {
            username = SecurityUtils.getUsername();
            if (username == null) {
                username = "system";
            }
        } catch (Exception e) {
            username = "system";
        }
        this.strictUpdateFill(metaObject, "updateBy", String.class, username);
    }
}
