package com.century21.repository.api_user_reset_pass;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface UserResetPassRepo {

    @Update("UPDATE verification SET enable = true " +
            "WHERE name = #{email} AND code = #{code}")
    Integer enableByEmailAndCode(@Param("email") String email, @Param("code") int code);

    @Update("UPDATE users SET password = crypt(#{password},gen_salt('bf')) " +
            "WHERE email = #{email}")
    Integer updateUserPassword(@Param("email") String email, @Param("password") String password);

    @Delete("DELETE FROM verification WHERE name = #{email}")
    Integer deleteVerification(String email);

}