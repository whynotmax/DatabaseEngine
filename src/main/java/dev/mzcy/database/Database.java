package dev.mzcy.database;

/**
 * Interface for database
 * @param <E>   Entity
 */
public interface Database<E> {

    /**
     * Save entity
     * @param entity    Entity
     */
    void save(E entity);

    /**
     * Get entity
     * @param key   Key
     * @return      Entity
     */
    E getEntity(String key);

}
