package com.zt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.zt.dao")
@EnableScheduling
@EnableTransactionManagement  // 开启事务支持，默认开启 1、使用注解回滚 2、使用TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();进行手动回滚。
public class ItServiceCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItServiceCenterApplication.class, args);
    }

}
