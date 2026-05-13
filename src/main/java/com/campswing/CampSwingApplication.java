package com.campswing;

import com.campswing.config.EventProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableConfigurationProperties(EventProperties.class)
public class CampSwingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampSwingApplication.class, args);
    }
}
