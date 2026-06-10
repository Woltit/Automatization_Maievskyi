package orm;

@orm.DbTable(name = "users")
public class UserEntity {

    @orm.DbColumn(name = "id")
    private int id;

    @orm.DbColumn(name = "username")
    private java.lang.String username;

    @orm.DbColumn(name = "email")
    private int email;

    public UserEntity() {}

}
