package com.century21.century21cambodia.repository.api_slider_update;

import com.century21.century21cambodia.repository.api_projects.dym.ProjectUtil;
import com.century21.century21cambodia.repository.api_slider_update.dym.SliderUpdateUtil;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface SliderUpdateRepo {
    @SelectProvider(type= SliderUpdateUtil.class,method = "updateSlider")
    Integer sliderUpdate(@Param("enable")boolean enable,@Param("image")String image,@Param("sliderID")int sliderID);

    @Select("SELECT COUNT(id) " +
            "FROM events " +
            "WHERE id =#{id}")
    int checkID(int id);

    @Select("SELECT banner " +
            "FROM events " +
            "WHERE id = #{id}")
    String findImage(int id);
}
