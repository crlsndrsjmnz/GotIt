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

package co.carlosandresjimenez.gotit.backend.beans;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Objects;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Created by carlosjimenez on 10/24/15.
 */
@PersistenceCapable
public class Answer {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key answerId;

    @Persistent
    private String checkinId;

    @Persistent
    private String questionId;

    @Persistent
    private String email;

    @Persistent
    private String value;

    @Persistent
    private String type;

    @Persistent
    private String datetime;

    public Answer() {
    }

    public Answer(String answerId, String checkinId, String questionId, String email, String value, String type, String datetime) {
        super();
        setAnswerId(answerId);
        this.checkinId = checkinId;
        this.questionId = questionId;
        this.email = email;
        this.value = value;
        this.type = type;
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "answerId=" + answerId +
                ", checkinId='" + checkinId + '\'' +
                ", questionId='" + questionId + '\'' +
                ", email='" + email + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", datetime='" + datetime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Answer) {
            Answer other = (Answer) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(answerId, other.answerId);
        } else {
            return false;
        }
    }

    public String getAnswerId() {
        return this.answerId != null ? KeyFactory.keyToString(this.answerId) : null;
    }

    public void setAnswerId(String answerId) {
        this.answerId = null;

        if (answerId != null && !answerId.equals("")) {
            this.answerId = KeyFactory.stringToKey(answerId);
        }
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(String checkinId) {
        this.checkinId = checkinId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
