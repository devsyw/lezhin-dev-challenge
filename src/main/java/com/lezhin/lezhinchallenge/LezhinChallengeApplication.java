package com.lezhin.lezhinchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LezhinChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LezhinChallengeApplication.class, args);
    }

}
