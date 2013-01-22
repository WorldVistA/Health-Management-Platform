package EXT.DOMAIN.cpe.vpr.pom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 *
 *
 * @see IPOMObjectDAO
 */
public interface IGenericPOMObjectDAO {
    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance.
     *
     * @param entity
     */
    <T extends IPOMObject> T save(T entity);

    /**
     * Deletes a given entity.
     *
     * @param entity
     * @throws IllegalArgumentException in case the given entity is (@literal null}.
     */
    <T extends IPOMObject> void delete(T entity);

    /**
     * Deletes the entity with the given uid.
     *
     * @param type
     * @param uid must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@code uid} is {@literal null}
     */
    <T extends IPOMObject> void deleteByUID(Class<T> type, String uid);

    /**
     * Deletes all entities of the specified type.
     *
     * @param type
     */
    <T extends IPOMObject> void deleteAll(Class<T> type);

    /**
     * Returns the number of entities available.
     *
     * @param type
     * @return the number of entities
     */
    <T extends IPOMObject> int count(Class<T> type);

    /**
     * Retrives an entity by its uid.
     *
     * @param type
     * @param uid must not be {@literal null}.
     * @return the entity with the given id or {@literal null} if none found
     * @throws IllegalArgumentException if {@code id} is {@literal null}
     */
    <T extends IPOMObject> T findByUID(Class<T> type, String uid);

    /**
     * Returns all instances of the type.
     *
     * @param type
     * @return all entities
     */
    <T extends IPOMObject> List<T> findAll(Class<T> type);

    /**
     * Returns all entities sorted by the given options.
     *
     * @param type
     * @param sort
     * @return all entities sorted by the given options
     */
    <T extends IPOMObject> List<T> findAll(Class<T> type, Sort sort);

    /**
     * Returns a {@link org.springframework.data.domain.Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param type
     * @param pageable
     * @return a page of entities
     */
    <T extends IPOMObject> Page<T> findAll(Class<T> type, Pageable pageable);
}
