package orm;

import java.lang.reflect.Field;

public class OrmManager {

    public void save(Object entity) throws Exception {
        Class<?> entityClass = entity.getClass();

        if (!entityClass.isAnnotationPresent(DbTable.class)) {
            throw new IllegalArgumentException("Not an ORM entity!");
        }


        DbTable tableAnnotation = entityClass.getAnnotation(DbTable.class);
        String tableName = tableAnnotation.name();

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(DbColumn.class)) {
                field.setAccessible(true);

                DbColumn columnAnnotation = field.getAnnotation(DbColumn.class);
                columns.append(columnAnnotation.name()).append(", ");

                Object value = field.get(entity);
                values.append("'").append(value).append("', ");
            }
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s);",
                tableName,
                columns.substring(0, columns.length() - 2),
                values.substring(0, values.length() - 2)
        );

        System.out.println("Executing SQL: " + sql);
    }
}