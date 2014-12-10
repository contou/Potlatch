package org.magnum.mobilecloud.video.controller;

import org.magnum.mobilecloud.video.core.Gift;
import org.magnum.mobilecloud.video.core.GiftStatus;
import org.magnum.mobilecloud.video.core.UserInfo;
import org.magnum.mobilecloud.video.repository.GiftRepository;
import org.magnum.mobilecloud.video.repository.UserRepository;
import org.magnum.mobilecloud.video.util.GiftFileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;


/**
 * Created by cong on 9/1/14.
 */
@Controller
public class GiftController {

    public static final String TITLE_PARAMETER = "title";

    // The path where we expect the GiftSvc to live
    public static final String GIFT_SVC_PATH = "/gifts";

    public static final String CATEGORY_SVC_PATH = "/category";

    // The path to search gifts by title
    public static final String GIFT_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/search/findByName";

    // The path to search gifts by category
    public static final String GIFT_CATEGORY_SEARCH_PATH = GIFT_SVC_PATH + "/search/findByCategory";


    @Autowired
    GiftRepository giftRepository;

    @Autowired
    UserRepository userRepository;


    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Liking a gift twice")  // 400
    public class likingTwiceException extends RuntimeException {
        // ...
    }


    @RequestMapping(value = GIFT_SVC_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    Iterable<Gift> getGiftList() {
        return giftRepository.findAll();
    }

    // Receives GET requests to /video/{category} and returns the current
    // list of videos that are part of the specified category.
    @RequestMapping(value = GIFT_CATEGORY_SEARCH_PATH , method = RequestMethod.GET)
    public
    @ResponseBody
    Iterable<Gift> getGiftListForCategory(@RequestParam("category") String categoryName) {
        return giftRepository.findByCategory(categoryName);
    }

    @RequestMapping(value = GIFT_SVC_PATH + "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    Gift getGiftById(@PathVariable("id") String id) {
        return giftRepository.findOne(id);
    }



    @RequestMapping(value = GIFT_SVC_PATH, method = RequestMethod.POST)
    public
    @ResponseBody
    Gift addGift(@RequestBody Gift v,Principal principal) {

//        if(categoryRepository.findOne(v.getCategory()) == null){
//            throw new RuntimeException("Unknown category:"+v.getCategory());
//        }
        if(v.getCategory() == null) {
            v.setCategory("default_category");
        }

        String id = UUID.randomUUID().toString();
        v.setId(id);
        v.setImage("/gifts/" + id + "/image");
        v.setCreatorId(principal.getName());
        Gift result = giftRepository.save(v);
        return result;
    }


    @RequestMapping(value = GIFT_TITLE_SEARCH_PATH+"/{title}", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Gift> findByTitle(@PathVariable(TITLE_PARAMETER) String title) {
//        return giftRepository.findAll();
        return giftRepository.findByTitle(title);
    }


    @RequestMapping(value = GIFT_SVC_PATH + "/{id}/like", method = RequestMethod.POST)
    public
    @ResponseBody
    Gift toggleLikeGift(@PathVariable("id") String id, Principal p) {
        Gift v = giftRepository.findOne(id);
        if (v == null) {
            throw new ResourceNotFoundException();
        }
        Set<String> likeBy = v.getLikesUsernames();
        UserInfo owner = userRepository.findOne(v.getCreatorId());
        if(owner != null) {
            final String user = p.getName();
            if (likeBy.contains(user)) {
                likeBy.remove(user);
                owner.setPoints(owner.getPoints() - 1);
            } else {
                likeBy.add(user);
                owner.setPoints(owner.getPoints() + 1);
            }
        } else {
            final String user = p.getName();
            if (likeBy.contains(user)) {
                likeBy.remove(user);
            } else {
                likeBy.add(user);
            }
        }
        v.setLikesUsernames(likeBy);
        v.setPoints(likeBy.size());
        Gift result = giftRepository.save(v);
        return result;
    }


    @RequestMapping(value = GIFT_SVC_PATH + "/{id}/unlike", method = RequestMethod.POST)
    public
    @ResponseBody
    void unlikeGift(@PathVariable("id") String id, Principal p) {

        Gift v = giftRepository.findOne(id);
        if (v == null) {
            throw new ResourceNotFoundException();
        }
        Set<String> likeBy = v.getLikesUsernames();
        boolean alreadyLike = likeBy.remove(p.getName());
        if (alreadyLike) {
            v.setLikesUsernames(likeBy);
            v.setPoints(likeBy.size());
            giftRepository.save(v);
        } else {
            throw new likingTwiceException();
        }
    }


    @RequestMapping(value = GIFT_SVC_PATH + "/{id}/likedby", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<String> getUsersWhoLikedGift(@PathVariable("id") String id, Principal p) {
        return giftRepository.findOne(id).getLikesUsernames();
    }


    @RequestMapping(value = GIFT_SVC_PATH + "/{id}/image", method = RequestMethod.POST)
    public
    @ResponseBody
    GiftStatus setGiftImage(
            @PathVariable("id") String id,
            @RequestParam("image") MultipartFile giftData) throws IOException {
        try {

            Gift gift = giftRepository.findOne(id);
            GiftFileManager.get().saveGiftData(gift, giftData.getInputStream());

            return new GiftStatus(GiftStatus.GiftState.READY);

        } catch (NullPointerException e) {

            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = GIFT_SVC_PATH + "/{id}/image", method = RequestMethod.GET)
    public void getGiftImage(@PathVariable String id, HttpServletResponse httpServletResponse) throws IOException {
        try {
            Gift gift = giftRepository.findOne(id);
            httpServletResponse.setHeader("Content-Type", "image/*");
            GiftFileManager.get().copyGiftData(gift, httpServletResponse.getOutputStream());
        } catch (NullPointerException e) {
            throw new ResourceNotFoundException();
        }
    }
}
