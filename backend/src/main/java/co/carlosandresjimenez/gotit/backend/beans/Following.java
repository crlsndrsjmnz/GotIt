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
 * Created by carlosjimenez on 10/27/15.
 */
@PersistenceCapable
public class Following {

    @NotPersistent
    public static final int PENDING = 0;

    @NotPersistent
    public static final int APPROVED = 1;

    @NotPersistent
    public static final int REJECTED = 2;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key followingId;

    @Persistent
    private String userEmail;

    @Persistent
    private String followingUserEmail;

    @Persistent
    private int approvedStatus;

    public Following() {
    }

    public Following(String userEmail, String followingUserEmail, int approvedStatus) {
        super();
        setFollowingId(userEmail, followingUserEmail);
        this.userEmail = userEmail;
        this.followingUserEmail = followingUserEmail;
        this.approvedStatus = approvedStatus;
    }

    @Override
    public String toString() {
        return "Following{" +
                "userEmail=" + userEmail +
                ", followingUserEmail='" + followingUserEmail + '\'' +
                ", followingId='" + followingId + '\'' +
                ", approvedStatus='" + approvedStatus + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Following) {
            Following other = (Following) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(followingId, other.followingId);
        } else {
            return false;
        }
    }

    public static Key getKey(String userEmail, String followingUserEmail) {
        Key keyFollowingId = null;
        String strFollowingId = userEmail + followingUserEmail;

        if (strFollowingId != null && !strFollowingId.equals(""))
            keyFollowingId = KeyFactory.createKey(Following.class.getSimpleName(), strFollowingId);

        return keyFollowingId;
    }

    public String getFollowingId() {
        return this.followingId != null ? KeyFactory.keyToString(this.followingId) : null;
    }

    public void setFollowingId(String userEmail, String followingUserEmail) {
        this.followingId = getKey(userEmail, followingUserEmail);
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

    public int getApprovedStatus() {
        return approvedStatus;
    }

    public void setApprovedStatus(int approvedStatus) {
        this.approvedStatus = approvedStatus;
    }
}