package com.example.shiro;

import com.example.entity.ManagerInfo;
import com.example.entity.Permission;
import com.example.entity.SysRole;
import com.example.service.ManagerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 身份校验核心类
 */
@Slf4j
public class MyShiroRealm extends AuthorizingRealm {
    @Autowired
    private ManagerInfoService managerInfoService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("权限设置: MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        ManagerInfo managerInfo = (ManagerInfo) principalCollection.getPrimaryPrincipal();
        // 设置相应角色的权限信息
        for (SysRole role : managerInfo.getRoles()) {
            // 设置角色
            authorizationInfo.addRole(role.getRole());
            for (Permission p : role.getPermissions()) {
                // 设置权限
                authorizationInfo.addStringPermission(p.getPermission());
            }
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("MyShiroRealm.doGetAuthenticationInfo()");
        // 获取用户名
        String username = (String) authenticationToken.getPrincipal();
        ManagerInfo managerInfo = managerInfoService.findByUsername(username);
        if (null == managerInfo) {
            return null;
        }
        return new SimpleAuthenticationInfo(managerInfo, managerInfo.getPassword(), ByteSource.Util.bytes(managerInfo.getCredentialsSalt()), getName());
    }

    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher md5CredentialsMatcher = new HashedCredentialsMatcher();
        md5CredentialsMatcher.setHashAlgorithmName(ShiroUtils.HASH_ALGORITHM_NAME);
        md5CredentialsMatcher.setHashIterations(ShiroUtils.HASH_ITERATIONS);
        super.setCredentialsMatcher(md5CredentialsMatcher);
    }
}
