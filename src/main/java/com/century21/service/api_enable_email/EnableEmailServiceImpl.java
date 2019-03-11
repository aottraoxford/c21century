//package com.century21.service.api_enable_email;
//
//import com.century21.exception.CustomRuntimeException;
//import com.century21.repository.UserRepo;
//import com.century21.repository.api_enable_email.EnableEmailRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EnableEmailServiceImpl implements EnableEmailService {
//    @Autowired
//    private EnableEmailRepo enableEmailRepo;
//    @Autowired
//    private UserRepo userRepo;
//    @Override
//    public void enableEmail(String email, int code) {
//        userRepo.removeCode();
//        if(enableEmailRepo.enableEmail(email,code)<1) throw new CustomRuntimeException(404,"code not match");
//    }
//}
