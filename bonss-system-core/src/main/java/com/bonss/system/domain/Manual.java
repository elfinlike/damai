package com.bonss.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@TableName("manual")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Manual {

    /**
     *  说明书ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 说明书新文件名（含路径）
     */
    private String fileName;

    /**
     * 说明书新文件名（不含路径）
     */
    private String newFileName;

    /**
     * 说明书文件原始名
     */
    private String originalName;

    /**
     * 说明书文件大小
     */
    private String fileSize;

    /**
     * 说明书的后缀格式
     */
    private String fileForm;

    /**
     * 说明书访问url
     */
    private String url;

    /**
     * 说明书上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 说明书由谁上传
     */
    private String uploadBy;

    /**
     * 当前说明书是否删除,0--未删除，2--删除
     */
    private String delFlag;

}
