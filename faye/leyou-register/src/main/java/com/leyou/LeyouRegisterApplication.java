package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

//启动类
@SpringBootApplication
@EnableEurekaServer
public class LeyouRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeyouRegisterApplication.class);
    }
}
