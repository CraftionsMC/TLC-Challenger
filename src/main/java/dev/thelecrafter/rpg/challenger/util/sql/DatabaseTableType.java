package dev.thelecrafter.rpg.challenger.util.sql;

public enum DatabaseTableType {
    ZOMBIE_BOSS("zombie_boss");

    private final String databaseTableName;

    DatabaseTableType(String databaseTableName) {
        this.databaseTableName = databaseTableName;
    }

    public String getDatabaseTableName() {
        return databaseTableName;
    }
}
