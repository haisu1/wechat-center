
package com.cdkj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * description: 微信公众号主服务 <br>
 * date: 2017/11/23 上午9:49
 *
 * @author bovine
 * @version 1.0
 * @since JDK 1.8
 */
@EnableTransactionManagement
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@MapperScan("com.cdkj.*.model.dao")
public class WechatCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatCenterApplication.class, args);
    }
}
