package org.magnum.mobilecloud.video.client;

import java.util.Collection;

import org.magnum.mobilecloud.video.core.*;
import retrofit.client.Response;
import retrofit.http.*;
import retrofit.mime.TypedFile;

/**
 *                       DO NOT MODIFY THIS INTERFACE
                    ___                    ___           ___                            
     _____         /\  \                  /\  \         /\  \                           
    /::\  \       /::\  \                 \:\  \       /::\  \         ___              
   /:/\:\  \     /:/\:\  \                 \:\  \     /:/\:\  \       /\__\             
  /:/  \:\__\   /:/  \:\  \            _____\:\  \   /:/  \:\  \     /:/  /             
 /:/__/ \:|__| /:/__/ \:\__\          /::::::::\__\ /:/__/ \:\__\   /:/__/              
 \:\  \ /:/  / \:\  \ /:/  /          \:\~~\~~\/__/ \:\  \ /:/  /  /::\  \              
  \:\  /:/  /   \:\  /:/  /            \:\  \        \:\  /:/  /  /:/\:\  \             
   \:\/:/  /     \:\/:/  /              \:\  \        \:\/:/  /   \/__\:\  \            
    \::/  /       \::/  /                \:\__\        \::/  /         \:\__\           
     \/__/         \/__/                  \/__/         \/__/           \/__/           
      ___           ___                                     ___                         
     /\  \         /\  \         _____                     /\__\                        
    |::\  \       /::\  \       /::\  \       ___         /:/ _/_         ___           
    |:|:\  \     /:/\:\  \     /:/\:\  \     /\__\       /:/ /\__\       /|  |          
  __|:|\:\  \   /:/  \:\  \   /:/  \:\__\   /:/__/      /:/ /:/  /      |:|  |          
 /::::|_\:\__\ /:/__/ \:\__\ /:/__/ \:|__| /::\  \     /:/_/:/  /       |:|  |          
 \:\~~\  \/__/ \:\  \ /:/  / \:\  \ /:/  / \/\:\  \__  \:\/:/  /      __|:|__|          
  \:\  \        \:\  /:/  /   \:\  /:/  /   ~~\:\/\__\  \::/__/      /::::\  \          
   \:\  \        \:\/:/  /     \:\/:/  /       \::/  /   \:\  \      ~~~~\:\  \         
    \:\__\        \::/  /       \::/  /        /:/  /     \:\__\          \:\__\        
     \/__/         \/__/         \/__/         \/__/       \/__/           \/__/        
 * 
 * 
 * /**
 * This interface defines an API for a GiftSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * interface into a client capable of sending the appropriate
 * HTTP requests.
 * 
 * The HTTP API that you must implement so that this interface
 * will work:
 * 
 * POST /oauth/token
 *    - The access point for the OAuth 2.0 Password Grant flow.
 *    - Clients should be able to submit a request with their username, password,
 *       client ID, and client secret, encoded as described in the OAuth lecture
 *       gifts.
 *    - The client ID for the Retrofit adapter is "mobile" with an empty password.
 *    - There must be 2 users, whose usernames are "user0" and "admin". All passwords 
 *      should simply be "pass".
 *    - Rather than implementing this from scratch, we suggest reusing the example
 *      configuration from the OAuth 2.0 example in GitHub by copying these classes over:
 *      https://github.com/juleswhite/mobilecloud-14/tree/master/examples/9-GiftServiceWithOauth2/src/main/java/org/magnum/mobilecloud/gift/auth
 *      You will need to @Import the OAuth2SecurityConfiguration into your Application or
 *      other configuration class to enable OAuth 2.0. You will also need to remove one
 *      of the containerCustomizer() methods in either OAuth2SecurityConfiguration or
 *      Application (they are the exact same code). You may need to customize the users
 *      in the OAuth2Config constructor or the security applied by the ResourceServer.configure(...) 
 *      method. You should determine what (if any) adaptations are needed by comparing this 
 *      and the test specification against the code in that class.
 *  
 * GET /gift
 *    - Returns the list of gifts that have been added to the
 *      server as JSON. The list of gifts should be persisted
 *      using Spring Data. The list of Gift objects should be able 
 *      to be unmarshalled by the client into a Collection<Gift>.
 *    - The return content-type should be application/json, which
 *      will be the default if you use @ResponseBody
 * 
 *      
 * POST /gift
 *    - The gift metadata is provided as an application/json request
 *      body. The JSON should generate a valid instance of the 
 *      Gift class when deserialized by Spring's default 
 *      Jackson library.
 *    - Returns the JSON representation of the Gift object that
 *      was stored along with any updates to that object made by the server. 
 *    - **_The server should store the Gift in a Spring Data JPA repository.
 *    	 If done properly, the repository should handle generating ID's._** 
 *    - A gift should not have any likes when it is initially created.
 *    - You will need to add one or more annotations to the Gift object
 *      in order for it to be persisted with JPA.
 * 
 * GET /gift/{id}
 *    - Returns the gift with the given id or 404 if the gift is not found.
 *      
 * POST /gift/{id}/like
 *    - Allows a user to like a gift. Returns 200 Ok on success, 404 if the
 *      gift is not found, or 400 if the user has already liked the gift.
 *    - The service should should keep track of which users have liked a gift and
 *      prevent a user from liking a gift twice. A POJO Gift object is provided for 
 *      you and you will need to annotate and/or add to it in order to make it persistable.
 *    - A user is only allowed to like a gift once. If a user tries to like a gift
 *       a second time, the operation should fail and return 400 Bad Request.
 *      
 * POST /gift/{id}/unlike
 *    - Allows a user to unlike a gift that he/she previously liked. Returns 200 OK
 *       on success, 404 if the gift is not found, and a 400 if the user has not 
 *       previously liked the specified gift.
 *       
 * GET /gift/{id}/likedby
 *    - Returns a list of the string usernames of the users that have liked the specified
 *      gift. If the gift is not found, a 404 error should be generated.
 * 
 * GET /gift/search/findByName?title={title}
 *    - Returns a list of gifts whose titles match the given parameter or an empty
 *      list if none are found.
 *     
 * GET /gift/search/findByDurationLessThan?duration={duration}
 *    - Returns a list of gifts whose durations are less than the given parameter or
 *      an empty list if none are found.	
 *     
 *     
 * The GiftSvcApi interface described below should be used as the ultimate ground
 * truth for what should be implemented in the assignment. If there are any details
 * in the description above that conflict with the GiftSvcApi interface below, use
 * the details in the GiftSvcApi interface and report the discrepancy on the course
 * forums. 
 * 
 * For the ultimate ground truth of how the assignment will be graded, please see 
 * AutoGradingTest, which shows the specific tests that will be run to grade your
 * solution. 
 *   
 * @author jules
 *
 *
 */
public interface GiftSvcApi {

	public static final String TITLE_PARAMETER = "title";

    public static final String CATEGORY_PARAMETER = "category";


	public static final String TOKEN_PATH = "/oauth/token";

	// The path where we expect the GiftSvc to live
	public static final String VIDEO_SVC_PATH = "/gifts";

    public static final String CATEGORY_SVC_PATH = "/categories";

	// The path to search gifts by title
	public static final String VIDEO_TITLE_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByName";

    // The path to search gifts by category
    public static final String VIDEO_CATEGORY_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByCategory";

    public static final String USER_SVC_PATH = "/users";

    @POST(USER_SVC_PATH)
    public UserInfo addUser(@Body UserInfo user);

    @GET(USER_SVC_PATH)
    public Collection<UserInfo> getUsers();

    @POST(CATEGORY_SVC_PATH)
    public Category addCategory(@Body Category category);

    @GET(CATEGORY_SVC_PATH)
    public Collection<Category> getCategories();

    @GET(VIDEO_SVC_PATH)
	public Collection<Gift> getGiftList();
	
	@GET(VIDEO_SVC_PATH + "/{id}")
	public Gift getGiftById(@Path("id") String id);
	
	@POST(VIDEO_SVC_PATH)
	public Gift addGift(@Body Gift v);
	
	@POST(VIDEO_SVC_PATH + "/{id}/like")
	public Void likeGift(@Path("id") String id);
	
	@POST(VIDEO_SVC_PATH + "/{id}/unlike")
	public Void unlikeGift(@Path("id") String id);

    @GET(VIDEO_CATEGORY_SEARCH_PATH)
    public Collection<Gift> findByCategory(@Query(CATEGORY_PARAMETER) String title);

	@GET(VIDEO_TITLE_SEARCH_PATH)
	public Collection<Gift> findByTitle(@Query(TITLE_PARAMETER) String title);

	@GET(VIDEO_SVC_PATH + "/{id}/likedby")
	public Collection<String> getUsersWhoLikedGift(@Path("id") String id);

    /**
     * This endpoint allows clients to set the mpeg gift data for previously
     * added Gift objects by sending multipart POST requests to the server.
     * The URL that the POST requests should be sent to includes the ID of the
     * Gift that the data should be associated with (e.g., replace {id} in
     * the url /gift/{id}/data with a valid ID of a gift, such as /gift/1/data
     * -- assuming that "1" is a valid ID of a gift).
     *
     * @return
     */
    @Multipart
    @POST(VIDEO_SVC_PATH + "/{id}/image")
    public GiftStatus setGiftImage(@Path("id") String id, @Part("image") TypedFile giftData);

    /**
     * This endpoint should return the gift data that has been associated with
     * a Gift object or a 404 if no gift data has been set yet. The URL scheme
     * is the same as in the method above and assumes that the client knows the ID
     * of the Gift object that it would like to retrieve gift data for.
     *
     * This method uses Retrofit's @Streaming annotation to indicate that the
     * method is going to access a large stream of data (e.g., the mpeg gift
     * data on the server). The client can access this stream of data by obtaining
     * an InputStream from the Response as shown below:
     *
     * GiftSvcApi client = ... // use retrofit to create the client
     * Response response = client.getData(someGiftId);
     * InputStream giftDataStream = response.getBody().in();
     *
     * @param id
     * @return
     */
    @Streaming
    @GET(VIDEO_SVC_PATH + "/{id}/image")
    Response getGiftImage(@Path("id") String id);


}
