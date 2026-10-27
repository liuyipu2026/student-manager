package com.example.student_web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student_web.bean.Student;
import com.example.student_web.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class StudentService {

    @Autowired
    private StudentMapper studentMapper;

    // 注入 Redis 工具类
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 统一缓存 key
    private static final String CACHE_KEY = "students:all";

    /**
     * 查询所有学生（带 Redis 缓存）
     */
    public List<Student> listAll() {
        // 1. 先查 Redis
        List<Student> cachedList = (List<Student>) redisTemplate.opsForValue().get(CACHE_KEY);
        if (cachedList != null) {
            System.out.println("【Redis缓存命中】直接从 Redis 返回数据");
            return cachedList;
        }

        // 2. 缓存没有，查数据库
        System.out.println("【Redis缓存未命中】查询数据库...");
        List<Student> list = studentMapper.selectList(null);

        // 3. 存入 Redis，设置 30 分钟过期
        redisTemplate.opsForValue().set(CACHE_KEY, list, 30, TimeUnit.MINUTES);

        return list;
    }

    public Student getById(String id) {
        return studentMapper.selectById(id);
    }

    public boolean add(Student student) {
        Student exist = studentMapper.selectById(student.getId());
        if (exist != null) {
            return false;
        }
        boolean success = studentMapper.insert(student) > 0;
        if (success) {
            clearCache(); // 数据变了，清空缓存
        }
        return success;
    }

    public boolean update(Student student) {
        boolean success = studentMapper.updateById(student) > 0;
        if (success) {
            clearCache();
        }
        return success;
    }

    public boolean delete(String id) {
        boolean success = studentMapper.deleteById(id) > 0;
        if (success) {
            clearCache();
        }
        return success;
    }

    public boolean batchDelete(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        int rows = studentMapper.deleteBatchIds(ids);
        if (rows > 0) {
            clearCache();
        }
        return rows > 0;
    }

    public List<Student> searchByName(String name) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.like("name", name);
        return studentMapper.selectList(wrapper);
    }

    public List<Student> searchByAge(int min, int max) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.between("age", min, max);
        return studentMapper.selectList(wrapper);
    }

    public List<Student> queryWithConditions(String gender, Integer minAge, Integer maxAge, String name) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        if (gender != null && !gender.isEmpty()) wrapper.eq("gender", gender);
        if (minAge != null) wrapper.ge("age", minAge);
        if (maxAge != null) wrapper.le("age", maxAge);
        if (name != null && !name.isEmpty()) wrapper.like("name", name);
        return studentMapper.selectList(wrapper);
    }

    public Page<Student> pageQuery(int pageNum, int pageSize, String gender, String name,
                                   Integer minAge, Integer maxAge) {
        Page<Student> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        if (gender != null && !gender.isEmpty()) wrapper.eq("gender", gender);
        if (name != null && !name.isEmpty()) wrapper.like("name", name);
        if (minAge != null) wrapper.ge("age", minAge);
        if (maxAge != null) wrapper.le("age", maxAge);
        return studentMapper.selectPage(page, wrapper);
    }

    /**
     * 按性别统计人数
     */
    public List<Map<String, Object>> countByGender() {
        return studentMapper.countByGender();
    }

    /**
     * 获取平均年龄
     */
    public Double getAvgAge() {
        return studentMapper.AvgAge();
    }

    /**
     * Excel 批量导入
     */
    public int importStudents(List<Student> studentList) {
        if (studentList == null || studentList.isEmpty()) {
            return 0;
        }
        int successCount = 0;
        for (Student s : studentList) {
            Student exist = studentMapper.selectById(s.getId());
            if (exist == null) {
                studentMapper.insert(s);
                successCount++;
            }
        }
        if (successCount > 0) {
            clearCache();
        }
        return successCount;
    }

    /**
     * 生成随机测试数据
     */
    public int generateRandomStudents(int count) {
        List<Student> list = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            Student s = new Student();
            s.setId("S" + System.currentTimeMillis() + random.nextInt(1000));
            s.setName("学生" + random.nextInt(10000));
            s.setAge(random.nextInt(8) + 18); // 18-25
            s.setGender(random.nextBoolean() ? "男" : "女");
            list.add(s);
        }

        int successCount = 0;
        for (Student s : list) {
            successCount += studentMapper.insert(s);
        }

        if (successCount > 0) {
            clearCache();
        }
        return successCount;
    }

    /**
     * 统一清理缓存的方法
     */
    private void clearCache() {
        redisTemplate.delete(CACHE_KEY);
        System.out.println("【Redis缓存已清空】");
    }
}