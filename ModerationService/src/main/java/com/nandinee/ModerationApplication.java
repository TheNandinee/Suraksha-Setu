package com.nandinee;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.annotation.PostConstruct;

@SpringBootApplication
public class ModerationApplication {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServers;

    public static void main(String[] args) {
        SpringApplication.run(ModerationApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("🔥 ACTUAL Kafka bootstrap-servers = " + kafkaServers);
    }

    @PostConstruct
    public void printKafkaServers() {
        System.out.println("🔥 ACTUAL Kafka bootstrap-servers = " + kafkaServers);
    }

}
