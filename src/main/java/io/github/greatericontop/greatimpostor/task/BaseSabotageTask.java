package io.github.greatericontop.greatimpostor.task;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import io.github.greatericontop.greatimpostor.impostor.Sabotage;
import org.bukkit.entity.Player;

public abstract class BaseSabotageTask extends BaseTask {

    public BaseSabotageTask(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        throw new RuntimeException("getTaskType should not be called on a BaseSabotageTask");
    }

    /*
     * Get the sabotage that this task repairs
     */
    public abstract Sabotage getSabotage();

    protected void taskSuccessful(Player player) {
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null) {
            player.sendMessage("§cNo profile, couldn't complete the task!");
            return;
        }
        plugin.sabotageManager.endSabotage(getSabotage());
        player.sendMessage("§aYou completed the task!");
    }

}
