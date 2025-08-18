package com.bonss.system.service;


import com.bonss.system.domain.vo.BaseConfigVo;

public interface ICommonService {

    BaseConfigVo getConfig();

    BaseConfigVo updateBannerConfig(BaseConfigVo newConfig);
}
