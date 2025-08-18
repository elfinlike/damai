package com.bonss.iot.service;

import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.system.domain.DTO.TutorialDTO;
import com.bonss.system.domain.Tutorial;

import java.util.List;

public interface ITutorialService {
    TableDataInfo list(Integer productId, PageQuery pageQuery);

    void addTutorials(Integer productId, TutorialDTO tutorialDTO);

    Tutorial getDetail(Long productId, Long id);

    void updateTutorials(Integer id, TutorialDTO tutorialDTO);

    void deleteTutorials(List<Integer> ids);
}
