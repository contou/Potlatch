package org.magnum.mobilecloud.video;

import java.util.UUID;

import org.magnum.mobilecloud.video.core.AuthInfo;
import org.magnum.mobilecloud.video.core.Gift;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.magnum.mobilecloud.video.core.UserInfo;

/**
 * This is a utility class to aid in the construction of
 * Video objects with random names, urls, and durations.
 * The class also provides a facility to convert objects
 * into JSON using Jackson, which is the format that the
 * VideoSvc controller is going to expect data in for
 * integration testing.
 * 
 * @author jules
 *
 */
public class TestData {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Construct and return a Video object with a
	 * rnadom name, url, and duration.
	 * 
	 * @return
	 */
	public static Gift randomGift() {
		// Information about the video
		// Construct a random identifier using Java's UUID class
		String title = "Video";
        String description = UUID.randomUUID().toString();
        String category = "test";
        String userId = "admin";

		return new Gift(title,category,description, 0,userId);
	}

    public static AuthInfo randomUser() {
        // Information about the video
        // Construct a random identifier using Java's UUID class
        String id = UUID.randomUUID().toString();

        return new AuthInfo(id,id,new String[]{"USER"});
    }

    public static AuthInfo adminUser() {
        // Information about the video
        // Construct a random identifier using Java's UUID class
        String id = UUID.randomUUID().toString();

        return new AuthInfo("admin","pass",new String[]{"ADMIN","USER"});
    }

    public static UserInfo userInfo() {
        // Information about the video
        // Construct a random identifier using Java's UUID class
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("admin");
        userInfo.setGender(1);
        userInfo.setStatus("what's up");

        return userInfo;
    }
	/**
	 *  Convert an object to JSON using Jackson's ObjectMapper
	 *  
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static String toJson(Object o) throws Exception{
		return objectMapper.writeValueAsString(o);
	}
}
