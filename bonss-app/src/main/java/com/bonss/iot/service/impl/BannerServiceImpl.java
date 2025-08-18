package com.bonss.iot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.bean.BeanUtils;
import com.bonss.common.utils.file.ImageUtils;
import com.bonss.iot.mapper.SysBannerMapper;
import com.bonss.iot.param.SysBannerParam;
import com.bonss.iot.param.SysBannerSpeed;
import com.bonss.iot.service.IBannerService;
import com.bonss.system.domain.SysBanner;
import com.bonss.system.domain.vo.BaseConfigVo;
import com.bonss.system.service.ICommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class BannerServiceImpl extends ServiceImpl<SysBannerMapper, SysBanner> implements IBannerService {

    @Autowired
    private SysBannerMapper bannerMapper;

    @Autowired
    private ICommonService commonService;

    @Autowired
    private ImageUtils imageUtils;

    /**
     * 新增轮播图 最大数量为5
     *
     * @param sysBannerParam 接收新建banner的相关参数对象
     * @return 影响数据库条数
     */
    @Override
    @Transactional
    public void addBanner(SysBannerParam sysBannerParam, MultipartFile file) {
        LambdaQueryWrapper<SysBanner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysBanner::getType, sysBannerParam.getType());
        List<SysBanner> sysBanners = bannerMapper.selectList(queryWrapper);
        if (sysBanners.size() >= 5) {
            throw new ServiceException("最多存在5个同项目的轮播图");
        }
        boolean existsByName = bannerMapper.exists(new LambdaQueryWrapper<SysBanner>().eq(SysBanner::getImgName, sysBannerParam.getImgName()));
        boolean existsByType = bannerMapper.exists(new LambdaQueryWrapper<SysBanner>().eq(SysBanner::getType, sysBannerParam.getType()));

        if (existsByName && existsByType) {
            throw new ServiceException(sysBannerParam.getImgName() + "轮播图已存在");
        }

        SysBanner sysBanner = new SysBanner();

        BeanUtils.copyProperties(sysBannerParam, sysBanner);
//        sysBanner.setImgUrl(imageUtils.transformWebpUpload(file));
//        sysBanner.setUpdateTime(new Date());
//        if(sysBannerParam.getType().equals(BannerType.LIVE.getCode())){
//            sysBanner.setBookId(null);
//            sysBanner.setBookName(null);
//        }
//        if (sysBannerParam.getType().equals(BannerType.DBMP.getCode())){
//            sysBanner.setLiveId(null);
//            sysBanner.setLiveTitle(null);
//        }
        bannerMapper.insert(sysBanner);
    }


    /**
     * 批量删除轮播图
     *
     * @param ids 待删除轮播记录id数组
     * @return 数据库影响条数
     */
    @Override
    public void deleteBanner(Long[] ids) {
        for (Long id : ids) {
            if (bannerMapper.selectCount(null) == 1) {
                throw new ServiceException("至少需要保留一张轮播图");
            }
            SysBanner sysBanner = bannerMapper.selectById(id);
//            FileUploadUtils.deleteFile(sysBanner.getImgUrl());
            bannerMapper.deleteById(id);
        }
    }

    /**
     * 处理更新轮播图信息
     *
     * @param sysBannerParam 接收更新数据对象
     * @param file           图片文件接收对象
     * @return
     */
    @Override
    @Transactional
    public void updateBanner(SysBannerParam sysBannerParam, MultipartFile file) {
        LambdaQueryWrapper<SysBanner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(SysBanner::getBannerId, sysBannerParam.getBannerId());
        queryWrapper.eq(SysBanner::getImgName, sysBannerParam.getImgName());
        if (bannerMapper.exists(queryWrapper)) {
            throw new ServiceException("轮播图名称:" + sysBannerParam.getImgName() + "已存在");
        }
        SysBanner sysBanner = bannerMapper.selectById(sysBannerParam.getBannerId());
        SysBanner sysBannerUpdate = BeanUtil.copyProperties(sysBannerParam, SysBanner.class);
        if (null != file) {
//            FileUploadUtils.deleteFile(sysBanner.getImgUrl());
//            sysBannerUpdate.setImgUrl(imageUtils.transformWebpUpload(file));
        }
//        sysBannerUpdate.setUpdateBy(SecurityUtils.getUsername());
//        sysBannerUpdate.setUpdateTime(new Date());
        bannerMapper.updateById(sysBannerUpdate);
    }

    @Override
    public void handleUpdateBannerConfig(SysBannerSpeed sysBannerSpeed) {
        BaseConfigVo config = commonService.getConfig();
        config.setDuration(sysBannerSpeed.getDuration());
        config.setSpeed(sysBannerSpeed.getSpeed());
        commonService.updateBannerConfig(config);
    }

    @Override
    public SysBannerSpeed handleGetBannerSpeed() {
        BaseConfigVo config = commonService.getConfig();
        return new SysBannerSpeed(config.getSpeed(), config.getDuration());
    }

    /**
     * 获取直播小程序轮播图列表
     *
     * @return
     */
    @Override
    public List listLiveBanner() {
        LambdaQueryWrapper<SysBanner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(SysBanner::getSort);
//        queryWrapper.eq(SysBanner::getType, BannerType.LIVE.getCode());
        List<SysBanner> sysBanners = bannerMapper.selectList(queryWrapper);
        for (SysBanner sysBanner : sysBanners) {
//            SysLive sysLive = liveMapper.selectById(sysBanner.getLiveId());
//            if (null != sysLive) {
//                sysBanner.setLiveTitle(sysLive.getLiveTitle());
//            }
        }
        return sysBanners;
    }

    @Override
    public List<SysBanner> listAll() {
        LambdaQueryWrapper<SysBanner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(SysBanner::getSort);
        List<SysBanner> sysBanners = bannerMapper.selectList(queryWrapper);
        for (SysBanner sysBanner : sysBanners) {
//            SysLive sysLive = liveMapper.selectById(sysBanner.getLiveId());
//            SysBook sysBook = bookMapper.selectById(sysBanner.getBookId());
//            if (null != sysLive) {
//                sysBanner.setLiveTitle(sysLive.getLiveTitle());
//            }
//            if(null != sysBook){
//                sysBanner.setBookName(sysBook.getBookName());
//            }
        }
        return sysBanners;
    }

    @Override
    public List<SysBanner> listByType(String type) {
        LambdaQueryWrapper<SysBanner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysBanner::getType, type);
        List<SysBanner> sysBanners = bannerMapper.selectList(queryWrapper);
        for (SysBanner sysBanner : sysBanners) {
//            if (type.equals(BannerType.DBMP.getCode())) {
//                SysBook sysBook = bookMapper.selectById(sysBanner.getBookId());
//                if (null != sysBook) {
//                    sysBanner.setBookName(sysBook.getBookName());
//                }
//            }
//            if (type.equals(BannerType.LIVE.getCode())) {
//                SysLive sysLive = liveMapper.selectById(sysBanner.getLiveId());
//                if (null != sysLive) {
//                    sysBanner.setLiveTitle(sysLive.getLiveTitle());
//                }
//            }
        }
        return sysBanners;
    }
}
