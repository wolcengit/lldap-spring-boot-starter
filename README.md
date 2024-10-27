# lldap-spring-boot-starter

> Spring Boot 集成 lldap 的最佳实践

# 1. 快速开始

## 1.1. 基础配置

- 引用依赖

```xml
<dependency>
  <groupId>top.wolcen.lldap</groupId>
  <artifactId>lldap-spring-boot-starter</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

- 添加配置

在 `application.yml` 中添加配置配置信息

```yaml
lldap:
  url: ldap://example.com:3890
  basedn: dc=example,dc=com
```

## 1.2. 代码调用


```java
@Slf4j
@RestController
@RequestMapping("/sys")
public class TestController {

    @Autowired
    private final LldapService lldapService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public LoginResponse login(@Validated @RequestBody LoginRequest request) {
        LoginResponse response = new LoginResponse();
        String usr = request.getUserName();
        String pwd = request.getUserPass();
        List<String> groups = lldapService.verifyUser(usr,pwd);
        // check grooups, null for error 
        // .......(略)
        if(groups == null){
            response.setSuccess(false);
            response.setMessage("用户名称或者密码错误");
            return response;
        }
        if(!groups.contains("some-group")){
            response.setSuccess(false);
            response.setMessage("用户没有被授权");
            return response;
        }
        response.setSuccess(true);
        response.setMessage("");
        response.setUsername(usr);
        return response;
    }

}
```

# 2.附录

> 不使用外部数据源时候，不要配置 LLDAP_DATABASE_URL

## 2.1 Docker运行 lldap

示例如下

```shell
sudo docker run -d \
  -p 3890:3890 -p 17170:17170 \
  -v /opt/lldap/data:/data \
  -e LLDAP_LDAP_BASE_DN="dc=example,dc=com" \
  -e LLDAP_LDAP_USER_PASS="P@ssw0rd" \
  -e LLDAP_DATABASE_URL="mysql://mysql-user:password@mysql-server/my-database"
  lldap/lldap:latest
```

## 2.2  Kubernetes运行 lldap

示例如下

```yaml
kind: Deployment
apiVersion: apps/v1
metadata:
  name: lldap
  labels:
    app: lldap
spec:
  replicas: 1
  selector:
    matchLabels:
      app: lldap
  template:
    metadata:
      labels:
        app: lldap
    spec:
      volumes:
        - name: vlldap
          emptyDir: {}
      containers:
        - name: lldap
          image: lldap/lldap:latest 
          env:
            - name: LLDAP_LDAP_BASE_DN
              value: "dc=example,dc=com"
            - name: LLDAP_LDAP_USER_PASS
              value: "P@ssw0rd"
            - name: LLDAP_DATABASE_URL
              value: "mysql://mysql-user:password@mysql-server/my-database"
          ports:
            - containerPort: 17170
            - containerPort: 3890
          resources:
            requests:
              memory: "128Mi"
              cpu: "1"
            limits:
              memory: "128Mi"
              cpu: "1"
          volumeMounts:
            - name: vlldap
              mountPath: /data
          imagePullPolicy: IfNotPresent
      restartPolicy: Always
      
---
apiVersion: v1
kind: Service
metadata:
  labels:
    app: lldap
  name: lldap
spec:
  ports:
    - name: "3890"
      port: 3890
      targetPort: 3890
    - name: "17170"
      port: 17170
      targetPort: 17170
  selector:
    app: lldap
```
