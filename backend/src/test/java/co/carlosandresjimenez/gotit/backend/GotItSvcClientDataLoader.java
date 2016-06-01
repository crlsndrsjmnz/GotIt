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

import com.google.api.client.util.Lists;

import org.junit.Test;

import java.util.Collection;

import co.carlosandresjimenez.gotit.backend.beans.Question;
import co.carlosandresjimenez.gotit.backend.controller.GotItSvcApi;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;

import static org.junit.Assert.assertTrue;

/**
 * This integration test sends a POST request to the fill the DB.
 * Actual network communication using HTTP is performed with this test.
 * <p/>
 * The test requires that the MutivoSvc be running first (see the directions in
 * the README.md file for how to launch the Application).
 * <p/>
 * To run this test, right-click on it in Eclipse and select
 * "Run As"->"JUnit Test"
 *
 * @author Carlos Andres Jimenez
 */
public class GotItSvcClientDataLoader {

    // private final String SERVER = "http://10.0.0.32:8080";
    private final String SERVER = "https://getit-1093.appspot.com";

    private GotItSvcApi gotItSvcApi = new RestAdapter.Builder()
            .setEndpoint(SERVER).setLogLevel(LogLevel.FULL).build()
            .create(GotItSvcApi.class);

    private Question question;

    @Test
    public void testQSetAddAndList() throws Exception {

        Collection<Question> questionsToInsert = fillCollection();
        String result;

        for (Question question : questionsToInsert) {

            System.out.println("Question: " + question.toString());

            // Add the question
            result = gotItSvcApi.saveQuestion(question);

            boolean ok = false;
            if (result != null && !result.isEmpty()) {
                System.out.println("Question saved, id: " + result);
                ok = true;
            }

            assertTrue(ok);
        }
    }

    private Collection<Question> fillCollection() {

        Collection<Question> questions = Lists.newArrayList();
        questions.add(new Question(null, 1, "What was your blood sugar level at [meal time/bedtime]?", "single choice", "numeric", "mmol/L", true));
        questions.add(new Question(null, 2, "What time did you check your blood sugar level at [meal time]?", "single choice", "time", "", false));
        questions.add(new Question(null, 3, "What did you eat at [meal time]?", "single choice", "string", "", false));

        return questions;

    }

}
