package com.example.student_web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.student_web.bean.Student;
import com.example.student_web.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentController {

    @Autowired
    private StudentMapper studentMapper;

    // 1. 查询所有学生
    @GetMapping("/students")
    public List<Student> listStudents() {
        return studentMapper.selectList(null);
    }

    // 2. 按学号查询单个学生
    @GetMapping("/students/{id}")
    public Student getStudentById(@PathVariable String id) {
        return studentMapper.selectById(id);
    }

    // 3. 添加学生
    @PostMapping("/students")
    public String addStudent(@RequestBody Student student) {
        // 检查学号是否已存在
        Student exist = studentMapper.selectById(student.getId());
        if (exist != null) {
            return "添加失败，学号已存在";
        }
        int rows = studentMapper.insert(student);
        return rows > 0 ? "添加成功" : "添加失败";
    }

    // 4. 修改学生
    @PutMapping("/students")
    public String updateStudent(@RequestBody Student student) {
        int rows = studentMapper.updateById(student);
        return rows > 0 ? "修改成功" : "未找到该学号";
    }

    // 5. 删除学生
    @DeleteMapping("/students/{id}")
    public String deleteStudent(@PathVariable String id) {
        int rows = studentMapper.deleteById(id);
        return rows > 0 ? "删除成功" : "未找到该学号";
    }

    // 6. 按姓名模糊搜索
    @GetMapping("/search")
    public List<Student> searchStudents(@RequestParam("name") String name) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.like("name", name);
        return studentMapper.selectList(wrapper);
    }

    // 7. 按年龄范围搜索
    @GetMapping("/age")
    public List<Student> searchByAge(@RequestParam("min") int minAge,
                                     @RequestParam("max") int maxAge) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.between("age", minAge, maxAge);
        return studentMapper.selectList(wrapper);
    }
}