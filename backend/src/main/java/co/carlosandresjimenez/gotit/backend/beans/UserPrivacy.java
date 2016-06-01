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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Created by carlosjimenez on 11/3/15.
 */
@PersistenceCapable
public class UserPrivacy {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key privacyId;

    @Persistent
    private String email;

    @Persistent
    private boolean shareMedical;

    @NotPersistent
    public static final boolean DEFAULT_SHARE_MEDICAL = false;

    @Persistent
    private boolean shareBirthDate;

    @NotPersistent
    public static final boolean DEFAULT_SHARE_BIRTH_DATE = false;

    @Persistent
    private boolean shareAvatar;

    @NotPersistent
    public static final boolean DEFAULT_SHARE_AVATAR = true;

    @Persistent
    private boolean shareCheckInLocation;

    @NotPersistent
    public static final boolean DEFAULT_SHARE_LOCATION = false;

    @Persistent
    private boolean shareTelephoneNumber;

    @NotPersistent
    public static final boolean DEFAULT_SHARE_TELEPHONE = false;

    @Persistent
    private boolean shareFeedback;

    @NotPersistent
    public static final boolean DEFAULT_SHARE_FEEDBACK = true;

    public UserPrivacy() {
    }

    public UserPrivacy(String email, boolean shareMedical, boolean shareBirthDate,
                       boolean shareAvatar, boolean shareCheckInLocation, boolean shareTelephoneNumber, boolean shareFeedback) {
        super();
        setPrivacyId(email);
        this.email = email;
        this.shareMedical = shareMedical;
        this.shareBirthDate = shareBirthDate;
        this.shareAvatar = shareAvatar;
        this.shareCheckInLocation = shareCheckInLocation;
        this.shareTelephoneNumber = shareTelephoneNumber;
        this.shareFeedback = shareFeedback;
    }

    public static Key getKey(String email) {
        Key keyPrivacyId = null;

        if (email != null && !email.equals(""))
            keyPrivacyId = KeyFactory.createKey(UserPrivacy.class.getSimpleName(), email);

        return keyPrivacyId;
    }

    public String getPrivacyId() {
        return this.privacyId != null ? KeyFactory.keyToString(this.privacyId) : null;
    }

    public void setPrivacyId(String email) {
        this.privacyId = getKey(email);
    }


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
