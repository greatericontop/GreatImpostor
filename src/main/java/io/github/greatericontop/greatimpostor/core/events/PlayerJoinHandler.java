package io.github.greatericontop.greatimpostor.core.events;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerJoinHandler implements Listener {

    private final GreatImpostorMain plugin;
    public PlayerJoinHandler(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!plugin.playerProfiles.containsKey(player.getUniqueId())) {
            // do cleanup for players not in game
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getTeam("greatimpostor_players");
            if (team != null) {
                team.removePlayer(player);
            }
            return;
        }

        for (PlayerProfile otherProfile : plugin.playerProfiles.values()) {
            if (otherProfile.getPlayer().equals(player))  continue; // don't hide self because bad things happen
            // hide players that need hiding
            if (
                    (!otherProfile.isAlive())
            ) {
                player.hidePlayer(plugin, otherProfile.getPlayer());
            }
        }
    }

}
