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
 * Created by carlosjimenez on 11/3/15.
 */
public class UserPrivacy implements Parcelable {

    public static final boolean DEFAULT_SHARE_MEDICAL = false;
    public static final boolean DEFAULT_SHARE_BIRTH_DATE = false;
    public static final boolean DEFAULT_SHARE_AVATAR = true;
    public static final boolean DEFAULT_SHARE_LOCATION = false;
    public static final boolean DEFAULT_SHARE_TELEPHONE = false;
    public static final boolean DEFAULT_SHARE_FEEDBACK = true;

    private String email;
    private boolean shareMedical;
    private boolean shareBirthDate;
    private boolean shareAvatar;
    private boolean shareCheckInLocation;
    private boolean shareTelephoneNumber;
    private boolean shareFeedback;

    public UserPrivacy() {

    }

    public UserPrivacy(String email, boolean shareAvatar, boolean shareBirthDate,
                       boolean shareCheckInLocation, boolean shareFeedback,
                       boolean shareMedical, boolean shareTelephoneNumber) {
        this.email = email;
        this.shareAvatar = shareAvatar;
        this.shareBirthDate = shareBirthDate;
        this.shareCheckInLocation = shareCheckInLocation;
        this.shareFeedback = shareFeedback;
        this.shareMedical = shareMedical;
        this.shareTelephoneNumber = shareTelephoneNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserPrivacy) {
            UserPrivacy other = (UserPrivacy) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(email, other.email);
        } else {
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public UserPrivacy(Parcel orig) {
        this.email = orig.readString();
        this.shareAvatar = orig.readByte() != 0;
        this.shareBirthDate = orig.readByte() != 0;
        this.shareCheckInLocation = orig.readByte() != 0;
        this.shareFeedback = orig.readByte() != 0;
        this.shareMedical = orig.readByte() != 0;
        this.shareTelephoneNumber = orig.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeByte((byte) (shareAvatar ? 1 : 0));
        dest.writeByte((byte) (shareBirthDate ? 1 : 0));
        dest.writeByte((byte) (shareCheckInLocation ? 1 : 0));
        dest.writeByte((byte) (shareFeedback ? 1 : 0));
        dest.writeByte((byte) (shareMedical ? 1 : 0));
        dest.writeByte((byte) (shareTelephoneNumber ? 1 : 0));
    }

    public static final Parcelable.Creator<UserPrivacy> CREATOR = new Parcelable.Creator<UserPrivacy>() {
        public UserPrivacy createFromParcel(Parcel orig) {
            return new UserPrivacy(orig);
        }

        public UserPrivacy[] newArray(int size) {
            return new UserPrivacy[size];
        }
    };

    @Override
    public String toString() {
        return "UserPrivacy{" +
                "email='" + email + '\'' +
                ", shareMedical=" + shareMedical +
                ", shareBirthDate=" + shareBirthDate +
                ", shareAvatar=" + shareAvatar +
                ", shareCheckInLocation=" + shareCheckInLocation +
                ", shareTelephoneNumber=" + shareTelephoneNumber +
                ", shareFeedback=" + shareFeedback +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isShareAvatar() {
        return shareAvatar;
    }

    public void setShareAvatar(boolean shareAvatar) {
        this.shareAvatar = shareAvatar;
    }

    public boolean isShareBirthDate() {
        return shareBirthDate;
    }

    public void setShareBirthDate(boolean shareBirthDate) {
        this.shareBirthDate = shareBirthDate;
    }

    public boolean isShareCheckInLocation() {
        return shareCheckInLocation;
    }

    public void setShareCheckInLocation(boolean shareCheckInLocation) {
        this.shareCheckInLocation = shareCheckInLocation;
    }

    public boolean isShareFeedback() {
        return shareFeedback;
    }

    public void setShareFeedback(boolean shareFeedback) {
        this.shareFeedback = shareFeedback;
    }

    public boolean isShareMedical() {
        return shareMedical;
    }

    public void setShareMedical(boolean shareMedical) {
        this.shareMedical = shareMedical;
    }

    public boolean isShareTelephoneNumber() {
        return shareTelephoneNumber;
    }

    public void setShareTelephoneNumber(boolean shareTelephoneNumber) {
        this.shareTelephoneNumber = shareTelephoneNumber;
    }
}
