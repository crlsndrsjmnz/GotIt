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

package co.carlosandresjimenez.android.gotit.connection;

/**
 * Created by carlosjimenez on 10/15/15.
 */
public enum ConnectionState {
    CREATED {
        @Override
        void connect(GoogleConnection googleConnection) {
            googleConnection.onSignUp();
        }

        @Override
        void disconnect(GoogleConnection googleConnection) {
            googleConnection.onSignOut();
        }
    },
    OPENING {
    },
    OPENED {
        @Override
        void renewToken(GoogleConnection googleConnection) {
            googleConnection.onRenewToken();
        }

        @Override
        void disconnect(GoogleConnection googleConnection) {
            googleConnection.onSignOut();
        }

        @Override
        void revokeAccessAndDisconnect(GoogleConnection googleConnection) {
            googleConnection.onRevokeAccessAndDisconnect();
        }
    },
    CLOSED {
        @Override
        void connect(GoogleConnection googleConnection) {
            googleConnection.onSignIn();
        }
    },
    TOKEN_OK {
        @Override
        void disconnect(GoogleConnection googleConnection) {
            googleConnection.onSignOut();
        }

        @Override
        void revokeAccessAndDisconnect(GoogleConnection googleConnection) {
            googleConnection.onRevokeAccessAndDisconnect();
        }
    },
    TOKEN_ERROR {
        @Override
        void renewToken(GoogleConnection googleConnection) {
            googleConnection.onRenewToken();
        }

        @Override
        void disconnect(GoogleConnection googleConnection) {
            googleConnection.onSignOut();
        }

        @Override
        void revokeAccessAndDisconnect(GoogleConnection googleConnection) {
            googleConnection.onRevokeAccessAndDisconnect();
        }
    },
    TOKEN_EXPIRED {
        @Override
        void renewToken(GoogleConnection googleConnection) {
            googleConnection.onRenewToken();
        }

        @Override
        void disconnect(GoogleConnection googleConnection) {
            googleConnection.onSignOut();
        }

        @Override
        void revokeAccessAndDisconnect(GoogleConnection googleConnection) {
            googleConnection.onRevokeAccessAndDisconnect();
        }
    };

    void connect(GoogleConnection googleConnection) {

    }

    void disconnect(GoogleConnection googleConnection) {

    }

    void revokeAccessAndDisconnect(GoogleConnection googleConnection) {

    }

    void renewToken(GoogleConnection googleConnection) {

    }
}
