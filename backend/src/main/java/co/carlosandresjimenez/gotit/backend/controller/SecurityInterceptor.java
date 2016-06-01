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

package co.carlosandresjimenez.gotit.backend.controller;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.carlosandresjimenez.gotit.backend.Utility;

public class SecurityInterceptor extends HandlerInterceptorAdapter {

    // TODO: Add a test environment safe key
    String TEST_ENVIRONMENT = "";

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        String testEnviroment = request.getHeader("Environment");
        if (testEnviroment != null && !testEnviroment.isEmpty() &&
                testEnviroment.equals(TEST_ENVIRONMENT)) {

            request.setAttribute("email", request.getHeader("Email"));

            System.out.println("SecurityInterceptor: Test environment access granted to user " + request.getHeader("Email"));

            return true;
        }

        String accessToken = request.getHeader("Authorization");
        String email;

        if (accessToken != null && accessToken.length() > 7) {
            // Removing 'Bearer ' prefix
            accessToken = accessToken.substring(7);
            email = Utility.validateGoogleToken(accessToken);

            if (email.equals("INVALID")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        request.setAttribute("email", email);
        return true;
    }
}