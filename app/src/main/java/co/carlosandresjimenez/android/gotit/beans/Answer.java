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

package co.carlosandresjimenez.android.gotit.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;

/**
 * Created by carlosjimenez on 10/22/15.
 */
public class Answer implements Parcelable {

    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        public Answer createFromParcel(Parcel orig) {
            return new Answer(orig);
        }

        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };
    private String answerId;
    private String checkinId;
    private String questionId;
    private String email;
    private String value;
    private String type;
    private String datetime;

    public Answer() {
    }

    public Answer(String email) {
        this.email = email;
    }

    public Answer(String answerId, String checkinId, String questionId, String email, String value, String type, String datetime) {
        this.answerId = answerId;
        this.checkinId = checkinId;
        this.questionId = questionId;
        this.email = email;
        this.value = value;
        this.type = type;
        this.datetime = datetime;
    }

    public Answer(Parcel orig) {
        this.answerId = orig.readString();
        this.checkinId = orig.readString();
        this.questionId = orig.readString();
        this.email = orig.readString();
        this.value = orig.readString();
        this.type = orig.readString();
        this.datetime = orig.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(answerId);
        dest.writeString(checkinId);
        dest.writeString(questionId);
        dest.writeString(email);
        dest.writeString(value);
        dest.writeString(type);
        dest.writeString(datetime);
    }

    @Override
    public String toString() {
        return "Answer{" +
                "answerId='" + answerId + '\'' +
                ", checkinId='" + checkinId + '\'' +
                ", questionId='" + questionId + '\'' +
                ", email='" + email + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", datetime='" + datetime + '\'' +
                '}';
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String id) {
        this.answerId = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(String checkinId) {
        this.checkinId = checkinId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
