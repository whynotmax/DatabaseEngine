package dev.mzcy.database.nosql;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import dev.mzcy.database.Database;
import dev.mzcy.database.nosql.annotation.PrimaryKey;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bson.Document;

import java.lang.reflect.Field;

/**
 * Implementation of the Database interface for MongoDB.
 *
 * @param <E> the type of the entity
 */
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class MongoDBDatabase<E> implements Database<E> {

    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> collection;
    Class<E> entityClass;

    /**
     * Constructs a MongoDBDatabase instance.
     *
     * @param connectionString the MongoDB connection string
     * @param databaseName the name of the database
     * @param collectionName the name of the collection
     * @param entityClass the class of the entity
     */
    public MongoDBDatabase(String connectionString, String databaseName, String collectionName, Class<E> entityClass) {
        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(collectionName);
        this.entityClass = entityClass;
    }

    /**
     * Saves the provided entity to the MongoDB collection.
     *
     * @param entity the entity to save
     */
    @Override
    public void save(E entity) {
        Document document = new Document();
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                document.append(field.getName(), field.get(entity));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        collection.insertOne(document);
    }

    /**
     * Retrieves an entity from the MongoDB collection using the provided primary key.
     *
     * @param key the primary key of the entity
     * @return the retrieved entity, or null if not found
     */
    @Override
    public E getEntity(String key) {
        Document document = collection.find(Filters.eq("_id", key)).first();
        if (document == null) {
            return null;
        }
        try {
            E entity = entityClass.getDeclaredConstructor().newInstance();
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(entity, document.get(field.getName()));
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deletes an entity from the MongoDB collection using the provided primary key.
     *
     * @param key the primary key of the entity to delete
     */
    public void deleteEntity(String key) {
        collection.deleteOne(Filters.eq("_id", key));
    }

    /**
     * Closes the MongoDB client connection.
     */
    public void close() {
        mongoClient.close();
    }

    /**
     * Gets the primary key of the entity.
     *
     * @param entity the entity
     * @return the primary key
     */
    private String getKey(E entity) {
        Class<?> clazz = entity.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                field.setAccessible(true);
                try {
                    return field.get(entity).toString();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new IllegalArgumentException("No field annotated with @PrimaryKey found in entity class");
    }
}