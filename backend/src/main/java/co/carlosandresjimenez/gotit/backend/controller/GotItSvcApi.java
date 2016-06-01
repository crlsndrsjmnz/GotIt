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

package co.carlosandresjimenez.gotit.backend.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import co.carlosandresjimenez.gotit.backend.beans.Answer;
import co.carlosandresjimenez.gotit.backend.beans.Checkin;
import co.carlosandresjimenez.gotit.backend.beans.Question;
import co.carlosandresjimenez.gotit.backend.beans.User;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * This interface defines an API for a GotItSvc. The interface is used to
 * provide a contract for client/server interactions. The interface is annotated
 * with Retrofit annotations so that clients can automatically convert the
 * input/output into objects.
 *
 * @author Carlos Andres Jimenez
 */
public interface GotItSvcApi {

    int RESPONSE_STATUS_OK = 200; // 200 OK
    int RESPONSE_STATUS_NO_CONTENT = 204; // 204 No content
    int RESPONSE_STATUS_NOT_SAVED = 209; // 209 Not saved
    int RESPONSE_STATUS_NOT_FOUND = 210; // 210 Not found
    int RESPONSE_STATUS_PK_VIOLATED = 211; // 211 PK violated
    int RESPONSE_STATUS_CANNOT_FOLLOW = 212; // 212 Cannot follow
    int RESPONSE_STATUS_CANNOT_FOLLOW_ITSELF = 213; // 213 Cannot follow itself
    int RESPONSE_STATUS_UNAUTHORIZED = 401; // 401 Unauthorized
    int RESPONSE_STATUS_AUTH_REQUIRED = 407; // 407 Authentication Required

    String ID_PARAMETER = "id";

    String USER_PATH = "/user";
    String REGISTER_PATH = USER_PATH + "/register";

    String CHECKIN_BASE_PATH = "/checkin";
    String CHECKIN_HISTORY_PATH = CHECKIN_BASE_PATH + "/history";
    String CHECKIN_GRAPH_PATH = CHECKIN_BASE_PATH + "/graph";
    String QUESTION_PATH = CHECKIN_BASE_PATH + "/question";
    String ANSWER_PATH = CHECKIN_BASE_PATH + "/answer";

    String LOAD_BASE_PATH = "/load";
    String LOAD_QUESTION_PATH = LOAD_BASE_PATH + "/question";

    String FOLLOWING_BASE_PATH = "/following";
    String FOLLOWING_LIST_PATH = FOLLOWING_BASE_PATH + "/list";
    String FOLLOWING_TIMELINE_PATH = FOLLOWING_BASE_PATH + "/timeline";

    String APPROVE_PARAMETER = "approved";
    String FOLLOWBACK_PARAMETER = "followback";

    String FOLLOW_BASE_PATH = "/follow";
    String FOLLOW_REQUESTS_PATH = FOLLOW_BASE_PATH + "/requests";
    String FOLLOW_APPROVE_PATH = FOLLOW_BASE_PATH + "/approve";

    @GET(USER_PATH)
    User getUserDetails(@Query(ID_PARAMETER) String email, HttpServletRequest request);

    @POST(REGISTER_PATH)
    String registerUser(@Body User user);


    @GET(QUESTION_PATH)
    ArrayList<Question> getAllQuestions();

    @POST(LOAD_QUESTION_PATH)
    String saveQuestion(@Body Question question);

    @POST(ANSWER_PATH)
    int saveAnswers(@Body ArrayList<Answer> answers, HttpServletRequest request);

    @GET(CHECKIN_HISTORY_PATH)
    ArrayList<Checkin> getCheckinHistory(HttpServletRequest request);

    @GET(CHECKIN_BASE_PATH)
    ArrayList<Question> getCheckinAnswers(
            @Query(ID_PARAMETER) String checkinId);

    @GET(FOLLOWING_LIST_PATH)
    ArrayList<User> getFollowingList(HttpServletRequest request);

    @GET(FOLLOW_REQUESTS_PATH)
    ArrayList<User> getFollowRequestsList(HttpServletRequest request);

    @GET(FOLLOWING_TIMELINE_PATH)
    ArrayList<Checkin> getFollowingTimeline(HttpServletRequest request);

    @GET(FOLLOW_BASE_PATH)
    int follow(@Query(ID_PARAMETER) String followEmail, HttpServletRequest request);

    @GET(FOLLOW_APPROVE_PATH)
    int changeFollowerStatus(@Query(ID_PARAMETER) String followerEmail,
                             @Query(APPROVE_PARAMETER) boolean approve,
                             @Query(FOLLOWBACK_PARAMETER) boolean followBack,
                             HttpServletRequest request);

    @GET(CHECKIN_GRAPH_PATH)
    ArrayList<Answer> getGraphData(@Query(ID_PARAMETER) String email,
                                   HttpServletRequest request);
}
