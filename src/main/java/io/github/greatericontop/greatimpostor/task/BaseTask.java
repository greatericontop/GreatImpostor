package io.github.greatericontop.greatimpostor.task;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class BaseTask implements Listener {

    protected GreatImpostorMain plugin;
    public BaseTask(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    /*
     * Subtask
     */
    public abstract TaskType getTaskType();

    /*
     * Activate the task for the player.
     */
    public abstract void startTask(Player player); // TODO

    protected void taskSuccessful(Player player) {
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null) {
            player.sendMessage("§cNo profile, couldn't complete the task!");
            return;
        }
        profile.processSubtaskCompleted(getTaskType());
        player.sendMessage("§aYou completed the task!");
    }

    protected void playSuccessSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }
    protected void playFailSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    /*
     * More listeners down here (that actually perform the task)
     */

}
