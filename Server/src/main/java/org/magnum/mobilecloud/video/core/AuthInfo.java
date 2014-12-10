package org.magnum.mobilecloud.video.core;

import org.springframework.data.annotation.Id;

/**
 * Created by cong on 11/16/14.
 */
public class AuthInfo {
    @Id
    private String username;
    private String password;
    private String[] authorities;

    public AuthInfo() {
    }


    public AuthInfo(String username, String password, String[] authorities) {
        super();
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }
}
