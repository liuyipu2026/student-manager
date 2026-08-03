package com.example.student_web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.student_web.bean.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    // 空着就行，BaseMapper 已经提供了增删改查方法
}