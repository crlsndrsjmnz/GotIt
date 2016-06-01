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

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOException;
import javax.jdo.JDOObjectNotFoundException;
import javax.servlet.http.HttpServletRequest;

import co.carlosandresjimenez.gotit.backend.Utility;
import co.carlosandresjimenez.gotit.backend.beans.Answer;
import co.carlosandresjimenez.gotit.backend.beans.Checkin;
import co.carlosandresjimenez.gotit.backend.beans.Following;
import co.carlosandresjimenez.gotit.backend.beans.Question;
import co.carlosandresjimenez.gotit.backend.beans.User;
import co.carlosandresjimenez.gotit.backend.beans.UserPrivacy;
import co.carlosandresjimenez.gotit.backend.repository.AnswerRepository;
import co.carlosandresjimenez.gotit.backend.repository.CheckinRepository;
import co.carlosandresjimenez.gotit.backend.repository.FollowingRepository;
import co.carlosandresjimenez.gotit.backend.repository.QuestionRepository;
import co.carlosandresjimenez.gotit.backend.repository.UserPrivacyRepository;
import co.carlosandresjimenez.gotit.backend.repository.UserRepository;

// Tell Spring that this class is a Controller that should
// handle certain HTTP requests for the DispatcherServlet
@Controller
public class GotItSvc implements GotItSvcApi {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private CheckinRepository checkinRepository;

    @Autowired
    private FollowingRepository followingRepository;

    @Autowired
    private UserPrivacyRepository userPrivacyRepository;

    @RequestMapping(value = USER_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    User getUserDetails(@RequestParam(ID_PARAMETER) String email,
                        HttpServletRequest request) {

        String sessionEmail = (String) request.getAttribute("email");

        if (email == null || email.isEmpty()) {
            return null; //RESPONSE_STATUS_AUTH_REQUIRED;
        }

        if (sessionEmail == null || sessionEmail.isEmpty()) {
            return null; //RESPONSE_STATUS_AUTH_REQUIRED;
        }

        boolean isPublic = true;
        if (sessionEmail.equals(email)) {
            isPublic = false;
        }

        return findUser(email, isPublic);
    }

    public User findUser(String email, boolean isPublic) {
        User user = null;
        try {
            user = userRepository.findByEmail(email);
        } catch (JDOObjectNotFoundException e) {
            System.out.println(USER_PATH + " getUserDetails: User " + email + " not found.");
        }

        if (user == null)
            return null;

        UserPrivacy userPrivacy = null;
        try {
            userPrivacy = userPrivacyRepository.findOne(UserPrivacy.getKey(email));
        } catch (JDOObjectNotFoundException e) {
            System.out.println(USER_PATH + " getUserDetails: User Privacy not found for " + email);
        }

        if (isPublic) {

            user = user.copy();

            if (userPrivacy != null) {
                if (!userPrivacy.isShareAvatar())
                    user.setAvatarUrl(null);

                if (!userPrivacy.isShareBirthDate())
                    user.setBirthDate(null);

                if (!userPrivacy.isShareMedical())
                    user.setMedicalRecNum(null);

                if (!userPrivacy.isShareTelephoneNumber())
                    user.setTelephoneNumber(null);
            } else {
                user.setBirthDate(null);
                user.setMedicalRecNum(null);
                user.setTelephoneNumber(null);
            }
        } else {
            if (userPrivacy == null)
                user.setUserPrivacy(new UserPrivacy(email,
                        UserPrivacy.DEFAULT_SHARE_MEDICAL,
                        UserPrivacy.DEFAULT_SHARE_BIRTH_DATE,
                        UserPrivacy.DEFAULT_SHARE_AVATAR,
                        UserPrivacy.DEFAULT_SHARE_LOCATION,
                        UserPrivacy.DEFAULT_SHARE_TELEPHONE,
                        UserPrivacy.DEFAULT_SHARE_FEEDBACK));
            else
                user.setUserPrivacy(userPrivacy);
        }

        return user;
    }

    @RequestMapping(value = REGISTER_PATH, method = RequestMethod.POST)
    public
    @ResponseBody
    String registerUser(@RequestBody User user) {

        if (user == null)
            return null;

        User result = null;

        try {
            result = userRepository.save(user);
        } catch (JDOException e) {
            e.printStackTrace();
        }

        UserPrivacy userPrivacy = user.getUserPrivacy();
        try {
            if (userPrivacy != null)
                userPrivacy.setPrivacyId(userPrivacy.getEmail());
            else
                userPrivacy = new UserPrivacy(user.getEmail(),
                        UserPrivacy.DEFAULT_SHARE_MEDICAL,
                        UserPrivacy.DEFAULT_SHARE_BIRTH_DATE,
                        UserPrivacy.DEFAULT_SHARE_AVATAR,
                        UserPrivacy.DEFAULT_SHARE_LOCATION,
                        UserPrivacy.DEFAULT_SHARE_TELEPHONE,
                        UserPrivacy.DEFAULT_SHARE_FEEDBACK);

            userPrivacyRepository.save(userPrivacy);
        } catch (JDOException e) {
            e.printStackTrace();
        }

        return result != null ? result.getUserId() : null;
    }

    @RequestMapping(value = QUESTION_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    ArrayList<Question> getAllQuestions() {

        ArrayList<Question> questions = Lists.newArrayList(questionRepository.findAll());

        questions.add(new Question(
                null,
                questions.size() + 1,
                "Share this check-in?",
                "singe choice",
                "checkbox",
                null,
                false));

        return Lists.newArrayList(questions);
    }

    @RequestMapping(value = LOAD_QUESTION_PATH, method = RequestMethod.POST)
    public
    @ResponseBody
    String saveQuestion(@RequestBody Question question) {

        Question result = null;

        try {
            result = questionRepository.save(question);
        } catch (JDOException e) {
            e.printStackTrace();
        }

        return result != null ? result.getQuestionId() : null;
    }

    @RequestMapping(value = ANSWER_PATH, method = RequestMethod.POST)
    public
    @ResponseBody
    int saveAnswers(@RequestBody ArrayList<Answer> answers, HttpServletRequest request) {

        if (answers == null || answers.isEmpty()) {
            return RESPONSE_STATUS_NOT_SAVED;
        }

        Checkin checkin;
        String email = (String) request.getAttribute("email");

        if (email == null || email.isEmpty()) {
            return RESPONSE_STATUS_AUTH_REQUIRED;
        }

        int lastAnswerPosition = answers.size() - 1;
        Answer lastAnswer = answers.get(lastAnswerPosition);

        try {
            checkin = checkinRepository.save(new Checkin(null,
                    email,
                    Utility.getDatetime(),
                    Boolean.parseBoolean(lastAnswer.getValue())));
        } catch (JDOException e) {
            e.printStackTrace();
            return RESPONSE_STATUS_NOT_SAVED;
        }

        answers.remove(lastAnswerPosition);

        for (Answer answer : answers) {
            answer.setCheckinId(checkin.getCheckinId());
        }

        ArrayList<Answer> result;

        try {
            result = (ArrayList<Answer>) answerRepository.save(answers);
        } catch (JDOException e) {
            e.printStackTrace();
            return RESPONSE_STATUS_NOT_SAVED;
        }

        if (result == null || result.isEmpty()) {
            return RESPONSE_STATUS_NOT_SAVED;
        }

        if (result.size() != answers.size()) {
            return RESPONSE_STATUS_NOT_SAVED;
        }

        return RESPONSE_STATUS_OK;
    }

    @RequestMapping(value = CHECKIN_HISTORY_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    ArrayList<Checkin> getCheckinHistory(HttpServletRequest request) {
        String email = (String) request.getAttribute("email");

        if (email == null || email.isEmpty()) {
            return null; //RESPONSE_STATUS_AUTH_REQUIRED;
        }

        return Lists.newArrayList(checkinRepository.findByEmail(email));
    }

    @RequestMapping(value = CHECKIN_BASE_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    ArrayList<Question> getCheckinAnswers(
            @RequestParam(ID_PARAMETER) String checkinId) {

        if (checkinId == null || checkinId.isEmpty())
            return null;

        Answer answer;
        ArrayList<Question> questions = Lists.newArrayList(questionRepository.findAll());

        for (Question question : questions) {

            if (question != null && !question.getQuestionId().isEmpty())
                answer = answerRepository.findByCheckinAndQuestionId(checkinId, question.getQuestionId());
            else
                answer = null;

            question.setAnswer(answer);
        }

        return questions;
    }

    @RequestMapping(value = FOLLOW_BASE_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    int follow(@RequestParam(ID_PARAMETER) String followEmail,
               HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("email");

        if (userEmail == null || userEmail.isEmpty()) {
            return RESPONSE_STATUS_AUTH_REQUIRED;
        }

        return followUser(userEmail, followEmail, Following.PENDING);
    }

    @RequestMapping(value = FOLLOW_APPROVE_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    int changeFollowerStatus(@RequestParam(ID_PARAMETER) String followerEmail,
                             @RequestParam(APPROVE_PARAMETER) boolean approve,
                             @RequestParam(FOLLOWBACK_PARAMETER) boolean followBack,
                             HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("email");

        if (userEmail == null || userEmail.isEmpty()) {
            return RESPONSE_STATUS_AUTH_REQUIRED;
        }

        int resultCode;

        try {
            if (approve) {
                // Save approved
                resultCode = followUser(followerEmail, userEmail, Following.APPROVED);

                if (resultCode == RESPONSE_STATUS_OK && followBack) {
                    // Save follow back
                    resultCode = followUser(userEmail, followerEmail, Following.PENDING);
                }

            } else {
                // Save rejected
                resultCode = followUser(followerEmail, userEmail, Following.REJECTED);
            }

        } catch (JDOException e) {
            e.printStackTrace();
            return RESPONSE_STATUS_NOT_SAVED;
        }

        return resultCode;
    }

    int followUser(String userEmail, String followUserEmail, int approvedStatus) {

        if (userEmail.equals(followUserEmail))
            return RESPONSE_STATUS_CANNOT_FOLLOW_ITSELF;

        try {
            if (approvedStatus == Following.PENDING &&
                    followingRepository.exists(Following.getKey(userEmail, followUserEmail))) {
                System.out.println("ERROR " + RESPONSE_STATUS_PK_VIOLATED + ". Primary Key violated, relation " + userEmail + " to " + followUserEmail + " exists.");
                return RESPONSE_STATUS_PK_VIOLATED;
            }
        } catch (JDOObjectNotFoundException e) {
            // This is thrown if the relationship is not found, in that case we can continue.
        }

        User user;
        try {
            user = userRepository.findByEmail(followUserEmail);
        } catch (JDOObjectNotFoundException e) {
            return RESPONSE_STATUS_NOT_FOUND;
        }

        if (user == null)
            return RESPONSE_STATUS_NOT_FOUND;

        if (user.getUserType().equals(User.USER_TYPE_FOLLOWER)) {
            return RESPONSE_STATUS_CANNOT_FOLLOW;
        }

        Following following;
        try {
            following = followingRepository.save(new Following(userEmail, followUserEmail, approvedStatus));
        } catch (JDOException e) {
            e.printStackTrace();
            return RESPONSE_STATUS_NOT_SAVED;
        }

        if (following == null)
            return RESPONSE_STATUS_NOT_SAVED;

        return RESPONSE_STATUS_OK;
    }

    @RequestMapping(value = FOLLOWING_LIST_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    ArrayList<User> getFollowingList(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("email");

        if (userEmail == null || userEmail.isEmpty()) {
            return null; // RESPONSE_STATUS_AUTH_REQUIRED;
        }

        List<Following> followingList = followingRepository.findFollowingUsers(userEmail, Following.APPROVED);
        ArrayList<User> users = Lists.newArrayList();

        for (Following following : followingList)
            users.add(findUser(following.getFollowingUserEmail(), true));

        if (users.isEmpty())
            return null;

        return users;
    }

    @RequestMapping(value = FOLLOW_REQUESTS_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    ArrayList<User> getFollowRequestsList(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("email");

        if (userEmail == null || userEmail.isEmpty()) {
            return null; // RESPONSE_STATUS_AUTH_REQUIRED;
        }

        List<Following> followingList = followingRepository.findFollowRequest(userEmail, Following.PENDING);
        ArrayList<User> users = Lists.newArrayList();

        for (Following following : followingList)
            users.add(findUser(following.getUserEmail(), true));

        if (users.isEmpty())
            return null;

        return users;
    }

    @RequestMapping(value = FOLLOWING_TIMELINE_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    ArrayList<Checkin> getFollowingTimeline(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("email");

        if (userEmail == null || userEmail.isEmpty()) {
            return null; // RESPONSE_STATUS_AUTH_REQUIRED;
        }

        ArrayList<String> followingUsers = Lists.newArrayList(followingRepository.findFollowingUsersEmails(userEmail));

        if (followingUsers.isEmpty())
            return null;

        return Lists.newArrayList(checkinRepository.findByListOfEmails(followingUsers));
    }

    @RequestMapping(value = CHECKIN_GRAPH_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    ArrayList<Answer> getGraphData(@RequestParam(ID_PARAMETER) String email,
                                   HttpServletRequest request) {
        String sessionEmail = (String) request.getAttribute("email");

        if (email == null || email.isEmpty()) {
            return null; //RESPONSE_STATUS_AUTH_REQUIRED;
        }

        if (sessionEmail == null || sessionEmail.isEmpty()) {
            return null; //RESPONSE_STATUS_AUTH_REQUIRED;
        }

        boolean isPublic = true;
        if (sessionEmail.equals(email)) {
            isPublic = false;
        }

        if (isPublic) {
            UserPrivacy userPrivacy = null;
            try {
                userPrivacy = userPrivacyRepository.findOne(UserPrivacy.getKey(email));
            } catch (JDOObjectNotFoundException e) {
                System.out.println(USER_PATH + " getUserDetails: User Privacy not found for " + email);
            }

            if (userPrivacy == null || !userPrivacy.isShareFeedback())
                return null;
        }

        Question question;
        try {
            question = questionRepository.findFirstGraphAvailableQuestion();
        } catch (JDOException e) {
            e.printStackTrace();
            return null;
        }

        if (question == null || question.getQuestionId() == null || question.getQuestionId().isEmpty())
            return null;

        return Lists.newArrayList(answerRepository.findByEmailAndQuestionId(email, question.getQuestionId()));
    }
}