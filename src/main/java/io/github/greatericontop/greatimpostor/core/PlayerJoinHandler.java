package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinHandler implements Listener {

    private final GreatImpostorMain plugin;
    public PlayerJoinHandler(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!plugin.playerProfiles.containsKey(player.getUniqueId()))  return; // only do this for in-game players

        for (PlayerProfile otherProfile : plugin.playerProfiles.values()) {
            // hide players that need hiding
            if (
                    (!otherProfile.isAlive())
                    || (otherProfile.isImpostor() && ((ImpostorProfile) otherProfile).isInVent)
            ) {
                player.hidePlayer(plugin, otherProfile.getPlayer());
            }
        }
    }

}
