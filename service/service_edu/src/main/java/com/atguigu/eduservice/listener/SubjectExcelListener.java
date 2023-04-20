package com.atguigu.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.SubjectData;
import com.atguigu.eduservice.service.EduSubjectService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
//有参构造传入 subjectService
@AllArgsConstructor
public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {
    //因为SubjectExcelListener不交给spring进行管理，需要自己new，不能注入其他对象
    //不能实现数据库操作
    public EduSubjectService subjectService;

    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if (subjectData == null){
            throw new GuliException(20001,"文件数据为空");
        }
        //一行一行读取，每次读取有两个值，第一个值一级分类，第二个值二级分类
        EduSubject existOneSubject = this.existOneSubject(subjectService, subjectData.getOneSubjectName());
        if (existOneSubject == null){
            //没有相同的一级分类
            existOneSubject = new EduSubject();
            existOneSubject.setTitle(subjectData.getOneSubjectName());
            existOneSubject.setParentId("0");
            subjectService.save(existOneSubject);
        }
        String pid = existOneSubject.getId();
        EduSubject existTwoSubject = this.existTwoSubject(subjectService, subjectData.getTwoSubjectName(),pid);
        if (existTwoSubject == null){
            //没有相同的二级分类
            existOneSubject = new EduSubject();
            existOneSubject.setTitle(subjectData.getTwoSubjectName());
            existOneSubject.setParentId(pid);
            subjectService.save(existOneSubject);
        }
    }
    private EduSubject existOneSubject(EduSubjectService subjectService,String name){
        //判断一级分类是否重复
        LambdaQueryWrapper<EduSubject> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EduSubject::getTitle,name);
        lambdaQueryWrapper.eq(EduSubject::getParentId,"0");
        EduSubject oneSubject = subjectService.getOne(lambdaQueryWrapper);
        return oneSubject;
    }
    private EduSubject existTwoSubject(EduSubjectService subjectService,String name,String pid){
        //判断二级分类是否重复
        LambdaQueryWrapper<EduSubject> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EduSubject::getTitle,name);
        lambdaQueryWrapper.eq(EduSubject::getParentId,pid);
        EduSubject twoSubject = subjectService.getOne(lambdaQueryWrapper);
        return twoSubject;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
