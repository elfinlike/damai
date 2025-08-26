package com.bonss.iot.service;

import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.system.domain.Manual;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IManualService {
    void addManuals(Integer productId, List<MultipartFile> multipartFiles,String prefix);


    void deleteManuals(Integer productId, List<Long> ids,String profileName);

    TableDataInfo listPage(Integer productId, PageQuery query);
}
