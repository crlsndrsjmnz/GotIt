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

import java.util.concurrent.Executor;

import retrofit.Endpoint;
import retrofit.ErrorHandler;
import retrofit.Profiler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.Log;
import retrofit.RestAdapter.LogLevel;
import retrofit.converter.Converter;

public class SecuredRestBuilder extends RestAdapter.Builder {

    private String accessToken;

    @Override
    public SecuredRestBuilder setEndpoint(String endpoint) {
        return (SecuredRestBuilder) super.setEndpoint(endpoint);
    }

    @Override
    public SecuredRestBuilder setEndpoint(Endpoint endpoint) {
        return (SecuredRestBuilder) super.setEndpoint(endpoint);
    }

    @Override
    public SecuredRestBuilder setErrorHandler(ErrorHandler errorHandler) {

        return (SecuredRestBuilder) super.setErrorHandler(errorHandler);
    }

    @Override
    public SecuredRestBuilder setExecutors(Executor httpExecutor,
                                           Executor callbackExecutor) {

        return (SecuredRestBuilder) super.setExecutors(httpExecutor,
                callbackExecutor);
    }

    @Override
    public SecuredRestBuilder setRequestInterceptor(
            RequestInterceptor requestInterceptor) {

        return (SecuredRestBuilder) super
                .setRequestInterceptor(requestInterceptor);
    }

    @Override
    public SecuredRestBuilder setConverter(Converter converter) {

        return (SecuredRestBuilder) super.setConverter(converter);
    }

    @Override
    public SecuredRestBuilder setProfiler(@SuppressWarnings("rawtypes") Profiler profiler) {

        return (SecuredRestBuilder) super.setProfiler(profiler);
    }

    @Override
    public SecuredRestBuilder setLog(Log log) {

        return (SecuredRestBuilder) super.setLog(log);
    }

    @Override
    public SecuredRestBuilder setLogLevel(LogLevel logLevel) {

        return (SecuredRestBuilder) super.setLogLevel(logLevel);
    }

    public SecuredRestBuilder setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    @Override
    public RestAdapter build() {

        if (accessToken == null) {
            throw new SecuredRestException(
                    "You must have an access token for a "
                            + "SecuredRestBuilder before calling the build() method.");
        }

        OAuthHandler hdlr = new OAuthHandler(accessToken);
        setRequestInterceptor(hdlr);

        return super.build();
    }

    private class OAuthHandler implements RequestInterceptor {

        private String accessToken;

        public OAuthHandler(String accessToken) {
            super();
            this.accessToken = accessToken;
        }

        /**
         * Every time a method on the client interface is invoked, this method is
         * going to get called. The method checks adds a Username and the session key
         * to the request headers.
         */
        @Override
        public void intercept(RequestFacade request) {

            // Add the access_token to this request as the "Authorization"
            // header.
            request.addHeader("Authorization", "Bearer " + accessToken);
        }

    }
}