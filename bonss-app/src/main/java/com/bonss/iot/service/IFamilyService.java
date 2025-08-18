package com.bonss.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonss.iot.domain.FamilyMember;
import com.bonss.iot.domain.vo.FamilyTableVO;
import com.bonss.iot.domain.vo.FamilyVo;
import com.bonss.iot.domain.Family;

import java.util.List;

public interface IFamilyService extends IService<Family> {
    FamilyVo createFamily(String familyName);

    boolean dismissFamily(Long familyId);

    /**
     * 获取家庭列表
     */
    List<FamilyTableVO> selectFamilyList();

    /**
     * 用户获取家庭列表
     */
    List<FamilyTableVO> selectFamilyListByUserId();
}
