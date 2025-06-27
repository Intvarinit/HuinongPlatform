package com.zhang.huinongplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zhang.huinongplatform.mapper")
public class HuiNongPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuiNongPlatformApplication.class, args);
    }

}
