package top.wolcen.lldap.service;

import java.util.List;

public interface LldapService {
    /**
     * 验证用户名密码
     * @param username
     * @param password
     * @return 出错返回 null 否则返回用户归属的所有用户组
     */
    List<String> verifyUser(String username, String password);
}
