package org.magnum.mobilecloud.video.repository;

import org.magnum.mobilecloud.video.core.AuthInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by cong on 11/14/14.
 */
public interface UserDetailsRepository extends MongoRepository<AuthInfo,String> {
}
