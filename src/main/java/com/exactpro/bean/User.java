package com.exactpro.bean;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable {
    private String loginName;
    private Set<String> roles;

    public String getName() { return loginName; }
    public void setName(String name) { this.loginName = name; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
