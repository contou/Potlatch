package org.magnum.mobilecloud.video.controller;

import org.magnum.mobilecloud.video.core.UserInfo;
import org.magnum.mobilecloud.video.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

/**
 * Created by cong on 11/27/14.
 */
@Controller
public class UserController {
    public static final String USER_SVC_PATH = "/users";

    @Autowired
    UserRepository userRepository;


    @RequestMapping(value = USER_SVC_PATH, method = RequestMethod.GET)
    public @ResponseBody
    List<UserInfo> getUsers() {
        List<UserInfo> result = userRepository.findAll();
        return result;
    }

    @RequestMapping(value = USER_SVC_PATH+"/{username}", method = RequestMethod.GET)
    public @ResponseBody
    UserInfo getUser(@RequestBody String username) {
        UserInfo result = userRepository.findOne(username);
        return result;
    }

    @RequestMapping(value = USER_SVC_PATH, method = RequestMethod.PUT)
    public @ResponseBody
    UserInfo updateUser(@RequestBody UserInfo user,Principal principal) {
        user.setUsername(principal.getName());
        UserInfo result = userRepository.save(user);
        return result;
    }

    @RequestMapping(value = USER_SVC_PATH + "/topUser", method = RequestMethod.GET)
    public @ResponseBody
    java.util.Iterator<UserInfo> getTopUser() {
//        List<UserInfo> result = userInfoRepository.findAll(new Sort(Sort.Direction.DESC, "points"));
        Page<UserInfo> result = userRepository.findAll(new PageRequest(0,10, Sort.Direction.DESC,"points"));

        return result.iterator();
    }
}
