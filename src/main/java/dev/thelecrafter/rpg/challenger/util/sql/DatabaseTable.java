package dev.thelecrafter.rpg.challenger.util.sql;

import dev.thelecrafter.rpg.challenger.ChallengerPlugin;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DatabaseTable {

    public static boolean insertDefault(DatabaseTableType databaseTableType, UUID uuid) {
        try {
            Statement statement = ChallengerPlugin.DATABASE_CONNECTION.createStatement();
            statement.execute("INSERT INTO " + databaseTableType.getDatabaseTableName() + " VALUES ('" + uuid + "', 0, 0, 0, 0, 0)");
            statement.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean add(DatabaseTableType databaseTableType, UUID uuid, String row, int addition) {
        try {
            Statement statement = ChallengerPlugin.DATABASE_CONNECTION.createStatement();
            statement.execute("UPDATE " + databaseTableType.getDatabaseTableName() + " SET " + row + " = " + row + " + " + addition + " WHERE uuid = '" + uuid + "'");
            statement.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean reset(DatabaseTableType databaseTableType, UUID uuid) {
        try {
            Statement statement = ChallengerPlugin.DATABASE_CONNECTION.createStatement();
            statement.execute("DELETE FROM " + databaseTableType.getDatabaseTableName() + " WHERE uuid = '" + uuid + "'");
            statement.close();
            return insertDefault(databaseTableType, uuid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

}

