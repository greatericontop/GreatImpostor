package io.github.greatericontop.greatimpostor.task.sabotage;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import io.github.greatericontop.greatimpostor.core.impostor.Sabotage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class BaseSabotageTask implements Listener {

    protected GreatImpostorMain plugin;
    public BaseSabotageTask(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }


    /*
     * Get the sabotage that this task repairs
     */
    public abstract Sabotage getSabotage();

    /*
     * Called when the sabotage is first created to prepare for it being fixed
     */
    public abstract void prepareSabotageTask();

    /*
     * Starts the sabotage fix. Requires passing the :SabotageSubtask:.
     */
    public abstract void startTask(Player player, SabotageSubtask sabotageSubtask);

    protected void taskSuccessful(Player player) {
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null) {
            player.sendMessage("§cNo profile, couldn't complete the task!");
            return;
        }
        plugin.sabotageManager.endSabotage(getSabotage());
        player.sendMessage("§aYou completed the task!");
    }

    // Everything below is the same as :BaseTask:

    protected void playSuccessSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }
    protected void playFailSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

}
