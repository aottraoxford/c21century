package com.century21.apexproperty.repository.api_signin;

import com.century21.apexproperty.service.authorize.Authority;
import com.century21.apexproperty.service.authorize.UserAccount;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignInRepo {
    @Select("SELECT email,password,id " +
            "FROM users " +
            "WHERE users.email=#{email} AND enable IS true AND account_type='origin'")
    @Results({
            @Result(property = "authorities", column = "id", many = @Many(select = "authorities"))
    })
    UserAccount signIn(@Param("email") String email);

    @Select("SELECT authority.role " +
            "FROM authority " +
            "INNER JOIN authorizations ON authority.id=authorizations.authority_id " +
            "WHERE authorizations.users_id=#{id}")
    List<Authority> authorities();

    @Select("SELECT authority.role " +
            "FROM authority " +
            "INNER JOIN authorizations ON authority.id=authorizations.authority_id " +
            "WHERE authorizations.users_id=#{id}")
    List<String> roles(int id);

    @Select("SELECT enable " +
            "FROM users " +
            "WHERE email = #{email} AND account_type='origin'")
    Boolean emailExist(@Param("email") String email);

    @Select("SELECT id " +
            "FROM users " +
            "WHERE email = #{email}")
    Integer findIdByEmail(String email);

}
