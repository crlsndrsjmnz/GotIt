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

@PersistenceCapable
public class User {

    @NotPersistent
    public static final int USER_FOUND = 200;

    @NotPersistent
    public static final int USER_NOT_FOUND = 201;

    @NotPersistent
    public static final String USER_TYPE_PATIENT = "P";

    @NotPersistent
    public static final String USER_TYPE_FOLLOWER = "F";

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key userId;

    @Persistent
    private String email;

    @Persistent
    private String avatarUrl;

    @Persistent
    private String name;

    @Persistent
    private String lastName;

    @Persistent
    private String birthDate;

    @Persistent
    private String telephoneNumber;

    @Persistent
    private String medicalRecNum;

    @Persistent
    private String userType;

    @NotPersistent
    private String errorMsg;

    @NotPersistent
    private UserPrivacy userPrivacy;

    public User() {
    }

    public User(User original) {
        this.email = original.email;
        this.avatarUrl = original.avatarUrl;
        this.name = original.name;
        this.lastName = original.lastName;
        this.birthDate = original.birthDate;
        this.telephoneNumber = original.telephoneNumber;
        this.medicalRecNum = original.medicalRecNum;
        this.userType = original.userType;
        this.errorMsg = original.errorMsg;
    }

    public User(String userId, String email, String avatarUrl, String name, String lastName,
                String birthDate, String telephoneNumber, String medicalRecNum, String userType, String errorMsg) {
        super();
        setUserId(userId);
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.telephoneNumber = telephoneNumber;
        this.medicalRecNum = medicalRecNum;
        this.userType = userType;
        this.errorMsg = errorMsg;
    }

    public User copy() {
        return new User(this);
    }

    public String getUserId() {
        return this.userId != null ? KeyFactory.keyToString(this.userId) : null;
    }

    public void setUserId(String userId) {
        this.userId = null;

        if (userId != null && !userId.equals("")) {
            this.userId = KeyFactory.stringToKey(userId);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "avatarUrl='" + avatarUrl + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                ", medicalRecNum='" + medicalRecNum + '\'' +
                ", userType='" + userType + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", userPrivacy=" + userPrivacy +
                '}';
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMedicalRecNum() {
        return medicalRecNum;
    }

    public void setMedicalRecNum(String medicalRecNum) {
        this.medicalRecNum = medicalRecNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public UserPrivacy getUserPrivacy() {
        return userPrivacy;
    }

    public void setUserPrivacy(UserPrivacy userPrivacy) {
        this.userPrivacy = userPrivacy;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User other = (User) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(userId, other.userId)
                    && Objects.equal(email, other.email);
        } else {
            return false;
        }
    }


}
