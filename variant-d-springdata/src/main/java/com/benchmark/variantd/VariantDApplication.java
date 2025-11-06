package com.benchmark.variantd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.benchmark.model")
@EnableJpaRepositories
public class VariantDApplication {

    public static void main(String[] args) {
        SpringApplication.run(VariantDApplication.class, args);
    }
}
