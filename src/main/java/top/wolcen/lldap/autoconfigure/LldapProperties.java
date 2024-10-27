package top.wolcen.lldap.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "lldap")
public class LldapProperties {
    private String url;
    private String basedn;

}
