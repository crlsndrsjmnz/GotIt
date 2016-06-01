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

/**
 * Created by carlosjimenez on 10/18/15.
 */
public enum ApplicationState {

    USER_FOUND {
    },
    USER_NOT_FOUND {
    },
    USER_SAVED {
    },
    USER_NOT_SAVED {
    },

    PENDING_USERS_FOUND {
    },
    APPROVED_USERS_FOUND {
    },
    REJECTED_USERS_FOUND {
    },
    PENDING_USERS_NOT_FOUND {
    },
    APPROVED_USERS_NOT_FOUND {
    },
    REJECTED_USERS_NOT_FOUND {
    },

    QUESTIONS_FOUND {
    },
    QUESTIONS_NOT_FOUND {
    },
    QUESTIONS_SAVED {
    },
    QUESTIONS_NOT_SAVED {
    },

    QUESTION_FOUND {
    },
    QUESTION_NOT_FOUND {
    },

    ANSWERS_FOUND {
    },
    ANSWERS_NOT_FOUND {
    },
    ANSWERS_SAVED {
    },
    ANSWERS_NOT_SAVED {
    },

    ANSWER_FOUND {
    },
    ANSWER_NOT_FOUND {
    },

    CHECKINS_FOUND {
    },
    CHECKINS_NOT_FOUND {
    },
    CHECKINS_SAVED {
    },
    CHECKINS_NOT_SAVED {
    },

    FOLLOWING_FOUND {
    },
    FOLLOWING_NOT_FOUND {
    },
    FOLLOWING_SAVED {
    },
    FOLLOWING_NOT_SAVED {
    },
    FOLLOWING_CANNOT_FOLLOW {
    },

    NO_INTERNET {
    },

    BAD_REQUEST {
    },

    ERROR {
    },

    ACCESS_UNAUTHORIZED {
    }
}
