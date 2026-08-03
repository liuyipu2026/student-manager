package com.example.student_web;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.example.student_web.mapper")
public class
StudentWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentWebApplication.class, args);
	}


}
