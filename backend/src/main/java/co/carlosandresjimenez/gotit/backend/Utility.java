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

package co.carlosandresjimenez.gotit.backend;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by carlosjimenez on 10/15/15.
 */
public class Utility {

    // TODO: Add a Google Client Id to be able to validate tokens
    private final static String CLIENT_ID = "";

    public static String validateGoogleToken(String accessToken) {

        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory mJFactory = new GsonFactory();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, mJFactory)
                .setAudience(Arrays.asList(CLIENT_ID))
                .build();

        GoogleIdToken idToken;
        Payload payload;
        String email = "";

        try {
            idToken = verifier.verify(accessToken);

            if (idToken != null) {
                //System.out.println("Token validated successfully");
            } else {
                //System.out.println("Invalid ID token.");
                return "INVALID";
            }

            payload = idToken.getPayload();

            if (payload != null) {
                email = payload.getEmail();
            }

        } catch (GeneralSecurityException gse) {
            gse.printStackTrace();
            return "INVALID";
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return "INVALID";
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            return "INVALID";
        } catch (Exception e) {
            e.printStackTrace();
            return "INVALID";
        }

        return email;
    }

    public static String getDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
                Locale.US);
        return sdf.format(new Date());
    }
}
