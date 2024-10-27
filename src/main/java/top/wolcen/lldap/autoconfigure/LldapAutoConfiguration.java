package top.wolcen.lldap.autoconfigure;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.wolcen.lldap.service.LldapService;
import top.wolcen.lldap.service.impl.LldapServiceImpl;

@Slf4j
@Configuration
@EnableConfigurationProperties(LldapProperties.class)
public class LldapAutoConfiguration {
    @Autowired
    private LldapProperties properties;

    @Bean
    @ConditionalOnProperty(name = "lldap.url")
    public LldapService lldapService() {
        return new LldapServiceImpl(properties);
    }

}
