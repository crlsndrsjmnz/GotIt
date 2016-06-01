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
 * Created by carlosjimenez on 10/25/15.
 */
@PersistenceCapable
public class Checkin {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key checkinId;

    @Persistent
    private String email;

    @Persistent
    private String datetime;

    @Persistent
    private boolean isShared;

    @NotPersistent
    private User user;

    public Checkin() {
    }

    public Checkin(String checkinId, String email, String datetime, boolean isShared) {
        super();
        setCheckinId(checkinId);
        this.email = email;
        this.datetime = datetime;
        this.isShared = isShared;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "checkinId=" + checkinId +
                ", email='" + email + '\'' +
                ", datetime='" + datetime + '\'' +
                ", isShared='" + isShared + '\'' +
                '}';
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

    public String getCheckinId() {
        return this.checkinId != null ? KeyFactory.keyToString(this.checkinId) : null;
    }

    public void setCheckinId(String checkinId) {
        this.checkinId = null;

        if (checkinId != null && !checkinId.equals("")) {
            this.checkinId = KeyFactory.stringToKey(checkinId);
        }
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setIsShared(boolean isShared) {
        this.isShared = isShared;
    }
}
