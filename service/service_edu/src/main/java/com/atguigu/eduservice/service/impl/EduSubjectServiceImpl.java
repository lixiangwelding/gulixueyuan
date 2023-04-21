package com.atguigu.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.SubjectData;
import com.atguigu.eduservice.entity.subject.OneSubject;
import com.atguigu.eduservice.entity.subject.TwoSubject;
import com.atguigu.eduservice.listener.SubjectExcelListener;
import com.atguigu.eduservice.mapper.EduSubjectMapper;
import com.atguigu.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-04-20
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    @Override
    public void saveSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
            //文件输入流
            InputStream in = file.getInputStream();
            //调用方法进行读取
            EasyExcel.read(in, SubjectData.class,new SubjectExcelListener(subjectService)).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OneSubject> getAllOneTwoSubject() {
        //最终要得到的列表
        ArrayList<OneSubject> finalOneTwoTSubject = new ArrayList<>();

        //查询所有一级标题
        LambdaQueryWrapper<EduSubject> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EduSubject::getParentId,"0");
        List<EduSubject> oneSubjectList = baseMapper.selectList(lambdaQueryWrapper);

        //查询所有二级标题
        LambdaQueryWrapper<EduSubject> lambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper2.ne(EduSubject::getParentId,"0");
        List<EduSubject> twoSubjectList = baseMapper.selectList(lambdaQueryWrapper2);

        for (EduSubject eduSubject : oneSubjectList) {
           OneSubject oneSubject = new OneSubject();
            BeanUtils.copyProperties(eduSubject,oneSubject);
            finalOneTwoTSubject.add(oneSubject);

            List<TwoSubject> finalTwoSubjectList = new ArrayList<>();
            for (EduSubject eduSubject2 : twoSubjectList) {
                if(eduSubject2.getParentId().equals(oneSubject.getId())){
                    TwoSubject twoSubject = new TwoSubject();
                    BeanUtils.copyProperties(eduSubject2,twoSubject);
                    finalTwoSubjectList.add(twoSubject);
                }
            }
            //把一级下面的所有二级分类放到一级分类里面
            oneSubject.setSubjectChildren(finalTwoSubjectList);
        }


        return finalOneTwoTSubject;
    }

}
