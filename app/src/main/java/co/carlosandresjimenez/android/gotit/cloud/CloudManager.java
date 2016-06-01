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

package co.carlosandresjimenez.android.gotit.cloud;

import java.util.Observer;

/**
 * Created by carlosjimenez on 10/15/15.
 */
public interface CloudManager {

    int BADREQUEST_MAX_RETRIES = 2;

    int REQUEST_TYPE_SAVE = 1;
    int REQUEST_TYPE_FIND_ONE = 2;
    int REQUEST_TYPE_FIND_ALL = 3;
    int REQUEST_TYPE_DELETE = 4;

    String AUTH_TOKEN_KEY = "AUTH_TOKEN";
    String MESSENGER_KEY = "MESSENGER";
    String REQUEST_CODE_KEY = "REQUEST_CODE";
    String RESPONSE_CODE_KEY = "RESPONSE_CODE";

    int RESPONSE_STATUS_OK = 200; // 200 OK
    int RESPONSE_STATUS_NO_CONTENT = 204; // 204 No content
    int RESPONSE_STATUS_NOT_SAVED = 209; // 209 Not saved
    int RESPONSE_STATUS_NOT_FOUND = 210; // 210 Not found
    int RESPONSE_STATUS_PK_VIOLATED = 211; // 211 PK violated
    int RESPONSE_STATUS_CANNOT_FOLLOW = 212; // 212 Cannot follow
    int RESPONSE_STATUS_CANNOT_FOLLOW_ITSELF = 213; // 213 Cannot follow itself
    int RESPONSE_STATUS_BADREQUEST = 400; // 400 Bad Request
    int RESPONSE_STATUS_UNAUTHORIZED = 401; // 401 Unauthorized
    int RESPONSE_STATUS_ERROR = 520; // 520 Unknown Error

    String USER_EMAIL_KEY = "USER_EMAIL";
    String USER_ENTITY_KEY = "USER_ENTITY";
    String USER_ENTITIES_KEY = "USER_ENTITIES";
    String USER_APPROVED_STATUS_KEY = "USER_APPROVED_STATUS";

    String CHECKIN_ID_KEY = "CHECKIN_ID";
    String CHECKIN_FEED_KEY = "CHECKIN_FEED";
    String CHECKIN_ENTITY_KEY = "CHECKIN_ENTITY";
    String CHECKIN_ENTITIES_KEY = "CHECKIN_ENTITIES";

    String QUESTION_ENTITY_KEY = "QUESTION_ENTITY";
    String QUESTION_ENTITIES_KEY = "QUESTION_ENTITIES";

    String ANSWER_EMAIL_KEY = "ANSWER_EMAIL";
    String ANSWER_ENTITY_KEY = "ANSWER_ENTITY";
    String ANSWER_ENTITIES_KEY = "ANSWER_ENTITIES";

    String FOLLOWING_ENTITY_KEY = "FOLLOWING_ENTITY";
    String FOLLOWING_ENTITIES_KEY = "FOLLOWING_ENTITIES";
    String FOLLOWING_EMAIL_KEY = "FOLLOWING_EMAIL";

    void addObserver(Observer observer);

    void deleteObserver(Observer observer);

    void save();

    void findOne();

    void findAll();

    void delete();
}
