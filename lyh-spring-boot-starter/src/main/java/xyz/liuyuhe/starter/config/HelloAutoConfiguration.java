package xyz.liuyuhe.starter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.liuyuhe.starter.properties.HelloProperties;
import xyz.liuyuhe.starter.service.HelloService;

@Configuration(proxyBeanMethods = false)
// 当存在某个类时，此自动配置类才会生效
@ConditionalOnClass(value={HelloService.class})
// 导入我们自定义的配置类，供当前类使用
@EnableConfigurationProperties(value = HelloProperties.class)
// 针对web应用
@ConditionalOnWebApplication
// 判断lyh.config.flag的值是否为true, matchIfMissing = true: 没有该配置属性时也会正常加载
@ConditionalOnProperty(prefix = "lyh.config", name = "flag", havingValue = "true", matchIfMissing = true)
public class HelloAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(HelloService.class)
    public HelloService helloService(HelloProperties helloProperties) {
        HelloService helloService = new HelloService();
        helloService.setName(helloProperties.getName());
        helloService.setAge(helloProperties.getAge());
        return helloService;
    }
}
