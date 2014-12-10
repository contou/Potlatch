package org.magnum.mobilecloud.video.repository;

import org.magnum.mobilecloud.video.core.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by cong on 11/27/14.
 */
public interface UserRepository extends MongoRepository<UserInfo,String> {
}
