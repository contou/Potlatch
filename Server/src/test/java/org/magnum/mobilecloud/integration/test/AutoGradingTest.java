package org.magnum.mobilecloud.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import io.magnum.autograder.junit.Rubric;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.magnum.mobilecloud.video.TestData;
import org.magnum.mobilecloud.video.client.GiftSvcApi;

import org.magnum.mobilecloud.video.client.SecuredRestBuilder;
import org.magnum.mobilecloud.video.core.*;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * A test for the Asgn2 gift service
 * 
 * @author mitchell
 */
public class AutoGradingTest {

	private class ErrorRecorder implements ErrorHandler {

		private RetrofitError error;

		@Override
		public Throwable handleError(RetrofitError cause) {
			error = cause;
			return error.getCause();
		}

		public RetrofitError getError() {
			return error;
		}
	}

	private final String TEST_URL = "https://localhost:8443";
    private File testImage = new File(
            "src/test/resources/test.jpg");

	private final String USERNAME1 = "guest";
	private final String USERNAME2 = "guest";
	private final String PASSWORD = "pass";
	private final String CLIENT_ID = "mobile";

	private GiftSvcApi readWriteGiftSvcUser1 = new SecuredRestBuilder()
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setEndpoint(TEST_URL)
			.setLoginEndpoint(TEST_URL + GiftSvcApi.TOKEN_PATH)
			// .setLogLevel(LogLevel.FULL)
			.setUsername(USERNAME1).setPassword(PASSWORD).setClientId(CLIENT_ID)
			.build().create(GiftSvcApi.class);

	private GiftSvcApi readWriteGiftSvcUser2 = new SecuredRestBuilder()
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setEndpoint(TEST_URL)
			.setLoginEndpoint(TEST_URL + GiftSvcApi.TOKEN_PATH)
			// .setLogLevel(LogLevel.FULL)
			.setUsername(USERNAME2).setPassword(PASSWORD).setClientId(CLIENT_ID)
			.build().create(GiftSvcApi.class);

	private Gift gift = TestData.randomGift();
    private UserInfo user = TestData.userInfo();


	@Rubric(value = "Gift data is preserved", 
			goal = "The goal of this evaluation is to ensure that your Spring controller(s) "
			+ "properly unmarshall Gift objects from the data that is sent to them "
			+ "and that the HTTP API for adding gifts is implemented properly. The"
			+ " test checks that your code properly accepts a request body with"
			+ " application/json data and preserves all the properties that are set"
			+ " by the client. The test also checks that you generate an ID and data"
			+ " URL for the uploaded gift.", 
			points = 20.0, 
			reference = "This test is derived from the material in these gifts: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/61 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/97 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 ")
	@Test
	public void testAddGiftMetadata() throws Exception {
		Gift received = readWriteGiftSvcUser1.addGift(gift);
		assertEquals(gift.getTitle(), received.getTitle());
		assertTrue(received.getPoints() == 0);
//		assertTrue(received.getId() > 0);
	}

    @Test
    public void testAddGetUser() throws Exception {
        UserInfo user1 = readWriteGiftSvcUser2.addUser(user);
        user.setUsername("user0");
        UserInfo user0 = readWriteGiftSvcUser2.addUser(user);
        Collection<UserInfo> authInfo = readWriteGiftSvcUser2.getUsers();
        Iterator<UserInfo> userInfos = authInfo.iterator();
        assertTrue(user1.equals(user));
    }

    @Test
    public void testAddGetCategory() throws Exception {
        Category category = new Category();
        category.setName("test");
        Category category1 = readWriteGiftSvcUser1.addCategory(category);
//        assertTrue(category1.equals(category));
        category.setName("test1");
        Category category2 = readWriteGiftSvcUser1.addCategory(category);
        Collection<Category> categories = readWriteGiftSvcUser1.getCategories();

        assertEquals(2,categories.size());
        assertTrue(categories.contains(category1));
        assertTrue(categories.contains(category2));
    }

	@Rubric(value = "The list of gifts is updated after an add", 
			goal = "The goal of this evaluation is to ensure that your Spring controller(s) "
			+ "can add gifts to the list that is stored in memory on the server."
			+ " The test also ensure that you properly return a list of gifts"
			+ " as JSON.", 
			points = 20.0, 
			reference = "This test is derived from the material in these gifts: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/61 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/97 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 ")
	@Test
	public void testAddGetGift() throws Exception {
		readWriteGiftSvcUser1.addGift(gift);
		Collection<Gift> stored = readWriteGiftSvcUser1.getGiftList();
		assertTrue(stored.contains(gift));
	}

    @Rubric(value = "The list of gifts is updated after an add",
            goal = "The goal of this evaluation is to ensure that your Spring controller(s) "
                    + "can add gifts to the list that is stored in memory on the server."
                    + " The test also ensure that you properly return a list of gifts"
                    + " as JSON.",
            points = 20.0,
            reference = "This test is derived from the material in these gifts: "
                    + "https://class.coursera.org/mobilecloud-001/lecture/61 "
                    + "https://class.coursera.org/mobilecloud-001/lecture/97 "
                    + "https://class.coursera.org/mobilecloud-001/lecture/99 ")
    @Test
    public void testFindGiftByCategory() throws Exception {
        readWriteGiftSvcUser1.addGift(gift);
        Collection<Gift> stored = readWriteGiftSvcUser1.findByCategory(gift.getCategory());
        for(Gift gift1 : stored) {
            assertTrue(gift1.getCategory().equals(gift.getCategory()));
        }
    }

	@Rubric(value = "Requests without authentication token are denied.", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "properly authenticates queries using the OAuth Password Grant flow."
			+ "Any query that does not contain the correct authorization token"
			+ "should be denied with a 401 error.", 
			points = 20.0, 
			reference = "This test is derived from the material in these gifts: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/117 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/127 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/123 ")
	@Test
	public void testDenyGiftAddWithoutOAuth() throws Exception {
		ErrorRecorder error = new ErrorRecorder();

		// Create an insecure version of our Rest Adapter that doesn't know how
		// to use OAuth.
		GiftSvcApi insecuregiftService = new RestAdapter.Builder()
				.setClient(
						new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
				.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL)
				.setErrorHandler(error).build().create(GiftSvcApi.class);
		try {
			// This should fail because we haven't logged in!
			insecuregiftService.addGift(gift);

			fail("Yikes, the security setup is horribly broken and didn't require the user to authenticate!!");

		} catch (Exception e) {
			// Ok, our security may have worked, ensure that
			// we got a 401
			assertEquals(HttpStatus.SC_UNAUTHORIZED, error.getError()
					.getResponse().getStatus());
		}

		// We should NOT get back the gift that we added above!
		Collection<Gift> gifts = readWriteGiftSvcUser1.getGiftList();
		assertFalse(gifts.contains(gift));
	}

	@Rubric(value = "A user can like/unlike a gift and increment/decrement the like count", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "allows users to like/unlike gifts using the /gift/{id}/like endpoint, and"
			+ "and the /gift/{id}/unlike endpoint."
			+ "Once a user likes/unlikes a gift, the count of users that like that gift"
			+ "should be incremented/decremented.", 
			points = 20.0, 
			reference = "This test is derived from the material in these gifts: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/121 ")
	@Test
	public void testLikeCount() throws Exception {

		// Add the gift
		Gift v = readWriteGiftSvcUser1.addGift(gift);

		// Like the gift
		readWriteGiftSvcUser1.likeGift(v.getId());

		// Get the gift again
		v = readWriteGiftSvcUser1.getGiftById(v.getId());

		// Make sure the like count is 1
		assertTrue(v.getPoints() == 1);

		// Unlike the gift
		readWriteGiftSvcUser1.unlikeGift(v.getId());

		// Get the gift again
		v = readWriteGiftSvcUser1.getGiftById(v.getId());

		// Make sure the like count is 0
		assertTrue(v.getPoints() == 0);
	}

	@Rubric(value = "A user can like/unlike a gift and be added to/removed from the \"liked by\" list.", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "allows users to like/unlike gifts using the /gift/{id}/like endpoint"
			+ "and the /gift/{id}/unlike endpoint."
			+ "Once a user likes/unlikes a gift, the username should be added to/removed from the "
			+ "list of users that like that gift.", 
			points = 20.0, 
			reference = "This test is derived from the material in these gifts: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/121 ")
	@Test
	public void testLikedBy() throws Exception {

		// Add the gift
		Gift v = readWriteGiftSvcUser1.addGift(gift);

		// Like the gift
		readWriteGiftSvcUser1.likeGift(v.getId());

		Collection<String> likedby = readWriteGiftSvcUser1.getUsersWhoLikedGift(v.getId());

		// Make sure we're on the list of people that like this gift
		assertTrue(likedby.contains(USERNAME1));
		
		// Have the second user like the gift
		readWriteGiftSvcUser2.likeGift(v.getId());
		
		// Make sure both users show up in the like list
		likedby = readWriteGiftSvcUser1.getUsersWhoLikedGift(v.getId());
		assertTrue(likedby.contains(USERNAME1));
		assertTrue(likedby.contains(USERNAME2));

		// Unlike the gift
		readWriteGiftSvcUser1.unlikeGift(v.getId());

		// Get the gift again
		likedby = readWriteGiftSvcUser1.getUsersWhoLikedGift(v.getId());

		// Make sure user1 is not on the list of people that liked this gift
		assertTrue(!likedby.contains(USERNAME1));
		
		// Make sure that user 2 is still there
		assertTrue(likedby.contains(USERNAME2));
	}

	@Rubric(value = "A user is only allowed to like a gift once.", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "restricts users to liking a gift only once. "
			+ "This test simply attempts to like a gift twice and then checks that "
			+ "the like count is only 1.", 
			points = 20.0, 
			reference = "This test is derived from the material in these gifts: "
					+ "https://class.coursera.org/mobilecloud-001/lecture/99 "
					+ "https://class.coursera.org/mobilecloud-001/lecture/121"
	)
	@Test
	public void testLikingTwice() throws Exception {

		// Add the gift
		Gift v = readWriteGiftSvcUser1.addGift(gift);

		// Like the gift
		readWriteGiftSvcUser1.likeGift(v.getId());

		// Get the gift again
		v = readWriteGiftSvcUser1.getGiftById(v.getId());

		// Make sure the like count is 1
		assertTrue(v.getPoints() == 1);

			// Like the gift again.
		readWriteGiftSvcUser1.likeGift(v.getId());
        v = readWriteGiftSvcUser1.getGiftById(v.getId());

        assertTrue(v.getPoints() == 0);

	}

	@Rubric(value = "A user cannot like a non-existant gift", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "won't crash if a user attempts to like a non-existant gift. "
			+ "This test simply attempts to like a non-existant gift then checks "
			+ "that a 404 Not Found response is returned.", 
			points = 20.0, 
			reference = "This test is derived from the material in these gifts: "
					+ "https://class.coursera.org/mobilecloud-001/lecture/99 "
					+ "https://class.coursera.org/mobilecloud-001/lecture/121"
	)
	@Test
	public void testLikingNonExistantGift() throws Exception {

		try {
			// Like the gift again.
			readWriteGiftSvcUser1.likeGift(getInvalidGiftId());

			fail("The server let us like a gift that doesn't exist without returning a 404.");
		} catch (RetrofitError e) {
			// Make sure we got a 400 Bad Request
			assertEquals(404, e.getResponse().getStatus());
		}
	}

	@Rubric(value = "A user can find a gift by providing its name", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "allows users to find gifts by searching for the gift's name.", 
			points = 20.0, 
			reference = "This test is derived from the material in these gifts: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/97 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 ")
	@Test
	public void testFindByName() {

		// Create the names unique for testing.
		String[] names = new String[3];
		names[0] = "The Cat";
		names[1] = "The Spoon";
		names[2] = "The Plate";

		// Create three random gifts, but use the unique names
		ArrayList<Gift> gifts = new ArrayList<Gift>();

		for (int i = 0; i < names.length; ++i) {
			gifts.add(TestData.randomGift());
			gifts.get(i).setTitle(names[i]);
		}

		// Add all the gifts to the server
		for (Gift v : gifts){
			readWriteGiftSvcUser1.addGift(v);
		}

		// Search for "The Cat"
		Collection<Gift> searchResults = readWriteGiftSvcUser1.findByTitle(names[0]);
		assertTrue(searchResults.size() > 0);

		// Make sure all the returned gifts have "The Cat" for their title
		for (Gift v : searchResults) {
			assertTrue(v.getTitle().equals(names[0]));
		}
	}

	/**
	 * Test finding a gift by its duration.
	 */
	@Rubric(value = "A user can find gifts that have a duration less than a certain value.", 
			goal = "The goal of this evaluation is to ensure that your Spring application "
			+ "allows users to find gifts by searching for gifts with a duration "
			+ "less that a specified value.", 
			points = 20.0, 
			reference = "This test is derived from the material in these gifts: "
			+ "https://class.coursera.org/mobilecloud-001/lecture/97 "
			+ "https://class.coursera.org/mobilecloud-001/lecture/99 ")
	@Test
	public void testFindByDurationLessThan() {


	}

    @Rubric(
            value="Mpeg gift data can be submitted for a gift",
            goal="The goal of this evaluation is to ensure that your Spring controller(s) "
                    + "allow mpeg gift data to be submitted for a gift. The test also"
                    + " checks that the controller(s) can serve that gift data to the"
                    + " client.",
            points=20.0,
            reference="This test is derived from the material in these gifts: "
                    + "https://class.coursera.org/mobilecloud-001/lecture/69 "
                    + "https://class.coursera.org/mobilecloud-001/lecture/65 "
                    + "https://class.coursera.org/mobilecloud-001/lecture/71 "
                    + "https://class.coursera.org/mobilecloud-001/lecture/207"
    )
    @Test
    public void testAddGiftData() throws Exception {
        Gift received = readWriteGiftSvcUser1.addGift(gift);
        GiftStatus status = readWriteGiftSvcUser1.setGiftImage(received.getId(),
                new TypedFile("image/jpg", testImage));
        assertEquals(GiftStatus.GiftState.READY, status.getState());

        Response response = readWriteGiftSvcUser1.getGiftImage(received.getId());
        assertEquals(200, response.getStatus());

        InputStream giftData = response.getBody().in();
        byte[] originalFile = IOUtils.toByteArray(new FileInputStream(testImage));
        byte[] retrievedFile = IOUtils.toByteArray(giftData);
        assertTrue(Arrays.equals(originalFile, retrievedFile));
    }

	private String getInvalidGiftId() {
		Set<String> ids = new HashSet<String>();
		Collection<Gift> stored = readWriteGiftSvcUser1.getGiftList();
		for (Gift v : stored) {
			ids.add(v.getId());
		}

        String nonExistantId = UUID.randomUUID().toString();
		while (ids.contains(nonExistantId)) {
            nonExistantId = UUID.randomUUID().toString();
		}
		return nonExistantId;
	}

}
