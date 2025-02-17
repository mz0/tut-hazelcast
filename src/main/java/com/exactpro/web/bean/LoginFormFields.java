package com.exactpro.web.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean(name = "userInfo")
@SessionScoped
public class LoginFormFields implements Serializable {
    private String loginName;
    private String password;

    public String getName() { return loginName; }
    public void setName(String name) { this.loginName = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
