package com.century21.apexproperty.repository;

import com.century21.apexproperty.model.ID;
import com.century21.apexproperty.model.Pagination;
import com.century21.apexproperty.repository.api_slider.Slider;
import com.century21.apexproperty.util.Url;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Repository
public interface ProjectRepo {
    @Select("DELETE FROM project " +
            "WHERE id=#{id} returning id")
    Integer removeProjectById(Integer id);

    @Select("DELETE FROM events " +
            "WHERE type ilike 'slider' AND id=#{id} RETURNING banner,id")
    @Results({
            @Result(property = "slider", column = "banner")
    })
    Slider removeSliderById(Integer id);

    @Select("insert into project_type(name) values (#{name}) returning id")
    Integer insertProjectType(String name);

    @Select("INSERT INTO country(name) VALUES (#{name}) RETURNING id")
    Integer insertCountry(String name);

    @Select("SELECT name,description,thumbnail,isdisplay " +
            "FROM project " +
            "WHERE id = #{proID} ")
    ProjectNoti projectNoti(int proID);

    @SelectProvider(type = ProjectUtil.class, method = "findAllProjectByFilterCount")
    int findAllProjectByFilterCount(@Param("filter") FilterRequest filter);

    @SelectProvider(type = ProjectUtil.class, method = "findAllProjectByFilter")
    @Results({
            @Result(property = "status", column = "isdisplay"),
            @Result(property = "rentOrBuy", column = "rent_or_buy"),
            @Result(property = "sqmPrice", column = "sqm_price"),
            @Result(property = "country", column = "country_id", one = @One(select = "country")),
            @Result(property = "projectType", column = "project_type_id", one = @One(select = "projectType"))
    })
    List<ProjectListingResponse> findAllProjectByFilter(@Param("filter") FilterRequest filterRequest, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT id,first_name,last_name,email,gender,phone_number,image,account_type " +
            "FROM users " +
            "WHERE id=#{user_id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "phoneNumber", column = "phone_number"),
            @Result(property = "accountType", column = "account_type")
    })
    UserRepo.User findOneUser();

    @Select("SELECT id " +
            "FROM project_intro " +
            "WHERE project_id=#{proID}")
    List<Integer> findAllProjectIntroID(int proID);

    @Update("UPDATE project_intro " +
            "SET name=#{pi.name},description=#{pi.description} " +
            "WHERE project_id=#{proID} AND id=#{pi.id}")
    Integer updateProjectIntro(@Param("pi") ProjectIntroduction projectIntroduction, @Param("proID") int proID);

    @Delete("DELETE FROM project_intro " +
            "WHERE id=#{id} AND project_id=#{proID}")
    Integer removeProjectIntro(@Param("id") int id, @Param("proID") int projectID);

    @Select("SELECT id " +
            "FROM country " +
            "WHERE name ilike #{name}")
    Integer findCountryIDByName(String name);

    @Select("SELECT id " +
            "FROM project_type " +
            "WHERE name ilike #{name}")
    Integer findProjectTypeIDByName(String name);

    @Select("SELECT DISTINCT(country.name),country.id " +
            "FROM country " +
            "INNER JOIN project ON project.country_id=country.id " +
            "WHERE project.isdisplay IS TRUE " +
            "ORDER BY country.id ")
    @Results({
            @Result(property = "projectTypeForWebList", column = "id", many = @Many(select = "getProjectTypeForWeb")),
            @Result(property = "countryName", column = "name"),
            @Result(property = "countryID", column = "id")
    })
    List<CountryForWeb> getCountryForWeb();

    @Select("SELECT DISTINCT(project_type.name),project_type.id ,project.country_id " +
            "FROM project_type " +
            "INNER JOIN project ON project.project_type_id=project_type.id " +
            "WHERE project.country_id = #{id} " +
            "ORDER BY project_type.id")
    @Results({
            //@Result(property = "projectList",column = "{cid=country_id,pid=id}",many = @Many(select = "getProjectForWeb")),
            @Result(property = "type", column = "name")
    })
    List<ProjectTypeForWeb> getProjectTypeForWeb();

    @Select("SELECT id,name,price,grr,country_id,project_type_id,thumbnail " +
            "FROM project " +
            "WHERE country_id=#{cid} AND project_type_id=#{pid} " +
            "ORDER BY id DESC LIMIT 1")
    List<Project> getProjectForWeb();

    @Select("SELECT id " +
            "FROM favorite " +
            "WHERE project_id=#{projectID} AND user_id=#{userID}")
    Integer favorite(@Param("projectID") int projectID, @Param("userID") int userID);

    @Delete("DELETE FROM project_gallery " +
            "WHERE url=#{name} AND type='image' ")
    void removeOneGallery(@Param("name") String name);

    @SelectProvider(type = ProjectUtil.class, method = "findAllProject")
    @Results({
            @Result(property = "status", column = "isdisplay"),
            @Result(property = "country", column = "country_id", one = @One(select = "country")),
            @Result(property = "projectType", column = "project_type_id", one = @One(select = "projectType"))
    })
    List<ProjectListingResponse> findAllProject(@Param("title") String title, @Param("cid") int cid, @Param("pid") int pid, @Param("status") String status, @Param("limit") int limit, @Param("offset") int offset);

    @SelectProvider(type = ProjectUtil.class, method = "findAllProjectCount")
    int findAllProjectCount(@Param("title") String title, @Param("cid") int cid, @Param("pid") int pid, @Param("status") String status);

    @Select("SELECT thumbnail " +
            "FROM project " +
            "WHERE id=#{proID}")
    String findOneThumbnail(@Param("proID") int projectID);

    @InsertProvider(type = ProjectUtil.class, method = "insertGallery")
    void insertGallery(@Param("gall") String galleries, @Param("proID") int projectID);

    @UpdateProvider(type = ProjectUtil.class, method = "updateThumbnail")
    void updateThumbnail(@Param("thumbnail") String thumbnail, @Param("proID") int projectID);

    @SelectProvider(type = ProjectUtil.class, method = "findOneProject")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "builtDate", column = "built_date"),
            @Result(property = "completedDate", column = "completed_date"),
            @Result(property = "title", column = "name"),
            @Result(property = "addressOne", column = "address_1"),
            @Result(property = "addressTwo", column = "address_2"),
            @Result(property = "averageAnnualRentFrom", column = "avg_rent_from"),
            @Result(property = "averageAnnualRentTo", column = "avg_rent_to"),
            @Result(property = "downPayment", column = "down_payment"),
            @Result(property = "status", column = "isdisplay"),
            @Result(property = "sqmPrice", column = "sqm_price"),
            @Result(property = "rentOrBuy", column = "rent_or_buy"),
            @Result(property = "country", column = "country_id", one = @One(select = "country")),
            @Result(property = "projectType", column = "project_type_id", one = @One(select = "projectType")),
            @Result(property = "projectIntro", column = "id", many = @Many(select = "projectIntro")),
            @Result(property = "projectGalleries", column = "id", many = @Many(select = "projectGalleries")),
            @Result(property = "propertyTypes", column = "id", many = @Many(select = "propertyTypes")),
            @Result(property = "towerTypes", column = "id", many = @Many(select = "towerTypes")),
            @Result(property = "user", column = "user_id", one = @One(select = "findOneUser"))
    })
    Project findOneProject(@Param("proID") int projectID);

    @Select("SELECT name " +
            "FROM country " +
            "WHERE id=#{country_id}")
    String country();

    @Select("SELECT name " +
            "FROM project_type " +
            "WHERE id=#{project_type_id}")
    String projectType();

    @Select("SELECT id,name,description " +
            "FROM project_intro " +
            "WHERE project_id=#{id}")
    List<ProjectIntroduction> projectIntro();

    @Select("SELECT id,url " +
            "FROM project_gallery " +
            "WHERE project_id=#{id} AND type='image'")
    @Results({
            @Result(property = "image", column = "url")
    })
    List<ProjectGallery> projectGalleries();

    @Select("SELECT * " +
            "FROM property_type " +
            "WHERE project_id=#{id}")
    @Results({
            @Result(property = "type", column = "name")
    })
    List<PropertyType> propertyTypes();

    @Select("SELECT * " +
            "FROM tower_type " +
            "WHERE project_id=#{id}")
    List<TowerType> towerTypes();

    @UpdateProvider(type = ProjectUtil.class, method = "updateProject")
    void updateProject(@Param("pro") ProjectRequest projectRequest);

    @InsertProvider(type = ProjectUtil.class, method = "insertProject")
    @SelectKey(statement = "select nextval('project_id_seq') ", resultType = int.class, before = true, keyProperty = "id.id")
    int insertProject(@Param("id") ID id, @Param("userID") int userID, @Param("project") ProjectInsertRequest project);

    @InsertProvider(type = ProjectUtil.class, method = "insertProjectIntro")
    int insertProjectIntro(@Param("intro") ProjectIntroduction intro, @Param("proID") int projectID);

    @InsertProvider(type = ProjectUtil.class, method = "insertPropertyType")
    int insertPropertyType(@Param("pt") PropertyType propertyType, @Param("proID") int projectID);

    @SelectProvider(type = ProjectUtil.class, method = "insertTowerType")
    void insertTowerType(@Param("type") String type, @Param("proID") int projectID);

    @Select("SELECT id " +
            "FROM property_type " +
            "WHERE project_id=#{proID}")
    List<Integer> findAllPropertyTypeID(@Param("proID") int proID);

    @Update("UPDATE property_type " +
            "SET name=#{pt.type},bedroom=#{pt.bedroom},floor=#{pt.floor},width=#{pt.width},height=#{pt.height},bathroom=#{pt.bathroom},parking=#{pt.parking} " +
            "WHERE id = #{pt.id} AND project_id=#{proID}")
    Integer updatePropertyType(@Param("pt") PropertyType propertyType, @Param("proID") int projectID);

    @Delete("DELETE FROM property_type " +
            "WHERE id=#{id}")
    void removePropertyType(@Param("id") int id, @Param("proID") int projectID);

    @Select("SELECT id " +
            "FROM tower_type " +
            "WHERE project_id=#{proID}")
    List<Integer> findAllTowerTypeID(@Param("proID") int projectID);

    @Update("UPDATE tower_type " +
            "SET type=#{ty.type} " +
            "WHERE id=#{ty.id} and project_id=#{proID}")
    Integer updateTowerType(@Param("ty") TowerType towerType, @Param("proID") int projectID);

    @Delete("DELETE FROM tower_type " +
            "WHERE id=#{id} and project_id=#{proID}")
    void removeTowerType(@Param("id") int id, @Param("proID") int projectID);

    class ProjectUtil {
        public String findAllProjectByFilterCount(@Param("filter") FilterRequest filter) {
            return new SQL() {
                {
                    SELECT("count(project.id)");
                    FROM("project");
                    INNER_JOIN("country ON country.id=project.country_id");
                    INNER_JOIN("project_type ON project_type.id=project.project_type_id");
                    if (filter.getRoom() > 0)
                        INNER_JOIN("property_type on project.id=property_type.project_id");
                    if (filter.getTitle() != null && filter.getTitle().length() > 0)
                        WHERE("project.name ILIKE '%'||#{filter.title}||'%'");
                    if (filter.getCity() != null && filter.getCity().length() > 0)
                        WHERE("city ILIKE '%'||#{filter.city}||'%'");
                    if (filter.getStatus() != null && filter.getStatus().length() > 0)
                        if (filter.getStatus().equalsIgnoreCase("true"))
                            WHERE("isdisplay IS TRUE");
                        else if (filter.getStatus().equalsIgnoreCase("false"))
                            WHERE("isdisplay IS FALSE");
                    if (filter.getCountryID() > 0)
                        WHERE("project.country_id=#{filter.countryID}");
                    if (filter.getProjectTypeID() > 0)
                        WHERE("project.project_type_id=#{filter.projectTypeID}");
                    if (filter.getRoom() > 0)
                        WHERE("bedroom = #{filter.room}");
                    else if (filter.getRoom() > 7)
                        WHERE("bedroom > 7");
                    if (filter.getToPrice() > 0 && filter.getFromPrice() > 0)
                        WHERE("price between #{filter.fromPrice} AND #{filter.toPrice}");
                    else if (filter.getToPrice() > 0) WHERE("price < #{filter.toPrice}");
                    else if (filter.getFromPrice() > 0) WHERE("price < #{filter.fromPrice}");
                    if (filter.getRentOrBuy() != null && filter.getRentOrBuy().length() > 0) {
                        if (filter.getRentOrBuy().equalsIgnoreCase("rent") || filter.getRentOrBuy().equalsIgnoreCase("buy")) {
                            WHERE("rent_or_buy ILIKE #{filter.rentOrBuy}");
                        }
                    }
                }
            }.toString();
        }

        public String findAllProjectByFilter(@Param("filter") FilterRequest filter, @Param("limit") int limit, @Param("offset") int offset) {
            return new SQL() {
                {
                    SELECT("substring(project.description,1,200)||'.....' as description,rent_or_buy,project.id,project.name,price,project.sqm_price,grr,country_id,project_type_id,country.name,project_type.name,thumbnail,isdisplay");
                    FROM("project");
                    INNER_JOIN("country ON country.id=project.country_id");
                    INNER_JOIN("project_type ON project_type.id=project.project_type_id");
                    if (filter.getRoom() > 0)
                        INNER_JOIN("property_type on project.id=property_type.project_id");
                    if (filter.getTitle() != null && filter.getTitle().length() > 0)
                        WHERE("project.name ILIKE '%'||#{filter.title}||'%'");
                    if (filter.getCity() != null && filter.getCity().length() > 0)
                        WHERE("city ILIKE '%'||#{filter.city}||'%'");
                    if (filter.getStatus() != null && filter.getStatus().length() > 0)
                        if (filter.getStatus().equalsIgnoreCase("true"))
                            WHERE("isdisplay IS TRUE");
                        else if (filter.getStatus().equalsIgnoreCase("false"))
                            WHERE("isdisplay IS FALSE");
                    if (filter.getCountryID() > 0)
                        WHERE("project.country_id=#{filter.countryID}");
                    if (filter.getProjectTypeID() > 0)
                        WHERE("project.project_type_id=#{filter.projectTypeID}");
                    if (filter.getRoom() > 0 && filter.getRoom() < 9)
                        WHERE("bedroom = #{filter.room}");
                    else if (filter.getRoom() > 7)
                        WHERE("bedroom > 7");
                    if (filter.getToPrice() > 0 && filter.getFromPrice() > 0)
                        WHERE("price between #{filter.fromPrice} AND #{filter.toPrice}");
                    else if (filter.getToPrice() > 0) WHERE("price < #{filter.toPrice}");
                    else if (filter.getFromPrice() > 0) WHERE("price < #{filter.fromPrice}");
                    if (filter.getRentOrBuy() != null && filter.getRentOrBuy().length() > 0) {
                        if (filter.getRentOrBuy().equalsIgnoreCase("rent") || filter.getRentOrBuy().equalsIgnoreCase("buy")) {
                            WHERE("rent_or_buy ILIKE #{filter.rentOrBuy}");
                        }
                    }
                    if (filter.getSortType() != null && filter.getSortType().length() > 0) {
                        if (filter.getSortType().equalsIgnoreCase("grr"))
                            ORDER_BY("grr limit #{limit} offset #{offset}");
                        else if(filter.getSortType().equalsIgnoreCase("id"))
                            ORDER_BY("project.id DESC limit #{limit} offset #{offset}");
                        else if (filter.getSortType().equalsIgnoreCase("grr-desc"))
                            ORDER_BY("grr DESC limit #{limit} offset #{offset}");
                        else if (filter.getSortType().equalsIgnoreCase("price"))
                            ORDER_BY("price limit #{limit} offset #{offset}");
                        else if (filter.getSortType().equalsIgnoreCase("price-desc"))
                            ORDER_BY("price DESC limit #{limit} offset #{offset}");
                        else if (filter.getSortType().equalsIgnoreCase("title"))
                            ORDER_BY("project.name limit #{limit} offset #{offset}");
                        else if (filter.getSortType().equalsIgnoreCase("title-desc"))
                            ORDER_BY("project.name DESC limit #{limit} offset #{offset}");
                    } else ORDER_BY("project.id DESC limit #{limit} offset #{offset}");
                }
            }.toString();
        }

        public String updateProject(@Param("pro") ProjectRequest projectRequest) {
            return new SQL() {
                {
                    UPDATE("project");
                    SET("city=#{pro.city},name=#{pro.name},grr=#{pro.grr},country_id=#{pro.countryID},project_type_id=#{pro.projectTypeID},completed_date=#{pro.completedDate},built_date=#{pro.builtDate},description=#{pro.description},price=#{pro.price},sqm_price=#{pro.sqmPrice},avg_rent_from=#{pro.avgRentFrom},avg_rent_to=#{pro.avgRentTo},down_payment=#{pro.downPayment},rent_or_buy=#{pro.rentOrBuy},address_1=#{pro.addressOne},address_2=#{pro.addressTwo}");
                    WHERE("id=#{pro.id}");
                }
            }.toString();
        }

        public String findAllProject(@Param("title") String title, @Param("cid") int cid, @Param("pid") int pid, @Param("status") String status, @Param("limit") int limit, @Param("offset") int offset) {
            return new SQL() {
                {
                    SELECT("substring(project.description,1,200)||'.....' as description,id,name,price,sqm_price,grr,country_id,project_type_id,thumbnail,isdisplay");
                    FROM("project");
                    if (cid > 0)
                        WHERE("country_id=#{cid}");
                    if (title != null && title.trim().length() > 0) {
                        WHERE("name ilike '%'||#{title}||'%'");
                    }
                    if (status.equalsIgnoreCase("true"))
                        WHERE("isdisplay IS TRUE");
                    else if (status.equalsIgnoreCase("false"))
                        WHERE("isdisplay IS false");
                    if (pid > 0)
                        WHERE("project_type_id=#{pid}");
                    ORDER_BY("id DESC LIMIT #{limit} OFFSET #{offset}");
                }
            }.toString();
        }

        public String findAllProjectCount(@Param("title") String title, @Param("cid") int cid, @Param("pid") int pid, @Param("status") String status) {
            return new SQL() {
                {
                    SELECT("COUNT(id)");
                    FROM("project");
                    if (title != null && title.trim().length() > 0) {
                        WHERE("name ilike '%'||#{title}||'%'");
                    }
                    if (status.equalsIgnoreCase("true"))
                        WHERE("isdisplay IS TRUE");
                    else if (status.equalsIgnoreCase("false"))
                        WHERE("isdisplay IS false");
                    if (cid > 0)
                        WHERE("country_id=#{cid}");
                    if (pid > 0)
                        WHERE("project_type_id=#{pid}");
                }
            }.toString();
        }

        public String updateThumbnail(@Param("thumbnail") String thumbnail, @Param("proID") int projectID) {
            return new SQL() {
                {
                    UPDATE("project");
                    SET("thumbnail = #{thumbnail}");
                    WHERE("id=#{proID}");
                }
            }.toString();
        }

        public String insertGallery(@Param("gall") String galleries, @Param("proID") int projectID) {
            return new SQL() {
                {
                    INSERT_INTO("project_gallery");
                    VALUES("url,type,project_id", "#{gall},'image',#{proID}");
                }
            }.toString();
        }

        public String findOneProject(@Param("proID") int projectID) {
            return new SQL() {
                {
                    SELECT("project.*");
                    FROM("project");
                    INNER_JOIN("users on project.user_id=users.id");
                    WHERE("project.id=#{proID}");
                }
            }.toString();
        }

        public String insertProject(@Param("id") ID id, @Param("userID") int userID, @Param("project") ProjectInsertRequest project) {
            return new SQL() {
                {
                    INSERT_INTO("project");
                    VALUES("user_id,id,city,name,grr,country_id,project_type_id,completed_date,built_date,description,price,sqm_price,avg_rent_from,avg_rent_to,down_payment,rent_or_buy,address_1,address_2", "#{userID},#{id.id},#{project.city},#{project.name},#{project.grr},#{project.countryID},#{project.projectTypeID},#{project.completedDate},#{project.builtDate},#{project.description},#{project.price},#{project.sqmPrice},#{project.avgRentFrom},#{project.avgRentTo},#{project.downPayment},#{project.rentOrBuy},#{project.addressOne},#{project.addressTwo}");
                }
            }.toString();
        }

        public String insertProjectIntro(@Param("intro") ProjectIntroduction intro, @Param("proID") int projectID) {
            return new SQL() {
                {
                    INSERT_INTO("project_intro");
                    VALUES("name,description,project_id", "#{intro.name},#{intro.description},#{proID}");
                }
            }.toString();
        }

        public String insertPropertyType(@Param("pt") PropertyType propertyType, @Param("proID") int projectID) {
            return new SQL() {
                {
                    INSERT_INTO("property_type");
                    VALUES("name,bedroom,floor,width,height,bathroom,parking,project_id", "#{pt.type},#{pt.bedroom},#{pt.floor},#{pt.width},#{pt.height},#{pt.bathroom},#{pt.parking},#{proID}");
                }
            }.toString();
        }

        public String insertTowerType(@Param("type") String type, @Param("proID") int projectID) {
            return new SQL() {
                {
                    INSERT_INTO("tower_type");
                    VALUES("type,project_id", "#{type},#{proID}");
                }
            }.toString();
        }
    }

    class ProjectNoti {
        @JsonProperty("title")
        private String name;
        private String description;
        @JsonProperty("image")
        private String thumbnail;
        @JsonProperty("status")
        private boolean isdisplay;

        public boolean isIsdisplay() {
            return isdisplay;
        }

        public void setIsdisplay(boolean isdisplay) {
            this.isdisplay = isdisplay;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getThumbnail() {
            if (thumbnail != null) return Url.projectThumbnailUrl + thumbnail;
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
    }

    class FilterRequest {
        @ApiModelProperty(example = "project 1")
        private String title;
        @ApiModelProperty(example = "all", allowableValues = "all,rent,buy")
        @JsonProperty("rent_or_buy")
        private String rentOrBuy;
        @ApiModelProperty(example = "id", allowableValues = "id,grr,grr-desc,price,price-desc,title,title-desc")
        @JsonProperty("sort_type")
        private String sortType;
        @ApiModelProperty(example = "phnom penh")
        private String city;
        @ApiModelProperty(example = "all", allowableValues = "all,true,false")
        private String status;
        @ApiModelProperty(example = "0")
        @JsonProperty("project_type_id")
        private int projectTypeID;
        @ApiModelProperty(example = "1")
        @JsonProperty("country_id")
        private int countryID;
        @ApiModelProperty(example = "0")
        private int room;
        @ApiModelProperty(example = "0")
        @JsonProperty("from_price")
        private double fromPrice;
        @ApiModelProperty(example = "0")
        @JsonProperty("to_price")
        private double toPrice;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTitle() {
            if (title != null) {
                if (title.equals("")) setTitle(null);
                else setTitle(title.trim().replaceAll(" ", "%"));
            }
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRentOrBuy() {
            if (rentOrBuy != null)
                if (rentOrBuy.equals("")) setRentOrBuy(null);
            return rentOrBuy;
        }

        public void setRentOrBuy(String rentOrBuy) {
            this.rentOrBuy = rentOrBuy;
        }

        public String getSortType() {
            if (sortType != null)
                if (sortType.equals("")) setSortType(null);
            return sortType;
        }

        public void setSortType(String sortType) {
            this.sortType = sortType;
        }

        public String getCity() {
            if (city != null)
                if (city.equals("")) setCity(null);
                else setCity(city.trim().replaceAll(" ", "%"));
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getProjectTypeID() {
            return projectTypeID;
        }

        public void setProjectTypeID(int projectTypeID) {
            this.projectTypeID = projectTypeID;
        }

        public int getCountryID() {
            return countryID;
        }

        public void setCountryID(int countryID) {
            this.countryID = countryID;
        }

        public int getRoom() {
            return room;
        }

        public void setRoom(int room) {
            this.room = room;
        }

        public double getFromPrice() {
            return fromPrice;
        }

        public void setFromPrice(double fromPrice) {
            this.fromPrice = fromPrice;
        }

        public double getToPrice() {
            return toPrice;
        }

        public void setToPrice(double toPrice) {
            this.toPrice = toPrice;
        }
    }

    class ProjectTypeForWeb {
        private int id;
        private String type;
        @JsonProperty("data")
        private List<Project> projectList;
        @JsonProperty("paging")
        private Pagination pagination;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Project> getProjectList() {
            return projectList;
        }

        public void setProjectList(List<Project> projectList) {
            this.projectList = projectList;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
    }

    class CountryForWeb {
        @JsonProperty("country_id")
        private int countryID;
        @JsonProperty("country_name")
        private String countryName;
        @JsonProperty("project_type")
        private List<ProjectTypeForWeb> projectTypeForWebList;

        public int getCountryID() {
            return countryID;
        }

        public void setCountryID(int countryID) {
            this.countryID = countryID;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public List<ProjectTypeForWeb> getProjectTypeForWebList() {
            return projectTypeForWebList;
        }

        public void setProjectTypeForWebList(List<ProjectTypeForWeb> projectTypeForWebList) {
            this.projectTypeForWebList = projectTypeForWebList;
        }
    }

    class ProjectListingResponse {
        private int id;
        private String name;
        private double price;
        private double grr;
        @JsonProperty("sqm_price")
        private double sqmPrice;
        private String country;
        private String description;
        @JsonProperty("project_type")
        private String projectType;
        private String thumbnail;
        @JsonProperty("rent_or_buy")
        private String rentOrBuy;
        private boolean status;

        public double getSqmPrice() {
            return sqmPrice;
        }

        public void setSqmPrice(double sqmPrice) {
            this.sqmPrice = sqmPrice;
        }

        public String getRentOrBuy() {
            return rentOrBuy;
        }

        public void setRentOrBuy(String rentOrBuy) {
            this.rentOrBuy = rentOrBuy;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getGrr() {
            return grr;
        }

        public void setGrr(double grr) {
            this.grr = grr;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProjectType() {
            return projectType;
        }

        public void setProjectType(String projectType) {
            this.projectType = projectType;
        }

        public String getThumbnail() {
            if (thumbnail != null) return Url.projectThumbnailUrl + thumbnail;
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

    }

    class ProjectListingRequest {
        @JsonProperty("country_id")
        private int countryID;
        @JsonProperty("project_type_id")
        private int projectTypID;
        @ApiModelProperty(allowableValues = "all,true,false")
        private String status;

        public int getCountryID() {
            return countryID;
        }

        public void setCountryID(int countryID) {
            this.countryID = countryID;
        }

        public int getProjectTypID() {
            return projectTypID;
        }

        public void setProjectTypID(int projectTypID) {
            this.projectTypID = projectTypID;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    class Project {
        private int id;
        private boolean status;
        private String title;
        @JsonProperty("rent_or_buy")
        private String rentOrBuy;
        private String country;
        private String city;
        @JsonProperty("address_1")
        private String addressOne;
        @JsonProperty("address_2")
        private String addressTwo;
        private double price;
        @JsonProperty("sqm_price")
        private double sqmPrice;
        @JsonProperty("avg_annual_rent_from")
        private double averageAnnualRentFrom;
        @JsonProperty("avg_annual_rent_to")
        private double averageAnnualRentTo;
        @JsonProperty("down_payment")
        private String downPayment;
        @JsonProperty("project_type")
        private String projectType;
        private String description;
        private String thumbnail;
        private Double grr;
        @JsonProperty("introductions")
        private List<ProjectIntroduction> projectIntro;
        @JsonProperty("galleries")
        private List<ProjectGallery> projectGalleries;
        @JsonProperty("property_types")
        private List<PropertyType> propertyTypes;
        @JsonProperty("tower_type")
        private List<TowerType> towerTypes;
        private boolean favorite;
        @JsonProperty("built_date")
        private Date builtDate;
        @JsonProperty("completed_date")
        private Date completedDate;
        private UserRepo.User user;

        public UserRepo.User getUser() {
            return user;
        }

        public void setUser(UserRepo.User user) {
            this.user = user;
        }

        public String getThumbnail() {
            if (thumbnail != null)
                return Url.projectThumbnailUrl + thumbnail;
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public double getSqmPrice() {
            return sqmPrice;
        }

        public void setSqmPrice(double sqmPrice) {
            this.sqmPrice = sqmPrice;
        }

        public String getRentOrBuy() {
            return rentOrBuy;
        }

        public void setRentOrBuy(String rentOrBuy) {
            this.rentOrBuy = rentOrBuy;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public List<TowerType> getTowerTypes() {
            return towerTypes;
        }

        public void setTowerTypes(List<TowerType> towerTypes) {
            this.towerTypes = towerTypes;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAddressOne() {
            return addressOne;
        }

        public void setAddressOne(String addressOne) {
            this.addressOne = addressOne;
        }

        public String getAddressTwo() {
            return addressTwo;
        }

        public void setAddressTwo(String addressTwo) {
            this.addressTwo = addressTwo;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getAverageAnnualRentFrom() {
            return averageAnnualRentFrom;
        }

        public void setAverageAnnualRentFrom(double averageAnnualRentFrom) {
            this.averageAnnualRentFrom = averageAnnualRentFrom;
        }

        public double getAverageAnnualRentTo() {
            return averageAnnualRentTo;
        }

        public void setAverageAnnualRentTo(double averageAnnualRentTo) {
            this.averageAnnualRentTo = averageAnnualRentTo;
        }

        public String getDownPayment() {
            return downPayment;
        }

        public void setDownPayment(String downPayment) {
            this.downPayment = downPayment;
        }

        public String getProjectType() {
            return projectType;
        }

        public void setProjectType(String projectType) {
            this.projectType = projectType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getGrr() {
            return grr;
        }

        public void setGrr(Double grr) {
            this.grr = grr;
        }

        public List<ProjectIntroduction> getProjectIntro() {
            return projectIntro;
        }

        public void setProjectIntro(List<ProjectIntroduction> projectIntro) {
            this.projectIntro = projectIntro;
        }

        public List<ProjectGallery> getProjectGalleries() {
            return projectGalleries;
        }

        public void setProjectGalleries(List<ProjectGallery> projectGalleries) {
            this.projectGalleries = projectGalleries;
        }

        public List<PropertyType> getPropertyTypes() {
            return propertyTypes;
        }

        public void setPropertyTypes(List<PropertyType> propertyTypes) {
            this.propertyTypes = propertyTypes;
        }

        public boolean isFavorite() {
            return favorite;
        }

        public void setFavorite(boolean favorite) {
            this.favorite = favorite;
        }

        public Date getBuiltDate() {
            return builtDate;
        }

        public void setBuiltDate(Date builtDate) {
            this.builtDate = builtDate;
        }

        public Date getCompletedDate() {
            return completedDate;
        }

        public void setCompletedDate(Date completedDate) {
            this.completedDate = completedDate;
        }
    }

    class TowerType {
        private int id;
        @JsonIgnore
        private int projectID;
        @ApiModelProperty(example = "Tower A")
        private String type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getProjectID() {
            return projectID;
        }

        public void setProjectID(int projectID) {
            this.projectID = projectID;
        }
    }

    class ProjectGallery {
        private int id;
        @JsonProperty("url")
        private String image;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImage() {
            if (image != null)
                return Url.projectGalleryUrl + image;
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

    }

    class PropertyType {
        private int id;
        @ApiModelProperty(example = "Flat E0")
        private String type;
        @ApiModelProperty(example = "2")
        private Integer floor;
        @ApiModelProperty(example = "20")
        private Double width;
        @ApiModelProperty(example = "40")
        private Double height;
        @ApiModelProperty(example = "2")
        private Integer bedroom;
        @ApiModelProperty(example = "1")
        private Integer bathroom;
        @ApiModelProperty(example = "1")
        private Integer parking;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getFloor() {
            return floor;
        }

        public void setFloor(Integer floor) {
            this.floor = floor;
        }

        public Double getWidth() {
            return width;
        }

        public void setWidth(Double width) {
            this.width = width;
        }

        public Double getHeight() {
            return height;
        }

        public void setHeight(Double height) {
            this.height = height;
        }

        public Integer getBedroom() {
            return bedroom;
        }

        public void setBedroom(Integer bedroom) {
            this.bedroom = bedroom;
        }

        public Integer getBathroom() {
            return bathroom;
        }

        public void setBathroom(Integer bathroom) {
            this.bathroom = bathroom;
        }

        public Integer getParking() {
            return parking;
        }

        public void setParking(Integer parking) {
            this.parking = parking;
        }

    }

    class ProjectIntroduction {
        private int id;
        @ApiModelProperty(example = "金融核心：玛卡拉区")
        private String name;
        @ApiModelProperty(example = "<div id=\"xmjs\" class=\"lease_intro_con\" style=\"margin: 0px; padding: 0px; border: 0px; vertical-align: baseline; color: rgb(83, 83, 83); font-family: &quot;Microsoft YaHei&quot;;\"><div class=\"intro_con_item\" style=\"margin: 0px; padding: 0px 0px 20px; border: 0px; vertical-align: baseline; line-height: 25px;\"><div id=\"xmjs\" class=\"lease_intro_con\" style=\"margin: 0px; padding: 0px; border: 0px; vertical-align: baseline;\"><div class=\"intro_con_item\" style=\"margin: 0px; padding: 0px 0px 20px; border: 0px; vertical-align: baseline; line-height: 25px;\"><ul style=\"margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none; letter-spacing: 1px; line-height: 20px;\"><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none;\"><p style=\"margin-bottom: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; color: rgb(83, 83, 83); line-height: 20px;\"><span style=\"font-size: 14px;\">金边市12个行政区内面积最小的一个，却是整个金边心脏地区，首相府坐落其中，生活配套最为完善！地区外资机构云集，外国工作人员较多，深受外国投资客青睐！出租公寓紧缺。</span></p><p style=\"margin-bottom: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; color: rgb(83, 83, 83); line-height: 20px;\">&nbsp;</p><ul style=\"margin-right: 0px; margin-left: 0px; padding: 0px; border: 0px; font-size: 14px; vertical-align: baseline; list-style: none; color: rgb(83, 83, 83); letter-spacing: normal;\"><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none; color: rgb(83, 83, 83); letter-spacing: 1px; line-height: 20px; text-align: center;\"><img src=\"http://www.shitonghk.com/d/file/2017/09/10/646da3c9d924e0ed273bd923020e17d6.jpg\" alt=\"2.jpg\" width=\"790\" height=\"380\" style=\"margin: 0px; padding: 0px; vertical-align: baseline;\"></li></ul><p style=\"margin-bottom: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; color: rgb(83, 83, 83); line-height: 20px;\">&nbsp;</p><p style=\"margin-bottom: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; color: rgb(83, 83, 83); line-height: 20px;\"><span style=\"font-size: 14px;\">玛卡拉区域内大型购物商场、传统市场并存，购物消费等生活配套完整，区内还有国际级的大型运动公园、高级外语学校（包含新加坡国际学校及中文学校等）及甲级的崭新办公大楼及住宅大楼，在治安部分，区内分别有古巴及德国大使馆等外使馆，治安十分严谨。</span></p><p style=\"margin-bottom: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; color: rgb(83, 83, 83); line-height: 20px;\"><span style=\"font-size: 14px;\"><br></span></p><p style=\"margin-bottom: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; color: rgb(83, 83, 83); line-height: 20px;\"><span style=\"font-size: 14px;\"><br></span></p><ul style=\"margin-right: 0px; margin-left: 0px; padding: 0px; border: 0px; vertical-align: baseline; list-style: none; color: rgb(83, 83, 83); line-height: 20px;\"><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none;\"><span style=\"font-size: 14px;\">总理府坐落其中</span></li><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none;\"><span style=\"font-size: 14px;\">银行云集，柬埔寨的金融区</span></li><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none;\"><span style=\"font-size: 14px;\">云集多国大使馆</span></li><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none;\"><span style=\"font-size: 14px;\">旅游局</span></li><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none;\"><span style=\"font-size: 14px;\">拥有大型购物商场、传统市场</span></li><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none;\"><span style=\"font-size: 14px;\">国际级的大型运动公园</span></li><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none;\"><span style=\"font-size: 14px;\">多所国际级学校</span></li><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none;\"><span style=\"font-size: 14px;\">火车站以及金边国际机场</span></li><li style=\"margin: 0px; padding: 0px; border: 0px; font-size: 16px; vertical-align: baseline; list-style: none;\"><span style=\"font-size: 14px;\">A级崭新写字楼及公寓</span></li></ul></li></ul></div></div></div></div>")
        private String description;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    class ProjectInsertRequest {
        private int id;
        @ApiModelProperty(example = "New Project Available")
        @JsonProperty("title")
        private String name;
        @ApiModelProperty(example = "2019-03-15")
        @JsonProperty("built_date")
        private Date builtDate;
        @ApiModelProperty(example = "2020-03-15")
        @JsonProperty("completed_date")
        private Date completedDate;

        @JsonProperty("project_type_id")
        private Integer projectTypeID;

        @JsonIgnore
        private String projectType;

        @JsonProperty("country")
        private String country;

        @NotNull
        @ApiModelProperty(example = "0.5")
        private double grr;
        @ApiModelProperty(example = "5000")
        @JsonProperty("down_payment")
        private String downPayment;
        @ApiModelProperty(example = "<p style=\"margin-bottom: 1.25em; color: rgb(51, 63, 72); font-family: Museo-Sans-300, &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px;\">Recent falls in house prices in major capital cities appear to have plateaued according to&nbsp;RBA Governor Philip Lowe.</p><p style=\"margin-bottom: 1.25em; color: rgb(51, 63, 72); font-family: Museo-Sans-300, &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px;\">“…in some markets the rate of price decline has slowed and auction clearance rates have increased,” he says.</p><div id=\"outstream_holder_news\" style=\"color: rgb(51, 63, 72); font-family: Museo-Sans-300, &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px; margin: auto;\"></div><p style=\"margin-bottom: 1.25em; color: rgb(51, 63, 72); font-family: Museo-Sans-300, &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px;\">“Mortgage rates remain low and there is strong competition for borrowers of high credit quality.”</p>")
        private String description;
        @JsonProperty("country_id")
        private int countryID;
        @ApiModelProperty(example = "#50, Corner of St. 516 and St. 335, Sangkat Boeung Kak 1, Khan Tuol Kouk, Phnom Penh, Cambodia.")
        @JsonProperty("address_1")
        private String addressOne;
        @ApiModelProperty(example = "#50, Corner of St. 516 and St. 335, Sangkat Boeung Kak 1, Khan Tuol Kouk, Phnom Penh, Cambodia.")
        @JsonProperty("address_2")
        private String addressTwo;
        @ApiModelProperty(example = "Phnom Penh")
        private String city;
        @ApiModelProperty(example = "600000")
        private double price;
        @ApiModelProperty(example = "2000")
        @JsonProperty("avg_annual_rent_from")
        private double avgRentFrom;
        @ApiModelProperty(example = "Rent", allowableValues = "rent,buy")
        @JsonProperty("rent_or_buy")
        private String rentOrBuy;
        @ApiModelProperty(example = "5000")
        @JsonProperty("avg_annual_rent_to")
        private double avgRentTo;
        @ApiModelProperty(example = "200")
        @JsonProperty("sqm_price")
        private double sqmPrice;
        @JsonProperty("introductions")
        private List<ProjectIntroduction> projectIntroductions;
        @JsonProperty("property_types")
        private List<PropertyType> propertyTypes;
        @JsonProperty("tower_types")
        private List<TowerType> towerTypes;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getBuiltDate() {
            return builtDate;
        }

        public void setBuiltDate(Date builtDate) {
            this.builtDate = builtDate;
        }

        public Date getCompletedDate() {
            return completedDate;
        }

        public void setCompletedDate(Date completedDate) {
            this.completedDate = completedDate;
        }

        public Integer getProjectTypeID() {
            return projectTypeID;
        }

        public void setProjectTypeID(Integer projectTypeID) {
            this.projectTypeID = projectTypeID;
        }

        public String getProjectType() {
            return projectType;
        }

        public void setProjectType(String projectType) {
            this.projectType = projectType;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public double getGrr() {
            return grr;
        }

        public void setGrr(double grr) {
            this.grr = grr;
        }

        public String getDownPayment() {
            return downPayment;
        }

        public void setDownPayment(String downPayment) {
            this.downPayment = downPayment;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCountryID() {
            return countryID;
        }

        public void setCountryID(int countryID) {
            this.countryID = countryID;
        }

        public String getAddressOne() {
            return addressOne;
        }

        public void setAddressOne(String addressOne) {
            this.addressOne = addressOne;
        }

        public String getAddressTwo() {
            return addressTwo;
        }

        public void setAddressTwo(String addressTwo) {
            this.addressTwo = addressTwo;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getAvgRentFrom() {
            return avgRentFrom;
        }

        public void setAvgRentFrom(double avgRentFrom) {
            this.avgRentFrom = avgRentFrom;
        }

        public String getRentOrBuy() {
            return rentOrBuy;
        }

        public void setRentOrBuy(String rentOrBuy) {
            this.rentOrBuy = rentOrBuy;
        }

        public double getAvgRentTo() {
            return avgRentTo;
        }

        public void setAvgRentTo(double avgRentTo) {
            this.avgRentTo = avgRentTo;
        }

        public double getSqmPrice() {
            return sqmPrice;
        }

        public void setSqmPrice(double sqmPrice) {
            this.sqmPrice = sqmPrice;
        }

        public List<ProjectIntroduction> getProjectIntroductions() {
            return projectIntroductions;
        }

        public void setProjectIntroductions(List<ProjectIntroduction> projectIntroductions) {
            this.projectIntroductions = projectIntroductions;
        }

        public List<PropertyType> getPropertyTypes() {
            return propertyTypes;
        }

        public void setPropertyTypes(List<PropertyType> propertyTypes) {
            this.propertyTypes = propertyTypes;
        }

        public List<TowerType> getTowerTypes() {
            return towerTypes;
        }

        public void setTowerTypes(List<TowerType> towerTypes) {
            this.towerTypes = towerTypes;
        }
    }

    class ProjectRequest {
        private int id;
        @ApiModelProperty(example = "New Project Available")
        @JsonProperty("title")
        private String name;
        @ApiModelProperty(example = "2019-03-15")
        @JsonProperty("built_date")
        private Date builtDate;
        @ApiModelProperty(example = "2020-03-15")
        @JsonProperty("completed_date")
        private Date completedDate;
        @JsonIgnore
        private Integer projectTypeID;
        @ApiModelProperty(example = "Borey")
        @JsonProperty("project_type")
        private String projectType;
        @ApiModelProperty(example = "Cambodia")
        @JsonProperty("country")
        private String country;
        @NotNull
        @ApiModelProperty(example = "0.5")
        private double grr;
        @ApiModelProperty(example = "5000")
        @JsonProperty("down_payment")
        private String downPayment;
        @ApiModelProperty(example = "<p style=\"margin-bottom: 1.25em; color: rgb(51, 63, 72); font-family: Museo-Sans-300, &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px;\">Recent falls in house prices in major capital cities appear to have plateaued according to&nbsp;RBA Governor Philip Lowe.</p><p style=\"margin-bottom: 1.25em; color: rgb(51, 63, 72); font-family: Museo-Sans-300, &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px;\">“…in some markets the rate of price decline has slowed and auction clearance rates have increased,” he says.</p><div id=\"outstream_holder_news\" style=\"color: rgb(51, 63, 72); font-family: Museo-Sans-300, &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px; margin: auto;\"></div><p style=\"margin-bottom: 1.25em; color: rgb(51, 63, 72); font-family: Museo-Sans-300, &quot;Helvetica Neue&quot;, Helvetica, Arial, sans-serif; font-size: 16px;\">“Mortgage rates remain low and there is strong competition for borrowers of high credit quality.”</p>")
        private String description;
        @JsonIgnore
        private int countryID;
        @ApiModelProperty(example = "#50, Corner of St. 516 and St. 335, Sangkat Boeung Kak 1, Khan Tuol Kouk, Phnom Penh, Cambodia.")
        @JsonProperty("address_1")
        private String addressOne;
        @ApiModelProperty(example = "#50, Corner of St. 516 and St. 335, Sangkat Boeung Kak 1, Khan Tuol Kouk, Phnom Penh, Cambodia.")
        @JsonProperty("address_2")
        private String addressTwo;
        @ApiModelProperty(example = "Phnom Penh")
        private String city;
        @ApiModelProperty(example = "600000")
        private double price;
        @ApiModelProperty(example = "2000")
        @JsonProperty("avg_annual_rent_from")
        private double avgRentFrom;
        @ApiModelProperty(example = "Rent", allowableValues = "rent,buy")
        @JsonProperty("rent_or_buy")
        private String rentOrBuy;
        @ApiModelProperty(example = "5000")
        @JsonProperty("avg_annual_rent_to")
        private double avgRentTo;
        @ApiModelProperty(example = "200")
        @JsonProperty("sqm_price")
        private double sqmPrice;
        @JsonProperty("introductions")
        private List<ProjectIntroduction> projectIntroductions;
        @JsonProperty("property_types")
        private List<PropertyType> propertyTypes;
        @JsonProperty("tower_types")
        private List<TowerType> towerTypes;

        public double getSqmPrice() {
            return sqmPrice;
        }

        public void setSqmPrice(double sqmPrice) {
            this.sqmPrice = sqmPrice;
        }

        public String getRentOrBuy() {
            return rentOrBuy;
        }

        public void setRentOrBuy(String rentOrBuy) {
            this.rentOrBuy = rentOrBuy;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getBuiltDate() {
            return builtDate;
        }

        public void setBuiltDate(Date builtDate) {
            this.builtDate = builtDate;
        }

        public Date getCompletedDate() {
            return completedDate;
        }

        public void setCompletedDate(Date completedDate) {
            this.completedDate = completedDate;
        }

        public Integer getProjectTypeID() {
            return projectTypeID;
        }

        public void setProjectTypeID(Integer projectTypeID) {
            this.projectTypeID = projectTypeID;
        }

        public String getProjectType() {
            return projectType;
        }

        public void setProjectType(String projectType) {
            this.projectType = projectType;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public double getGrr() {
            return grr;
        }

        public void setGrr(double grr) {
            this.grr = grr;
        }

        public String getDownPayment() {
            return downPayment;
        }

        public void setDownPayment(String downPayment) {
            this.downPayment = downPayment;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCountryID() {
            return countryID;
        }

        public void setCountryID(int countryID) {
            this.countryID = countryID;
        }

        public String getAddressOne() {
            return addressOne;
        }

        public void setAddressOne(String addressOne) {
            this.addressOne = addressOne;
        }

        public String getAddressTwo() {
            return addressTwo;
        }

        public void setAddressTwo(String addressTwo) {
            this.addressTwo = addressTwo;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getAvgRentFrom() {
            return avgRentFrom;
        }

        public void setAvgRentFrom(double avgRentFrom) {
            this.avgRentFrom = avgRentFrom;
        }

        public double getAvgRentTo() {
            return avgRentTo;
        }

        public void setAvgRentTo(double avgRentTo) {
            this.avgRentTo = avgRentTo;
        }

        public List<ProjectIntroduction> getProjectIntroductions() {
            return projectIntroductions;
        }

        public void setProjectIntroductions(List<ProjectIntroduction> projectIntroductions) {
            this.projectIntroductions = projectIntroductions;
        }

        public List<PropertyType> getPropertyTypes() {
            return propertyTypes;
        }

        public void setPropertyTypes(List<PropertyType> propertyTypes) {
            this.propertyTypes = propertyTypes;
        }

        public List<TowerType> getTowerTypes() {
            return towerTypes;
        }

        public void setTowerTypes(List<TowerType> towerTypes) {
            this.towerTypes = towerTypes;
        }
    }
}
