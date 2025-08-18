package com.bonss.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonss.iot.param.SysBannerParam;
import com.bonss.iot.param.SysBannerSpeed;
import com.bonss.system.domain.SysBanner;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IBannerService extends IService<SysBanner> {

    void addBanner(SysBannerParam sysBannerParam, MultipartFile file);

    List<SysBanner> listLiveBanner();

    void deleteBanner(Long[] ids);

    void updateBanner(SysBannerParam sysBannerParam, MultipartFile multipartFile);

    void handleUpdateBannerConfig(SysBannerSpeed sysBannerSpeed);

    SysBannerSpeed handleGetBannerSpeed();

    List<SysBanner> listAll();

    List<SysBanner> listByType(String type);
}
