package com.century21.century21cambodia.service.api_social_signin;

import org.springframework.http.ResponseEntity;

public interface SocialSignInService {
    ResponseEntity socialSignIn(String token);
}