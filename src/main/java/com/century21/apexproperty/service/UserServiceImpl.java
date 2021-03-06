package com.century21.apexproperty.service;

import com.century21.apexproperty.exception.CustomRuntimeException;
import com.century21.apexproperty.model.Pagination;
import com.century21.apexproperty.model.request.EnableEmail;
import com.century21.apexproperty.util.MailService;
import com.century21.apexproperty.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MailService mailService;

    @Override
    public void verification(EnableEmail enableEmail) {
        if (userRepo.updateEnable(enableEmail.getCode(), enableEmail.getEmail()) < 1) {
            throw new CustomRuntimeException(401, "VERIFY CODE NOT MATCH.");
        }
        userRepo.removeEmail(enableEmail.getEmail());
    }

    @Override
    public void sendMail(String email) {
        if (userRepo.findUserIDByEmail(email) == null) throw new CustomRuntimeException(404, "EMAIL NOT YET REGISTER.");
        UserRepo.User user = userRepo.findUserByEmail(email);

        //random number to verify email and save to database
        int code = (int) (Math.random() * 89999) + 10000;

        String mailTemplate = "" +
                "<div style=\"height:100px\"><img style=\"height:100px; width:100px; float:right;\" src=\"https://app.c21apex.com/api/event/banner/c21apex-icon.png\"/></div>" +
                "<div>" +
                "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>" +
                "<br/></br><p>You recently requested a password reset for your C21 Apex Property account.To complete the process, enter the code below on the app.</p>" +
                "<h1>" + code + "</h1>" +
                "<p>If you did not make this request, you can ignore this email or if you believe an unauthorized person has accessed your account, please contact C21 Apex Property support. </p>" +
                "<br/></br><p>C21 Apex Property</p>" +
                "</div>";

//        //send mail to email
//        mailTemplate+=code+"</h1><h6>NOTE: This CODE will be invalid after 2 minutes then u require to request code again.</h6><p>If you don't register for user account with CENTURY 21 CAMBODIA,simply ignore this email. No action will be taken.<h1></h1> Take care.<h1></h1>CENTURY 21 CAMBODIA</p></body></html>";
        mailService.sendMail("CENTURY 21 APEX PROPERTY", email, mailTemplate);
        userRepo.insertVerification(email, code);
    }

    @Override
    public void changePassword(UserRepo.ChangePassword changePassword) {
        if (userRepo.findUserIDByEmail(changePassword.getEmail()) == null)
            throw new CustomRuntimeException(404, "EMAIL NOT EXIST.");
        userRepo.removeCode();
        if (userRepo.checkAccount(changePassword.getEmail()) < 1) {
            throw new CustomRuntimeException(404, "Email not yet verify.");
        }
        userRepo.removeEmail(changePassword.getEmail());
        userRepo.updatePassword(changePassword);
    }

    @Override
    public void assignRole(int userID, String roleType, Principal principal) {
        String role = userRepo.findUserRole(userID);
        if (role == null) throw new CustomRuntimeException(404, "USER NOT EXIST");
        if (role.equalsIgnoreCase("admin")) throw new CustomRuntimeException(400, "THIS USER IS ADMIN");

        Integer roleID = userRepo.roleID(roleType);
        Integer adminID = userRepo.findUserIDByEmail(principal.getName());

        if (roleID == null) throw new CustomRuntimeException(404, "AVAILABLE ROLE( ADMIN, AGENT ,USER)");
        if (roleType.equalsIgnoreCase("agent")) {
            userRepo.updateRole(userID, roleID);
            userRepo.setChild(adminID, userID);
        } else if (roleType.equalsIgnoreCase("user")) {
            userRepo.updateRole(userID, roleID);
            userRepo.setChild(null, userID);
        } else if (roleType.equalsIgnoreCase("admin")) {
            userRepo.updateRole(userID, roleID);
            userRepo.setChild(userID, userID);
        }
    }

    @Override
    public List<UserRepo.User> agents(String name, Principal principal, Pagination pagination) {
        Integer userID = userRepo.findUserIDByEmail(principal.getName());
        List<UserRepo.User> agents = userRepo.agents(name, userID, pagination.getLimit(), pagination.getOffset());
        if (agents == null || agents.size() < 1) throw new CustomRuntimeException(404, "ZERO RESULT");
        pagination.setTotalItem(userRepo.agentsCount(name, userID));
        return agents;
    }

    @Override
    public List<UserRepo.User> findUsers(String name, String role, Pagination pagination) {
        if (name != null && name.trim().length() > 0) {
            name = name.replaceAll(" ", "%");
        }
        List<UserRepo.User> users = userRepo.findUsers(name, role, pagination.getLimit(), pagination.getOffset());
        if (users == null || users.size() < 1) throw new CustomRuntimeException(404, "ZERO RESULT");
        pagination.setTotalItem(userRepo.findUsersCount(name, role));
        return users;
    }

    @Override
    public List<UserRepo.Contact> findContacts(UserRepo.ContactFilter filter, Pagination pagination, Principal principal) {
        Integer userID = userRepo.findUserIDByEmail(principal.getName());
        if (userID == null) throw new CustomRuntimeException(404, "user id not found.");
        String roleType = userRepo.findUserRoleByEmail(principal.getName());
        List<UserRepo.Contact> contacts = userRepo.findAllContact(filter, userID, roleType, pagination.getLimit(), pagination.getOffset());
        if (contacts.size() < 1) throw new CustomRuntimeException(404, "zero result.");
        pagination.setTotalItem(userRepo.findAllContactCount(filter, userID, roleType));
        return contacts;
    }

    @Override
    public List<UserRepo.Question> findQuestions(Pagination pagination) {
        List<UserRepo.Question> questions = userRepo.findQuestions(pagination.getLimit(), pagination.getOffset());
        if (questions == null && questions.size() < 1) throw new CustomRuntimeException(404, "zero result.");
        pagination.setTotalItem(userRepo.findQuestionsCount());
        return questions;
    }

    @Override
    public UserRepo.MailAccount findOneMailAccount() {
        return userRepo.findOneMailAccount();
    }

    @Override
    public UserRepo.MailAccount updateMailAccount(UserRepo.MailAccountRequest request) {
        userRepo.updateMailAccount(request.getEmail(), request.getPassword());
        return userRepo.findOneMailAccount();
    }
}
