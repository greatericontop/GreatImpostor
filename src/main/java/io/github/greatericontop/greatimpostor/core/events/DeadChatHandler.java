package io.github.greatericontop.greatimpostor.core.events;

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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class DeadChatHandler implements Listener {

    private final GreatImpostorMain plugin;
    public DeadChatHandler(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeadChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null || profile.isAlive()) {
            return;
        }
        event.setFormat("§7[DEAD CHAT]§r " + event.getFormat());
        for (PlayerProfile recipientProfile : plugin.playerProfiles.values()) {
            if (recipientProfile.isAlive()) {
                try {
                    event.getRecipients().remove(recipientProfile.getPlayer());
                } catch (UnsupportedOperationException e) {
                    // This should usually not happen but the docs say it's possible.
                    player.sendMessage("§cDue to a problem with the server, you can't send chat messages while dead. Sorry!");
                    event.setCancelled(true);
                }
            }
        }
    }

}
