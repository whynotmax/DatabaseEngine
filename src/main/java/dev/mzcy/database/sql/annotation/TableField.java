package dev.mzcy.database.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Table field annotation. Use this annotation to mark a field as a table field.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {

    /**
     * The name of the field.
     * @return  the name of the field in the table.
     */
    String name();

    /**
     * The type of the field.
     * @return  true if the field is a primary key, false otherwise.
     */
    boolean primaryKey() default false;

}
