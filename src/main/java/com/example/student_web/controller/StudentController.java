package com.example.student_web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.student_web.bean.Student;
import com.example.student_web.common.Result;
import com.example.student_web.service.StudentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j   // 日志注解
@RestController
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 1. 查询所有学生
    @GetMapping("/students")
    public Result listStudents() {
        System.out.println("===== 测试日志输出 =====");
        log.info("查询所有学生");
        List<Student> list = studentService.listAll();
        log.info("查询到 {} 条学生数据", list.size());
        return Result.success(list);
    }

    // 2. 按学号查询单个学生
    @GetMapping("/students/{id}")
    public Result getStudentById(@PathVariable String id) {
        log.info("按学号查询单个学生：{}", id);
        Student student = studentService.getById(id);
        if (student == null) {
            log.warn("未找到学号为 {} 的学生", id);
            return Result.error(404, "未找到该学生");
        }
        log.info("查询成功：{}", student);
        return Result.success(student);
    }

    // 3. 添加学生
    @PostMapping("/students")
    public Result addStudent(@RequestBody Student student) {
        log.info("添加学生：{}", student);
        boolean success = studentService.add(student);
        if (success) {
            log.info("添加成功");
            return Result.success("添加成功");
        } else {
            log.warn("添加失败，学号 {} 已存在", student.getId());
            return Result.error(400, "学号已存在");
        }
    }

    // 4. 修改学生
    @PutMapping("/students")
    public Result updateStudent(@RequestBody Student student) {
        log.info("修改学生：{}", student);
        boolean success = studentService.update(student);
        if (success) {
            log.info("修改成功");
            return Result.success("修改成功");
        } else {
            log.warn("修改失败，未找到学号 {}", student.getId());
            return Result.error(404, "未找到该学号");
        }
    }

    // 5. 删除单个学生
    @DeleteMapping("/students/{id}")
    public Result deleteStudent(@PathVariable String id) {
        log.info("删除单个学生：{}", id);
        boolean success = studentService.delete(id);
        if (success) {
            log.info("删除成功");
            return Result.success("删除成功");
        } else {
            log.warn("删除失败，未找到学号 {}", id);
            return Result.error(404, "未找到该学号");
        }
    }

    // 6. 批量删除
    @DeleteMapping("/students/batch")
    public Result batchDeleteStudents(@RequestBody List<String> ids) {
        log.info("批量删除，学号列表：{}", ids);
        if (ids == null || ids.isEmpty()) {
            log.warn("批量删除，学号列表为空");
            return Result.error(400, "请至少选择一个学生");
        }
        boolean success = studentService.batchDelete(ids);
        if (success) {
            log.info("批量删除成功，删除 {} 条", ids.size());
            return Result.success("批量删除成功");
        } else {
            log.error("批量删除失败，学号列表：{}", ids);
            return Result.error(500, "批量删除失败");
        }
    }

    // 7. 按姓名模糊搜索
    @GetMapping("/search")
    public Result searchStudents(@RequestParam("name") String name) {
        log.info("按姓名模糊搜索：{}", name);
        List<Student> list = studentService.searchByName(name);
        log.info("搜索到 {} 条数据", list.size());
        return Result.success(list);
    }

    // 8. 按年龄范围搜索
    @GetMapping("/age")
    public Result searchByAge(@RequestParam("min") int minAge,
                              @RequestParam("max") int maxAge) {
        log.info("按年龄范围搜索：{} ~ {}", minAge, maxAge);
        List<Student> list = studentService.searchByAge(minAge, maxAge);
        log.info("搜索到 {} 条数据", list.size());
        return Result.success(list);
    }

    // 9. 组合查询
    @GetMapping("/query")
    public Result queryStudents(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String name) {
        log.info("组合查询：gender={}, minAge={}, maxAge={}, name={}", gender, minAge, maxAge, name);
        List<Student> list = studentService.queryWithConditions(gender, minAge, maxAge, name);
        log.info("查询到 {} 条数据", list.size());
        return Result.success(list);
    }

    // 10. 排序查询
    @GetMapping("/query/order")
    public Result queryWithOrder(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String direction) {

        log.info("排序查询：gender={}, minAge={}, maxAge={}, name={}, orderBy={}, direction={}",
                gender, minAge, maxAge, name, orderBy, direction);

        List<Student> list = studentService.queryWithConditions(gender, minAge, maxAge, name);
        if (orderBy != null && !orderBy.isEmpty()) {
            list.sort((a, b) -> {
                if ("age".equals(orderBy)) {
                    return "desc".equalsIgnoreCase(direction)
                            ? Integer.compare(b.getAge(), a.getAge())
                            : Integer.compare(a.getAge(), b.getAge());
                }
                return 0;
            });
        }
        log.info("排序完成，返回 {} 条数据", list.size());
        return Result.success(list);
    }

    // 11. 分页查询
    @GetMapping("/page")
    public Result pageStudents(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {

        log.info("分页查询：pageNum={}, pageSize={}, gender={}, name={}, minAge={}, maxAge={}",
                pageNum, pageSize, gender, name, minAge, maxAge);

        Page<Student> page = studentService.pageQuery(pageNum, pageSize, gender, name, minAge, maxAge);
        log.info("分页查询结果：总记录数={}, 当前页数据={}", page.getTotal(), page.getRecords().size());
        return Result.success(page);
    }

    // 12. 导出 Excel
    @GetMapping("/students/export")
    public void exportStudents(HttpServletResponse response) throws IOException {
        log.info("导出 Excel");
        List<Student> list = studentService.listAll();
        log.info("导出数据量：{} 条", list.size());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("学生列表");

        String[] headers = {"学号", "姓名", "年龄", "性别"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (Student s : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getId());
            row.createCell(1).setCellValue(s.getName());
            row.createCell(2).setCellValue(s.getAge());
            row.createCell(3).setCellValue(s.getGender() != null ? s.getGender() : "");
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("学生列表.xlsx", "UTF-8"));

        workbook.write(response.getOutputStream());
        workbook.close();
        log.info("Excel 导出完成");
    }

    // 13. 导入 Excel
    @PostMapping("/students/import")
    public Result importStudents(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("导入 Excel，文件为空");
            return Result.error(400, "请选择文件");
        }

        try {
            log.info("导入 Excel，文件名：{}，大小：{} KB", file.getOriginalFilename(), file.getSize() / 1024);

            List<Student> studentList = new ArrayList<>();
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            int rowCount = 0;
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                String id = getCellStringValue(row.getCell(0));
                String name = getCellStringValue(row.getCell(1));
                String ageStr = getCellStringValue(row.getCell(2));
                String gender = getCellStringValue(row.getCell(3));

                if (id == null || id.trim().isEmpty()) {
                    log.warn("跳过空行：第 {} 行", rowIndex + 1);
                    continue;
                }

                Student s = new Student();
                s.setId(id.trim());
                s.setName(name != null ? name.trim() : "");
                s.setAge(ageStr != null ? Integer.parseInt(ageStr.trim()) : 0);
                s.setGender(gender != null ? gender.trim() : "");
                studentList.add(s);
                rowCount++;
            }

            workbook.close();
            int count = studentService.importStudents(studentList);
            log.info("导入完成，成功导入 {} 条数据（共解析 {} 行）", count, rowCount);
            return Result.success("成功导入 " + count + " 条数据");

        } catch (Exception e) {
            log.error("导入 Excel 失败：", e);
            return Result.error(500, "导入失败：" + e.getMessage());
        }
    }
    //14.按性别统计人数
    @GetMapping("/students/stats/gender")
    public Result countByGender(){
        return Result.success(studentService.countByGender());
    }
    // 辅助方法：安全读取单元格字符串
    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return null;
        }
    }
    //平均年龄
    @GetMapping("/students/stats/avg-age")
    public Result getAvgAge(){
        return Result.success(studentService.getAvgAge());
    }

    @PostMapping("/students/generate")
    public Result generateRandomStudents(@RequestParam("count") int count){
        int successCount = studentService.generateRandomStudents(count);
        return Result.success("成功生成"+ successCount+"条学生数据");

    }
}