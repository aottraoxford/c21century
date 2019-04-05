package com.century21.apexproperty.repository.api_slider;

import com.century21.apexproperty.repository.api_slider.dym.SliderUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SliderRepo {
    @SelectProvider(type= SliderUtil.class,method = "getSlider")
    @Results({
            @Result(property = "slider",column = "banner")
    })
    List<Slider> getSlider(@Param("en") boolean enable);
}