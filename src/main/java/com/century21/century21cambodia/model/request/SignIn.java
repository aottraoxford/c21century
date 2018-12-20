package com.century21.century21cambodia.model.request;

import javax.validation.constraints.*;

public class SignIn {
    @NotEmpty
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @NotNull
    @NotEmpty
    @Size(min = 8)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
