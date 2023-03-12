package io.github.greatericontop.greatimpostor.task;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface BaseTask extends Listener {

    /*
     * A player is next to the task and wants to do it.
     * Check to see if the specific task can be executed.
     */
    boolean canExecute(Player player); // TODO

    /*
     * Activate the task for the player.
     */
    void startTask(Player player); // TODO

    default void taskSuccessful(Player player) { // TODO
        player.sendMessage("§aYou completed the task!");
    }

    default void playSuccessSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }
    default void playFailSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    /*
     * More listeners down here (that actually perform the task)
     */

}
