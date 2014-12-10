package org.magnum.mobilecloud.video.controller;

import org.magnum.mobilecloud.video.core.AuthInfo;
import org.magnum.mobilecloud.video.core.UserInfo;
import org.magnum.mobilecloud.video.repository.UserDetailsRepository;
import org.magnum.mobilecloud.video.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.security.Principal;

/**
 * Created by cong on 11/14/14.
 */
@Controller
public class UserDetailController {

    public static final String AUTH_SVC_PATH = "/auth";

    @Autowired
    UserDetailsRepository userDetailsRepository;
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = AUTH_SVC_PATH + "/register", method = RequestMethod.POST)
    public @ResponseBody
    AuthInfo addUser(@RequestBody AuthInfo user) {
        AuthInfo result = userDetailsRepository.save(user);
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(user.getUsername());
        userInfo.setPoints(0);
        userRepository.save(userInfo);
        return result;
    }
    @RequestMapping(value = AUTH_SVC_PATH + "/changePass", method = RequestMethod.POST)
    public @ResponseBody
    AuthInfo changePassword(@RequestParam("oldPassword") String oldPassword,
                            @RequestParam("newPassowrd") String newPassword,
                            Principal principal) throws AuthenticationException {
        AuthInfo authInfo = userDetailsRepository.findOne(principal.getName());

        if (authInfo == null) {
            throw new IllegalStateException("Current user doesn't exist in database.");
        }
        if (oldPassword.equals(authInfo.getPassword())) {
            return userDetailsRepository.save(new AuthInfo(authInfo.getUsername(),newPassword,authInfo.getAuthorities()));
        }else {
            throw new AuthenticationException("Wrong password!");
        }


    }
}
