package org.magnum.mobilecloud.video.repository;

import org.magnum.mobilecloud.video.core.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by cong on 11/13/14.
 */

public interface CategoryRepository extends MongoRepository<Category,String> {
}
