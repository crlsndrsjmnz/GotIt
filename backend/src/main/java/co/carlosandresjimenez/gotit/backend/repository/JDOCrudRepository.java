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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * This class provides a minimal interface to mimic a subset
 * of the functionality in the Spring Data Repository. This
 * example is provided solely to show how to accomplish
 * similar types of operations using JDO. It is possible to
 * run Spring Data on top of AppEngine's JPA implementation,
 * which will provide an identical environment to previous
 * examples.
 *
 * @param <T>  - The type of Object stored by the repository
 * @param <ID> - The type of ID used by the stored object
 * @author Carlos Andres Jimenez
 */
public class JDOCrudRepository<T, ID extends Serializable> {

    private Class<T> type_;

    public JDOCrudRepository(Class<T> type) {
        type_ = type;
    }

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity
     * @return the saved entity
     */
    public <S extends T> S save(S entity) {
        return PMF.get().getPersistenceManager().makePersistent(entity);
    }

    /**
     * Saves all given entities.
     *
     * @param entities
     * @return the saved entities
     */
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        ArrayList<S> saved = new ArrayList<S>();

        try {
            for (S entity : entities)
                saved.add(pm.makePersistent(entity));
        } catch (Exception e) {
            throw e;
        } finally {
            pm.close();
        }

        return saved;
    }

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found
     */
    public T findOne(ID id) {
        return PMF.get().getPersistenceManager().getObjectById(type_, id);
    }

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return true if an entity with the given id exists, {@literal false} otherwise
     */
    public boolean exists(ID id) {
        return findOne(id) != null;
    }

    /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    @SuppressWarnings("unchecked")
    public Iterable<T> findAll() {
        Query query = PMF.get().getPersistenceManager().newQuery(type_);
        Object rslt = query.execute();
        return (Collection<T>) rslt;
    }

    /**
     * Deletes the entity with the given id.
     *
     * @param id must not be {@literal null}.
     */
    public void delete(ID id) {
        T obj = findOne(id);
        if (obj != null) {
            PMF.get().getPersistenceManager().deletePersistent(obj);
        }
    }

    /**
     * Deletes a given entity.
     *
     * @param entity
     */
    public void delete(T entity) {
        PMF.get().getPersistenceManager().deletePersistent(entity);
    }

    /**
     * Deletes all entities.
     */
    public void deleteAll() {
        Query query = PMF.get().getPersistenceManager().newQuery(type_);
        query.deletePersistentAll();

    }

}
