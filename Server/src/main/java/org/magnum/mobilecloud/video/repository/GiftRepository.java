package org.magnum.mobilecloud.video.repository;

import org.magnum.mobilecloud.video.core.Gift;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

/**
 * Created by cong on 9/1/14.
 */
//@RepositoryRestResource

public interface GiftRepository extends MongoRepository<Gift, String> {
    // Find all gifts with a matching title (e.g., Video.name)
    public List<Gift> findByTitle(
            // The @Param annotation tells Spring Data Rest which HTTP request
            // parameter it should use to fill in the "title" variable used to
            // search for Videos
           String title);
//
//    // Find all Gifts with a matching category
    public List<Gift> findByCategory(
            // The @Param annotation tells Spring Data Rest which HTTP request
            // parameter it should use to fill in the "title" variable used to
            // search for Videos
            String category);

}
