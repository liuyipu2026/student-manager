//package com.example.student_web.dao;
//
//import com.example.student_web.bean.Student;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class JdbcCRUD {
//    private static final String URL = "jdbc:mysql://localhost:3306/student_db";
//    private static final String USER = "root";
//    private static final String PASSWORD = "2596@@@Liu";  // 确认密码正确
//
//    public static List<Student> queryAll() {
//        System.out.println("进入queryAll方法");
//        List<Student> list = new ArrayList<>();
//        String sql = "SELECT * FROM students";
//
//        try  (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {
//            System.out.println("开始查询数据库...");
//            while (rs.next()) {
//                Student s = new Student();
//                s.setId(rs.getString("id"));
//                s.setName(rs.getString("name"));
//                s.setAge(rs.getInt("age"));
//                s.setGender(rs.getString("gender"));
//                list.add(s);
//            }
//        } catch (Exception e) {
//            System.out.println("数据库查询出错：");
//            e.printStackTrace();
//        }
//        System.out.println("查询结果数量："+list.size());
//        return list;
//    }
//    public static List<Student> searchByName(String keyword) {
//        System.out.println("JdbcCRUD搜索方法被调用了，关键词：" + keyword);
//        List<Student> list = new ArrayList<>();
//        String sql = "SELECT * FROM students WHERE name LIKE ?";
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, "%" + keyword + "%");
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                Student s = new Student();
//                s.setId(rs.getString("id"));
//                s.setName(rs.getString("name"));
//                s.setAge(rs.getInt("age"));
//                s.setGender(rs.getString("gender"));
//                list.add(s);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//    public static List<Student> findByAgeRange(int minAge, int maxAge) {
//        List<Student> list = new ArrayList<>();
//        String sql = "SELECT * FROM students WHERE age BETWEEN ? AND ?";
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, minAge);
//            pstmt.setInt(2, maxAge);
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                Student s = new Student();
//                s.setId(rs.getString("id"));
//                s.setName(rs.getString("name"));
//                s.setAge(rs.getInt("age"));
//                s.setGender(rs.getString("gender"));
//                list.add(s);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//    // 按学号查询单个学生
//    public static Student findById(String id) {
//        String sql = "SELECT * FROM students WHERE id = ?";
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, id);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                Student s = new Student();
//                s.setId(rs.getString("id"));
//                s.setName(rs.getString("name"));
//                s.setAge(rs.getInt("age"));
//                s.setGender(rs.getString("gender"));
//                return s;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    // 添加学生
//    public static boolean addStudent(Student s) {
//        String sql = "INSERT INTO students (id, name, age,gender) VALUES (?, ?, ? ,?)";
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, s.getId());
//            pstmt.setString(2, s.getName());
//            pstmt.setInt(3, s.getAge());
//            pstmt.setString(4,s.getGender());
//            return pstmt.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    // 删除学生
//    public static boolean deleteById(String id) {
//        String sql = "DELETE FROM students WHERE id = ?";
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, id);
//            return pstmt.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    // 修改学生（更新姓名和年龄）
//    public static boolean updateStudent(Student s) {
//        String sql = "UPDATE students SET name = ?, age = ?,gender = ? WHERE id = ?";
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, s.getName());
//            pstmt.setInt(2, s.getAge());
//            pstmt.setString(3, s.getGender());
//            pstmt.setString(4,s.getId());
//            return pstmt.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}