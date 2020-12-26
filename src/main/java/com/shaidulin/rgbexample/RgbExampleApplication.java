package com.shaidulin.rgbexample;

import com.shaidulin.rgbexample.domain.Color;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.MongoRepository;

@SpringBootApplication
@Slf4j
public class RgbExampleApplication{

    public static void main(String[] args) {
        SpringApplication.run(RgbExampleApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(MongoRepository<Color, String> colorRepository) {
        return args -> {
            log.error("################");
            log.error("count is: {}", colorRepository.count());
            colorRepository.save(new Color());
            log.error("################");
            log.error("count is: {}", colorRepository.count());
        };
    }

}
