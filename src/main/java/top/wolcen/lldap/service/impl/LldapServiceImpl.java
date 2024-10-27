package top.wolcen.lldap.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import top.wolcen.lldap.autoconfigure.LldapProperties;
import top.wolcen.lldap.service.LldapService;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Slf4j
public class LldapServiceImpl implements LldapService {

    private LldapProperties properties;

    public LldapServiceImpl(LldapProperties properties) {
        this.properties = properties;
    }

    @Override
    public List<String> verifyUser(String username, String password) {
        List<String> groups = null;
        try {
            log.debug("ldap auth user:{} ", username);
            String usrDN = "uid=" + username + ",ou=people," + properties.getBasedn();
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, properties.getUrl());
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, usrDN);
            env.put(Context.SECURITY_CREDENTIALS, password);

            DirContext ctx = new InitialDirContext(env);

            // 构建搜索过滤器，查找用户
            String searchFilter = "(&(objectClass=person)(uid=" + username + "))";
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<SearchResult> results = ctx.search(properties.getBasedn(), searchFilter, searchControls);

            groups = new ArrayList<>();
            if (results.hasMore()) {
                SearchResult result = results.next();
                String userDn = result.getNameInNamespace();

                // 构建搜索过滤器，查找用户所属的组
                searchFilter = "(&(objectClass=groupOfNames)(member=" + userDn + "))";
                results = ctx.search(properties.getBasedn(), searchFilter, searchControls);

                while (results.hasMore()) {
                    SearchResult groupResult = results.next();
                    Attributes attrs = groupResult.getAttributes();
                    Attribute cnAttr = attrs.get("cn");

                    if (cnAttr != null) {
                        groups.add(cnAttr.get().toString());
                    }
                }
            }

            ctx.close();
            return groups;
        } catch (Exception e) {
            log.error("ldap auth error", e);
        }
        return null;
    }

}
