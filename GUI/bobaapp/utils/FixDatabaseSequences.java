package bobaapp.utils;

public class FixDatabaseSequences {
    public static void main(String[] args) {
        System.out.println("Fixing database sequences...");
        DatabaseUtils.performDatabaseMaintenance();
        System.out.println("Done!");
    }
}
