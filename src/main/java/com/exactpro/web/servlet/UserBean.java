package com.exactpro.web.servlet;

import java.io.Serializable;
import java.util.Set;

public class UserBean implements Serializable {
    private String loginName;
    private Set<String> roles;

    public String getLoginName() { return loginName; }
    public void setLoginName(String loginName) { this.loginName = loginName; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
