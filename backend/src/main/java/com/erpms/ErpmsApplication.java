package com.erpms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ErpmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ErpmsApplication.class, args);
    }
}