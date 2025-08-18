package com.bonss.system.domain.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TutorialDTO {

    /**
     * 图文教程的标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 图文教程的内容
     */
    @NotBlank(message ="内容不能为空")
    private String content;
}
