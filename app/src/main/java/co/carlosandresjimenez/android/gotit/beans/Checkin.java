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
 * Created by carlosjimenez on 10/25/15.
 */
public class Checkin implements Parcelable {

    public static final Parcelable.Creator<Checkin> CREATOR = new Parcelable.Creator<Checkin>() {
        public Checkin createFromParcel(Parcel orig) {
            return new Checkin(orig);
        }

        public Checkin[] newArray(int size) {
            return new Checkin[size];
        }
    };

    private String checkinId;
    private String email;
    private String datetime;
    private boolean personal;
    private User user;

    public Checkin() {
    }

    public Checkin(boolean personal) {
        this.personal = personal;
    }

    public Checkin(String checkinId, String email, String datetime, boolean personal, User user) {
        this.checkinId = checkinId;
        this.email = email;
        this.datetime = datetime;
        this.personal = personal;
        this.user = user;
    }

    public Checkin(Parcel orig) {
        this.checkinId = orig.readString();
        this.email = orig.readString();
        this.datetime = orig.readString();
        this.personal = orig.readByte() != 0;
        this.user = orig.readParcelable(User.class.getClassLoader());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Checkin) {
            Checkin other = (Checkin) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(checkinId, other.checkinId);
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
        dest.writeString(checkinId);
        dest.writeString(email);
        dest.writeString(datetime);
        dest.writeByte((byte) (personal ? 1 : 0));
        dest.writeParcelable(user, flags);
    }

    @Override
    public String toString() {
        return "Checkin{" +
                "checkinId='" + checkinId + '\'' +
                ", email='" + email + '\'' +
                ", datetime='" + datetime + '\'' +
                ", personal=" + personal +
                ", user=" + user +
                '}';
    }

    public String getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(String checkinId) {
        this.checkinId = checkinId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPersonal() {
        return personal;
    }

    public void setPersonal(boolean personal) {
        this.personal = personal;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
