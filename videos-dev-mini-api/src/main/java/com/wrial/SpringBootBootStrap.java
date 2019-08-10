package com.wrial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = {"com.wrial", "org.n3r.idworker"})
@MapperScan("com.wrial.mapper")
public class SpringBootBootStrap {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootBootStrap.class, args);
    }
}
