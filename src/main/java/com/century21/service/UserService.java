package com.century21.service;

import com.century21.repository.UserRepo;
import javafx.scene.control.TextFormatter;

public interface UserService {
    void sendMail(String email);
    void changePassword(UserRepo.ChangePassword changePassword);
}
