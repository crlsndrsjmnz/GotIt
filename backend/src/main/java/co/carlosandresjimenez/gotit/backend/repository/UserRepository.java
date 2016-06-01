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

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Query;

import co.carlosandresjimenez.gotit.backend.beans.User;

@Service
public class UserRepository extends JDOCrudRepository<User, Key> {

    public UserRepository() {
        super(User.class);
    }

    @SuppressWarnings("unchecked")
    public User findByEmail(String email) {
        Query query = PMF.get().getPersistenceManager()
                .newQuery(User.class);
        query.setFilter("email == n");
        query.declareParameters("String n");

        List<User> users = (List<User>) query.execute(email);

        if (users == null || users.isEmpty()) {
            throw new JDOObjectNotFoundException("User not found");
        }

        return users.get(0);
    }

}
