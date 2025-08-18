package com.bonss.system.domain.DTO;


import com.bonss.system.domain.Manual;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManualDTO {
    /**
     * 产品ID
     */
    private Integer productId;

    /**
     * 说明书文件集合
     */
    private List<MultipartFile> multipartFiles;
}
