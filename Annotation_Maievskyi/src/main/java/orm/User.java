package orm;

@GenerateOrmEntity(tableName = "users")
public class User {
    private int id;
    private String username;
    private int email;
}
