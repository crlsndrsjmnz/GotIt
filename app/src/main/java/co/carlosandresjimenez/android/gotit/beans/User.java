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
 * Created by carlosjimenez on 10/15/15.
 */
public class User implements Parcelable {

    private String userId;
    private String email;
    private String avatarUrl;
    private String name;
    private String lastName;
    private String birthDate;
    private String telephoneNumber;
    private String medicalRecNum;
    private String userType;
    private String errorMsg;
    private int approvedStatus;
    private UserPrivacy userPrivacy;

    public User() {

    }

    public User(String email) {
        super();
        this.email = email;
    }

    public User(String email, UserPrivacy userPrivacy) {
        super();
        this.email = email;
        this.userPrivacy = userPrivacy;
    }

    public User(int approvedStatus) {
        super();
        this.approvedStatus = approvedStatus;
    }

    public User(String userId, String email, String avatarUrl, String name, String lastName,
                String birthDate, String telephoneNumber, String medicalRecNum, String userType,
                String errorMsg, UserPrivacy userPrivacy) {
        super();
        this.userId = userId;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.telephoneNumber = telephoneNumber;
        this.medicalRecNum = medicalRecNum;
        this.userType = userType;
        this.errorMsg = errorMsg;
        this.userPrivacy = userPrivacy;
    }

    public User(Parcel orig) {
        this.userId = orig.readString();
        this.email = orig.readString();
        this.avatarUrl = orig.readString();
        this.name = orig.readString();
        this.lastName = orig.readString();
        this.birthDate = orig.readString();
        this.telephoneNumber = orig.readString();
        this.medicalRecNum = orig.readString();
        this.userType = orig.readString();
        this.errorMsg = orig.readString();
        this.userPrivacy = orig.readParcelable(UserPrivacy.class.getClassLoader());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(email);
        dest.writeString(avatarUrl);
        dest.writeString(name);
        dest.writeString(lastName);
        dest.writeString(birthDate);
        dest.writeString(telephoneNumber);
        dest.writeString(medicalRecNum);
        dest.writeString(userType);
        dest.writeString(errorMsg);
        dest.writeParcelable(userPrivacy, flags);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel orig) {
            return new User(orig);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getApprovedStatus() {
        return approvedStatus;
    }

    public void setApprovedStatus(int approvedStatus) {
        this.approvedStatus = approvedStatus;
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
    public String toString() {
        return "User{" +
                "approvedStatus=" + approvedStatus +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
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

    public UserPrivacy getUserPrivacy() {
        return userPrivacy;
    }

    public void setUserPrivacy(UserPrivacy userPrivacy) {
        this.userPrivacy = userPrivacy;
    }
}

