package dev.thelecrafter.rpg.challenger.util.sql;

import dev.thelecrafter.rpg.challenger.ChallengerPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        try {
            for (DatabaseTableType type : DatabaseTableType.values()) {
                Statement statement = ChallengerPlugin.DATABASE_CONNECTION.createStatement();
                ResultSet result = statement.executeQuery("UPDATE " + type.getDatabaseTableName() + " SELECT COUNT(*) FROM " + type.getDatabaseTableName() + " WHERE uuid = '" + event.getPlayer().getUniqueId() + "'");
                if (result.getRow() == 0) {
                    DatabaseTable.insertDefault(type, event.getPlayer().getUniqueId());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
