package server;


import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.UUID;

public class Authentication implements Serializable {
    private final String login;
    private final UUID password;

    public Authentication(String login, UUID password) {
        if (StringUtils.isEmpty(login)) {
            throw new IllegalArgumentException("Login is null");
        }
        if (password == null) {
            throw new NullPointerException("Password is null");
        }
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public UUID getPassword() {
        return password;
    }
}