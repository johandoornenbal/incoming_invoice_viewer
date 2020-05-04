package org.incode.estatio;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Service
public class CredentialsService implements Serializable {

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String password;

    @Getter @Setter
    private Boolean bypass;

    public String greet(String name) {
        if (name == null || name.isEmpty()) {
            return "Hello anonymous user";
        } else {
            return "Hello " + name;
        }
    }

    public boolean hasCredentials() {
        return getUsername()!=null && getPassword()!=null;
    }
}
