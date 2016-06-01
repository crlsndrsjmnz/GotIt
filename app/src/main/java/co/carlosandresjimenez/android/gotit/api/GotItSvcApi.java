/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Carlos Andres Jimenez <apps@carlosandresjimenez.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package co.carlosandresjimenez.android.gotit.api;

import java.util.ArrayList;

import co.carlosandresjimenez.android.gotit.beans.Answer;
import co.carlosandresjimenez.android.gotit.beans.Checkin;
import co.carlosandresjimenez.android.gotit.beans.Question;
import co.carlosandresjimenez.android.gotit.beans.User;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * This interface defines an API for a GotItSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * results into objects.
 *
 * @author Carlos Andres Jimenez
 */
public interface GotItSvcApi {

    String ID_PARAMETER = "id";

    String USER_PATH = "/user";
	String REGISTER_PATH = USER_PATH + "/register";

	String CHECKIN_BASE_PATH = "/checkin";
	String CHECKIN_HISTORY_PATH = CHECKIN_BASE_PATH + "/history";
    String CHECKIN_GRAPH_PATH = CHECKIN_BASE_PATH + "/graph";
    String QUESTION_PATH = CHECKIN_BASE_PATH + "/question";
    String ANSWER_PATH = CHECKIN_BASE_PATH + "/answer";

    String FOLLOWING_BASE_PATH = "/following";
    String FOLLOWING_LIST_PATH = FOLLOWING_BASE_PATH + "/list";
    String FOLLOWING_TIMELINE_PATH = FOLLOWING_BASE_PATH + "/timeline";

    String APPROVE_PARAMETER = "approved";
    String FOLLOWBACK_PARAMETER = "followback";

    String FOLLOW_BASE_PATH = "/follow";
    String FOLLOW_REQUESTS_PATH = FOLLOW_BASE_PATH + "/requests";
    String FOLLOW_APPROVE_PATH = FOLLOW_BASE_PATH + "/approve";

    @GET(USER_PATH)
    User getUserDetails(
            @Query(ID_PARAMETER) String userId);

    @POST(REGISTER_PATH)
    String registerUser(@Body User u);

	@GET(QUESTION_PATH)
	ArrayList<Question> getAllQuestions();

	@POST(ANSWER_PATH)
	int saveAnswers(@Body ArrayList<Answer> answers);

	@GET(CHECKIN_HISTORY_PATH)
	ArrayList<Checkin> getCheckinHistory();

	@GET(CHECKIN_BASE_PATH)
	ArrayList<Question> getCheckinAnswers(@Query(ID_PARAMETER) String checkinId);

    @GET(FOLLOWING_LIST_PATH)
    ArrayList<User> getFollowingList();

    @GET(FOLLOW_REQUESTS_PATH)
    ArrayList<User> getFollowRequestsList();

    @GET(FOLLOWING_TIMELINE_PATH)
    ArrayList<Checkin> getFollowingTimeline();

    @GET(FOLLOW_BASE_PATH)
    int follow(@Query(ID_PARAMETER) String followEmail);

    @GET(FOLLOW_APPROVE_PATH)
    int changeFollowerStatus(@Query(ID_PARAMETER) String followerEmail,
                             @Query(APPROVE_PARAMETER) boolean approve,
                             @Query(FOLLOWBACK_PARAMETER) boolean followBack);

    @GET(CHECKIN_GRAPH_PATH)
    ArrayList<Answer> getGraphData(@Query(ID_PARAMETER) String email);

}





