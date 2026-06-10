package orm;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println(" Runtime Annotations test");

            UserEntity user = new UserEntity();

            OrmManager manager = new OrmManager();

            manager.save(user);

            System.out.println(" Success ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}