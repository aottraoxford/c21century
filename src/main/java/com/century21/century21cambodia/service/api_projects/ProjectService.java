package com.century21.century21cambodia.service.api_projects;

import com.century21.century21cambodia.model.Pagination;
import com.century21.century21cambodia.repository.api_projects.CountryForWeb;
import com.century21.century21cambodia.repository.api_projects.Project;

import java.util.List;

public interface ProjectService {
    List<Project> projects(int countryID,int projectTypeID,boolean status,Pagination pagination);
    List<CountryForWeb> getProjectsFroWeb(int limit,int offset);
}
