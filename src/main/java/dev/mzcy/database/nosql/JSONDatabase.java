package dev.mzcy.database.nosql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.mzcy.database.Database;
import dev.mzcy.database.nosql.annotation.PrimaryKey;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the Database interface for JSON storage.
 *
 * @param <E> the type of the entity
 */
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class JSONDatabase<E> implements Database<E> {

    File file;
    Gson gson;
    Type type;
    Map<String, E> data;
    Class<E> entityClass;

    /**
     * Constructs a JSONDatabase instance.
     *
     * @param filePath the path to the JSON file
     * @param entityClass the class of the entity
     */
    public JSONDatabase(String filePath, Class<E> entityClass) {
        this.entityClass = entityClass;
        this.file = new File(filePath);
        this.gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        this.type = TypeToken.getParameterized(HashMap.class, String.class, entityClass).getType();
        this.data = loadData();
    }

    /**
     * Loads data from the JSON file.
     *
     * @return the data map
     */
    private Map<String, E> loadData() {
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Saves data to the JSON file.
     */
    private void saveData() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the provided entity to the JSON file.
     *
     * @param entity the entity to save
     */
    @Override
    public void save(E entity) {
        String key = getKey(entity);
        data.put(key, entity);
        saveData();
    }

    /**
     * Retrieves an entity from the JSON file using the provided primary key.
     *
     * @param key the primary key of the entity
     * @return the retrieved entity, or null if not found
     */
    @Override
    public E getEntity(String key) {
        return data.get(key);
    }

    /**
     * Deletes an entity from the JSON file using the provided primary key.
     *
     * @param key the primary key of the entity to delete
     */
    public void deleteEntity(String key) {
        data.remove(key);
        saveData();
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
                return field.getName();
            }
        }
        throw new IllegalArgumentException("No field annotated with @PrimaryKey found in entity class");
    }
}