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
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Created by carlosjimenez on 10/20/15.
 */

@PersistenceCapable
public class Question {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key questionId;

    @Persistent
    private int questionNumber;

    @Persistent
    private String value;

    @Persistent
    private String type;

    @Persistent
    private String answerType;

    @Persistent
    private String answerSufix;

    @Persistent
    private boolean graphAvailable;

    @NotPersistent
    private Answer answer;

    public Question() {
    }

    public Question(String questionId, int questionNumber, String value, String type, String answerType, String answerSufix, boolean graphAvailable) {
        super();
        setQuestionId(questionId);
        this.questionNumber = questionNumber;
        this.value = value;
        this.type = type;
        this.answerType = answerType;
        this.answerSufix = answerSufix;
        this.graphAvailable = graphAvailable;
    }

    public String getQuestionId() {
        return this.questionId != null ? KeyFactory.keyToString(this.questionId) : null;
    }

    public void setQuestionId(String questionId) {
        this.questionId = null;

        if (questionId != null && !questionId.equals("")) {
            this.questionId = KeyFactory.stringToKey(questionId);
        }
    }

    public String getAnswerSufix() {
        return answerSufix;
    }

    public void setAnswerSufix(String answerSufix) {
        this.answerSufix = answerSufix;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
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

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public boolean isGraphAvailable() {
        return graphAvailable;
    }

    public void setGraphAvailable(boolean graphAvailable) {
        this.graphAvailable = graphAvailable;
    }

    @Override
    public String toString() {
        return "Question{" +
                "answer=" + answer +
                ", questionId=" + questionId +
                ", questionNumber=" + questionNumber +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", answerType='" + answerType + '\'' +
                ", answerSufix='" + answerSufix + '\'' +
                ", graphAvailable=" + graphAvailable +
                '}';
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
}
