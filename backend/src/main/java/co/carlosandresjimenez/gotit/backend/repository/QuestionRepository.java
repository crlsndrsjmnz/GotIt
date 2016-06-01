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

package co.carlosandresjimenez.gotit.backend.repository;

import com.google.appengine.api.datastore.Key;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.jdo.Query;

import co.carlosandresjimenez.gotit.backend.beans.Question;

/**
 * Created by carlosjimenez on 10/20/15.
 */
@Service
public class QuestionRepository extends JDOCrudRepository<Question, Key> {

    public QuestionRepository() {
        super(Question.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Question> findAll() {
        Query query = PMF.get().getPersistenceManager()
                .newQuery(Question.class);
        query.setOrdering("questionNumber asc");

        return (List<Question>) query.execute();
    }

    @SuppressWarnings("unchecked")
    public Question findFirstGraphAvailableQuestion() {
        Query query = PMF.get().getPersistenceManager()
                .newQuery(Question.class);
        query.setFilter("graphAvailable == true");

        List<Question> questions = (List<Question>) query.execute();

        return questions != null ? questions.get(0) : null;
    }
}