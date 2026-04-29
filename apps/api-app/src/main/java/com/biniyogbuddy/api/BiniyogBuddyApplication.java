package com.biniyogbuddy.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.biniyogbuddy")
@EnableScheduling
@EnableJpaRepositories(basePackages = {
        "com.biniyogbuddy.users.repository",
        "com.biniyogbuddy.stocks.repository",
        "com.biniyogbuddy.trades.repository",
        "com.biniyogbuddy.market.repository",
        "com.biniyogbuddy.scraper.repository"
})
@EntityScan(basePackages = {
        "com.biniyogbuddy.users.entity",
        "com.biniyogbuddy.stocks.entity",
        "com.biniyogbuddy.trades.entity",
        "com.biniyogbuddy.market.entity",
        "com.biniyogbuddy.scraper.entity"
})
public class BiniyogBuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiniyogBuddyApplication.class, args);
    }
}
