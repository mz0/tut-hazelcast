package com.exactpro.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;

@ManagedBean(name = "auth")
@ViewScoped
public class Auth {
    private static final Logger log = LoggerFactory.getLogger(Auth.class);
    @ManagedProperty(value = "#{userInfo}")
    User userInfo;

    public void tryLogin() {
        log.info("login usr {} with password {}", userInfo.getName(), userInfo.getRoles());
    }

    public void setUserInfo(User uInfo) {
        userInfo = uInfo;
    }

    public User getUserInfo() {
        return userInfo;
    }
}
