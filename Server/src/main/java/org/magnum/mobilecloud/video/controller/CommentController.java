package org.magnum.mobilecloud.video.controller;

import org.magnum.mobilecloud.video.core.Comment;
import org.magnum.mobilecloud.video.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

/**
 * Created by cong on 11/13/14.
 */
@Controller
public class CommentController {
    // The path where we expect the GiftSvc to live
    public static final String GIFT_SVC_PATH = "/gifts";

    public static final String COMMENT_SVC_PATH = "/comments";
    @Autowired
    CommentRepository commentRepository;

    @RequestMapping(value = GIFT_SVC_PATH + "/{giftId}/comments", method = RequestMethod.POST)
    public
    @ResponseBody
    Comment addGiftComment(@PathVariable String giftId, @RequestBody Comment comment,Principal principal) throws IOException {
        String id = UUID.randomUUID().toString();
        comment.setId(id);
        comment.setGiftId(giftId);
        comment.setCreator(principal.getName());
        Comment result = commentRepository.save(comment);
        return result;
    }


    @RequestMapping(value = GIFT_SVC_PATH + "/{id}/comments", method = RequestMethod.GET)
    public
    @ResponseBody
    Iterable<Comment> getGiftComment(@PathVariable String id) throws IOException {
        return commentRepository.findByGiftId(id);
    }

    @RequestMapping(value = COMMENT_SVC_PATH , method = RequestMethod.GET)
    public
    @ResponseBody
    Iterable<Comment> getComments() throws IOException {
        return commentRepository.findAll();
    }

    @RequestMapping(value = COMMENT_SVC_PATH + "/{id}/comments", method = RequestMethod.GET)
    public
    @ResponseBody
    Iterable<Comment> getChildComment(@PathVariable String id) throws IOException {
        return commentRepository.findByParentId(id);
    }
}
