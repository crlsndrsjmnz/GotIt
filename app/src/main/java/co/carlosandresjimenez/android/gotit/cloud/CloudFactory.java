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

import android.app.Activity;

import java.util.ArrayList;

import co.carlosandresjimenez.android.gotit.beans.Answer;
import co.carlosandresjimenez.android.gotit.beans.Checkin;
import co.carlosandresjimenez.android.gotit.beans.Following;
import co.carlosandresjimenez.android.gotit.beans.Question;
import co.carlosandresjimenez.android.gotit.beans.User;

/**
 * Created by carlosjimenez on 10/18/15.
 */
public class CloudFactory {

    public static CloudManager getManager(Activity activity, Object object) {

        if (object instanceof User)
            return new UserManager(activity, (User) object);

        else if (object instanceof Question)
            return new QuestionManager(activity, (Question) object);

        else if (object instanceof Answer)
            return new AnswerManager(activity, (Answer) object);

        else if (object instanceof Checkin)
            return new CheckinManager(activity, (Checkin) object);

        else if (object instanceof Following)
            return new FollowingManager(activity, (Following) object);

        else if (object instanceof ArrayList<?>) {
            Object listObject = ((ArrayList<?>) object).get(0);

            if (listObject instanceof Question)
                return new QuestionManager(activity, ((ArrayList<Question>) object));

            else if (listObject instanceof Answer)
                return new AnswerManager(activity, ((ArrayList<Answer>) object));

        }

        return null;
    }
}
