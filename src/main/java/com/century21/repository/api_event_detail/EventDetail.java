package com.century21.repository.api_event_detail;

import com.century21.util.Url;

public class EventDetail {
    private int id;
    private String title;
    private String banner;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBanner() {
        if(banner!=null) return Url.bannerUrl+banner;
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EventDetail{" +
                "id=" + id +
                ", banner='" + banner + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}