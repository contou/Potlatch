package org.magnum.mobilecloud.video.repository;

import org.magnum.mobilecloud.video.core.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

/**
 * Created by cong on 11/12/14.
 */
public interface CommentRepository extends MongoRepository<Comment, String> {
    public List<Comment> findByGiftId(String id);

    public List<Comment> findByParentId(String id);
}
