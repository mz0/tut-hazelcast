package com.exactpro.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean(name = "userInfo")
@SessionScoped
public class User implements Serializable {
    private String loginName;
    private String roles;

    public String getName() { return loginName; }
    public void setName(String name) { this.loginName = name; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
}
