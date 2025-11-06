package com.benchmark.variantc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.benchmark.model")
@EnableJpaRepositories
public class VariantCApplication {

    public static void main(String[] args) {
        SpringApplication.run(VariantCApplication.class, args);
    }
}
