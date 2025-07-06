package io.github.greatericontop.greatimpostor.task;

/*
 * Copyright (C) 2023-present greateric.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty  of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
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
    public abstract void startTask(Player player);

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
