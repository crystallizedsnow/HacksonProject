package com.Hackason.BankAccountProject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.Hackason.BankAccountProject.Mapper")
@SpringBootApplication
public class HackasonProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackasonProjectApplication.class, args);
	}

}
