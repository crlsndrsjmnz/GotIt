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
 * Created by carlosjimenez on 10/28/15.
 */
public class Following implements Parcelable {

    public static final int PENDING = 0;
    public static final int APPROVED = 1;
    public static final int REJECTED = 2;

    private String userEmail;
    private String followingUserEmail;
    private int approvedStatus;
    private boolean followBack;

    public static final Parcelable.Creator<Following> CREATOR = new Parcelable.Creator<Following>() {
        public Following createFromParcel(Parcel orig) {
            return new Following(orig);
        }

        public Following[] newArray(int size) {
            return new Following[size];
        }
    };

    public Following() {
    }

    public Following(String followingUserEmail) {
        this.followingUserEmail = followingUserEmail;
    }

    public Following(String userEmail, String followingUserEmail, int approvedStatus, boolean followBack) {
        this.userEmail = userEmail;
        this.followingUserEmail = followingUserEmail;
        this.approvedStatus = approvedStatus;
        this.followBack = followBack;
    }

    public Following(Parcel orig) {
        this.userEmail = orig.readString();
        this.followingUserEmail = orig.readString();
        this.approvedStatus = orig.readInt();
        this.followBack = orig.readByte() != 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Following) {
            Following other = (Following) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(userEmail, other.userEmail) &&
                    Objects.equal(followingUserEmail, other.followingUserEmail);
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
        dest.writeString(userEmail);
        dest.writeString(followingUserEmail);
        dest.writeInt(approvedStatus);
        dest.writeByte((byte) (followBack ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Following{" +
                "approvedStatus=" + approvedStatus +
                ", userEmail='" + userEmail + '\'' +
                ", followingUserEmail='" + followingUserEmail + '\'' +
                ", followBack='" + followBack + '\'' +
                '}';
    }

    public int getApprovedStatus() {
        return approvedStatus;
    }

    public void setApprovedStatus(int approvedStatus) {
        this.approvedStatus = approvedStatus;
    }

    public String getFollowingUserEmail() {
        return followingUserEmail;
    }

    public void setFollowingUserEmail(String followingUserEmail) {
        this.followingUserEmail = followingUserEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isFollowBack() {
        return followBack;
    }

    public void setFollowBack(boolean followBack) {
        this.followBack = followBack;
    }
}
