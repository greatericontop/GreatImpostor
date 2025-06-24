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
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;

public class BodyReportingListener implements Listener {
    private static final double REPORT_DIST = 4.0;

    private final GreatImpostorMain plugin;
    public BodyReportingListener(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onRightClickReportButton(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)  return;
        if (event.getHand() != EquipmentSlot.HAND)  return;
        if (event.getItem() == null)  return;
        ItemMeta im = event.getItem().getItemMeta();
        if (im == null)  return;
        if (im.getPersistentDataContainer().has(ImpostorUtil.REPORT_KEY, PersistentDataType.INTEGER)) {
            Player player = event.getPlayer();
            PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
            if (profile == null) {
                player.sendMessage("§cCouldn't get your profile!");
                return;
            }
            if (!profile.isAlive()) {
                player.sendMessage("§cYou're dead! You can't report a body!");
                return;
            }
            // Dead body bypasses some sabotages and even cancels some
            if (plugin.sabotageManager.shouldRemoveWhenBodyReported()) {
                plugin.sabotageManager.forceEndSabotage();
            }
            boolean noBodyFound = true; // no for-else :(
            for (Entity entity : player.getNearbyEntities(REPORT_DIST, REPORT_DIST, REPORT_DIST)) {
                if (!(entity instanceof ArmorStand armorStand))  continue;
                if (armorStand.getPersistentDataContainer().has(ImpostorUtil.DEAD_BODY_KEY, PersistentDataType.INTEGER)) {
                    armorStand.remove();
                    player.sendMessage("§3Successfully reported!");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.showTitle(Title.title(
                                Component.text("§cDead Body Reported"),
                                Component.text("§6" + player.getName() + " reported a body!"),
                                Title.Times.times(Duration.ofMillis(1500L), Duration.ofMillis(5000L), Duration.ofMillis(1500L))
                        ));
                    }
                    plugin.meetingManager.startNewMeeting(false);
                    noBodyFound = false;
                    break;
                }
            }
            if (noBodyFound) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                player.sendMessage("§3No dead body close to you was found!");
            }
        }
    }

}
