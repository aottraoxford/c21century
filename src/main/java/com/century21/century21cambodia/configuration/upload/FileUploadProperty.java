package com.century21.century21cambodia.configuration.upload;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileUploadProperty {
    private String userImage;
    private String projectThumbnail;
    private String projectGallery;

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getProjectThumbnail() {
        return projectThumbnail;
    }

    public void setProjectThumbnail(String projectThumbnail) {
        this.projectThumbnail = projectThumbnail;
    }

    public String getProjectGallery() {
        return projectGallery;
    }

    public void setProjectGallery(String projectGallery) {
        this.projectGallery = projectGallery;
    }

    @Override
    public String toString() {
        return "FileUploadProperty{" +
                "userImage='" + userImage + '\'' +
                ", projectThumbnail='" + projectThumbnail + '\'' +
                ", projectGallery='" + projectGallery + '\'' +
                '}';
    }
}