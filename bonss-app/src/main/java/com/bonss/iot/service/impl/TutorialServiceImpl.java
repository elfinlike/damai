package com.bonss.iot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bonss.common.core.domain.PageQuery;
import com.bonss.common.core.page.TableDataInfo;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.SecurityUtils;
import com.bonss.system.domain.DTO.TutorialDTO;
import com.bonss.system.domain.Tutorial;
import com.bonss.system.mapper.TutorialMapper;
import com.bonss.iot.service.ITutorialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
public class TutorialServiceImpl implements ITutorialService {

    @Autowired
    private TutorialMapper tutorialMapper;

    /**
     * 分页查询图文教程
     * @param productId 产品id
     * @param pageQuery 分页实体
     * @return 返回结果
     */
    @Override
    public TableDataInfo list(Integer productId, PageQuery pageQuery) {
        Page<Tutorial> page = pageQuery.build();
        LambdaQueryWrapper<Tutorial> wrapper=new LambdaQueryWrapper<>();
        wrapper.like(pageQuery.getSearchValue()!=null,Tutorial::getTitle,pageQuery.getSearchValue())
                .eq(Tutorial::getDelFlag,"0");
        Page<Tutorial> tutorialPage = tutorialMapper.selectPage(page, wrapper);
        return new TableDataInfo(tutorialPage.getRecords(),tutorialPage.getTotal());
    }

    /**
     * 上传图文教程
     * @param productId 产品id
     * @param tutorialDTO 图文教程DTO实体类
     */
    @Override
    public void addTutorials(Integer productId, TutorialDTO tutorialDTO) {
        Tutorial tutorial=new Tutorial();
        BeanUtils.copyProperties(tutorialDTO,tutorial);
        tutorial.setProductId(productId);
        tutorial.setUpdateBy(SecurityUtils.getUsername());
        tutorial.setCreateBy(SecurityUtils.getUsername());
        int insert = tutorialMapper.insert(tutorial);
        if (insert<=0){
            log.error("上传图文教程到数据库失败");
            throw new ServiceException("图文教程添加失败");
        }
    }

    /**
     * 查询图文教程具体信息
     * @param productId 产品id
     * @param id 图文教程id
     */
    @Override
    public Tutorial getDetail(Long productId, Long id) {
        Tutorial tutorial = tutorialMapper.selectById(id);
        if (tutorial==null){
            log.error("查询不到对应的图文教程详细信息");
            throw new ServiceException("查询不到对应详细记录");
        }
        return tutorial;
    }


    /**
     *修改图文教程
     * @param id 图文教程id
     * @param tutorialDTO 图文教程实体类
     */
    @Override
    public void updateTutorials(Integer id, TutorialDTO tutorialDTO) {
        Tutorial tutorial=new Tutorial();
        BeanUtils.copyProperties(tutorialDTO,tutorial);
        tutorial.setId(id);
        tutorial.setUpdateBy(SecurityUtils.getUsername());
        tutorial.setUpdateTime(LocalDateTime.now());

        int update = tutorialMapper.updateById(tutorial);
        if (update<=0){
            log.error("数据库修改图文教程失败");
            throw new ServiceException("修改图文教程失败");
        }
    }


    /**
     * 批量删除图文教程
     * @param ids 图文教程的id集合
     */
    @Override
    public void deleteTutorials(List<Integer> ids) {
        UpdateWrapper<Tutorial> wrapper=new UpdateWrapper<>();
        wrapper.lambda()
                .in(Tutorial::getId,ids)
                .set(Tutorial::getDelFlag,"2");
        int update = tutorialMapper.update(null, wrapper);
        if (update<=0){
            log.error("批量删除图文教程失败");
            throw new ServiceException("批量删除图文教程失败");
        }
    }
}
