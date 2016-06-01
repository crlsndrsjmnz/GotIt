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
 * Created by carlosjimenez on 10/21/15.
 */
public class Question implements Parcelable {

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        public Question createFromParcel(Parcel orig) {
            return new Question(orig);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    private String questionId;
    private String value;
    private String type;
    private String answerType;
    private String answerSuffix;
    private Answer answer;

    public Question() {
    }

    public Question(String checkinId) {
        Answer answer = new Answer();
        answer.setCheckinId(checkinId);
        this.answer = answer;
    }

    public Question(String questionId, String value, String type, String answerType, String answerSuffix, Answer answer) {
        this.questionId = questionId;
        this.value = value;
        this.type = type;
        this.answerType = answerType;
        this.answerSuffix = answerSuffix;
        this.answer = answer;
    }

    public Question(Parcel orig) {
        this.questionId = orig.readString();
        this.value = orig.readString();
        this.type = orig.readString();
        this.answerType = orig.readString();
        this.answerSuffix = orig.readString();
        this.answer = orig.readParcelable(Answer.class.getClassLoader());
    }

    @Override
    public String toString() {
        return "Question{" +
                "answerSuffix='" + answerSuffix + '\'' +
                ", questionId='" + questionId + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", answerType='" + answerType + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public String getAnswerSuffix() {
        return answerSuffix;
    }

    public void setAnswerSuffix(String answerSuffix) {
        this.answerSuffix = answerSuffix;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Question) {
            Question other = (Question) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(questionId, other.questionId);
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
        dest.writeString(questionId);
        dest.writeString(value);
        dest.writeString(type);
        dest.writeString(answerType);
        dest.writeString(answerSuffix);
        dest.writeParcelable(answer, flags);
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String id) {
        this.questionId = id;
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

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
