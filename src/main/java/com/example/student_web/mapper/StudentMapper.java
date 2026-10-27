package com.example.student_web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.student_web.bean.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    // 空着就行，BaseMapper 已经提供了增删改查方法
    @Select("SELECT gender, COUNT(*) as count FROM students GROUP BY gender")
    List<Map<String, Object>> countByGender();
    @Select("SELECT AVG(age) FROM students")
    Double aveAge();

    Double AvgAge();
}
