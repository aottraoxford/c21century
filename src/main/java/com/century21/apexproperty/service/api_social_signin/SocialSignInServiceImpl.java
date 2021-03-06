package com.century21.apexproperty.service.api_social_signin;

import com.century21.apexproperty.exception.CustomRuntimeException;
import com.century21.apexproperty.model.request.SocialSignIn;
import com.century21.apexproperty.model.response.CustomResponse;
import com.century21.apexproperty.model.response.OAuth2;
import com.century21.apexproperty.repository.UserRepo;
import com.century21.apexproperty.repository.api_social_signin.SocialSignInRepo;
import com.century21.apexproperty.util.JwtUtil;
import com.century21.apexproperty.util.Url;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SocialSignInServiceImpl implements SocialSignInService {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SocialSignInRepo socialSignInRepo;
    @Autowired
    private UserRepo userRepo;

    @Override
    public ResponseEntity socialSignIn(String token) {
        SocialSignIn socialSignIn = (SocialSignIn) jwtUtil.tokenToObject(token, JwtUtil.secret, SocialSignIn.class);
        CustomResponse customResponse;

        //check if >0 email from social is exist
        if (socialSignInRepo.isEmailExist(socialSignIn.getEmail()) > 0) {

        }

        if (socialSignIn.getEmail() == null || socialSignIn.getEmail().equalsIgnoreCase("null")) {
            socialSignIn.setEmail(socialSignIn.getSocialId());
        } else socialSignIn.setEmail(socialSignIn.getEmail() + "|" + UUID.randomUUID());
        if (socialSignInRepo.checkSocialAccount(socialSignIn.getSocialId()) < 1) {
            if (socialSignInRepo.saveSocialSignIn(socialSignIn) < 1) {
                throw new CustomRuntimeException(500, "Insert fail.");
            }
        }//else{
//            if(socialSignInRepo.updateSocialSignIn(socialSignIn)==null){
//                throw new CustomRuntimeException(500,"Update fail.");
//            }
//        }
        try {
            HttpResponse<OAuth2> jsonResponse = Unirest.post(Url.oauthTokenUrl)
                    .header("accept", "application/json")
                    .header("Authorization", "Basic YzIxYzoxMjM0NTY3OEBDZW51dHJ5MjFDYW1ib2RpYVJlYWxFc3RhdGU=")
                    .queryString("grant_type", "password")
                    .queryString("username", socialSignIn.getSocialId())
                    .queryString("password", socialSignIn.getSocialId())
                    .asObject(OAuth2.class);
            List<String> roles = new ArrayList<>();
            roles.add(socialSignInRepo.findRoleByAppID(socialSignIn.getSocialId()));
            jsonResponse.getBody().setRoles(roles);
            customResponse = new CustomResponse(200, jsonResponse.getBody());
        } catch (UnirestException e) {
            customResponse = new CustomResponse(500);
            customResponse.setStatus(e.getMessage());
            return customResponse.httpResponse();
        }

        return customResponse.httpResponse("result");
    }
}
