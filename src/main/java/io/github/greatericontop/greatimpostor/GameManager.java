package io.github.greatericontop.greatimpostor;

import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager {

    private final GreatImpostorMain plugin;
    public GameManager(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    public void registerGameRunnable() {

        new BukkitRunnable() {
            public void run() {

                plugin.sabotageManager.tickSabotages();

                for (PlayerProfile profile : plugin.playerProfiles.values()) {
                    if (plugin.meetingManager.isMeetingActive()) {
                        plugin.meetingManager.setMeetingActionBar(profile.getPlayer());
                    } else {
                        profile.setActionBar();
                    }
                }

            }
        }.runTaskTimer(plugin, 1L, 1L);

    }

}
