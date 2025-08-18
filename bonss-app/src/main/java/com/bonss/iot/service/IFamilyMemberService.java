package com.bonss.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bonss.iot.domain.vo.FamilyMemberVo;
import com.bonss.iot.domain.vo.FamilyVo;
import com.bonss.iot.domain.FamilyMember;

import java.util.List;

public interface IFamilyMemberService extends IService<FamilyMember> {
    List<FamilyMemberVo> getFamilyMemberList(Long familyId);

    boolean addFamilyMember(Long familyId, String phone);

    boolean removeFamilyMember(Long userId,Long familyId);

    FamilyVo shareFamily(Long familyId);

    boolean inviteFamilyMember(Long familyId, String phone);

    boolean quitFamily(Long familyId);

    boolean approveFamilyMember(Long familyId, Long userId, Integer status,String  reason);

    boolean joinFamily(Long familyId);
}
