package com.event.msalearningproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MsaLearningProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsaLearningProjectApplication.class, args);
    }
}
