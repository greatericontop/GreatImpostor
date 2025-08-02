package io.github.greatericontop.greatimpostor.core.impostor;

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
import io.github.greatericontop.greatimpostor.core.profiles.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import io.github.greatericontop.greatimpostor.utils.CooldownResetReason;
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nullable;
import java.util.UUID;

public class ImpostorKillListener implements Listener {

    private final GreatImpostorMain plugin;
    public ImpostorKillListener(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player))  return;
        Player victimPlayer;
        boolean isFakePlayerKill = false;
        if (event.getEntity() instanceof Player) {
            victimPlayer = (Player) event.getEntity();
        } else {
            // Could be a fake player armor stand, otherwise ignore
            if (!(event.getEntity() instanceof ArmorStand entity
                    && entity.getPersistentDataContainer().has(ImpostorUtil.FAKE_PLAYER_KEY, PersistentDataType.STRING))) {
                return;
            }
            victimPlayer = Bukkit.getPlayer(UUID.fromString(entity.getPersistentDataContainer().get(ImpostorUtil.FAKE_PLAYER_KEY, PersistentDataType.STRING)));
            if (victimPlayer == null) {
                return;
            }
            isFakePlayerKill = true;
        }
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null)  return;
        event.setCancelled(true); // (cancel the damage once we have the profile)
        if (!profile.isImpostor()) {
            player.sendMessage("§cYou can't attack others!");
            return;
        }
        ImpostorProfile impostorProfile = (ImpostorProfile) profile;
        if (!impostorProfile.isAlive()) {
            player.sendMessage("§cYou can't kill while dead!");
            return;
        }
        if (event.getDamage() < 2.0) { // chose this so fist crits (1.5) don't count but a not-very-attack-speed'ed sword hit counts
            player.sendMessage("§cYou must use your sword!");
            return;
        }
        if (!impostorProfile.getCanKill()) {
            player.sendMessage("§cYou can't kill right now!");
            return;
        }
        PlayerProfile victimProfile = plugin.playerProfiles.get(victimPlayer.getUniqueId());
        if (victimProfile == null) {
            player.sendMessage("§cThis player isn't in this game!");
            return;
        }
        if (victimProfile.isImpostor()) {
            player.sendMessage("§cDon't kill your fellow impostors!");
            return;
        }
        impostorProfile.resetKillCooldown(CooldownResetReason.AFTER_USE);
        if (isFakePlayerKill) {
            // Kick out of cameras (& removes armor stand) before killing the player
            plugin.securityCameraManager.exitCameras(victimProfile, victimPlayer);
        }
        victimProfile.die();
        generateDeadBody(event.getEntity().getLocation(), victimPlayer);
    }

    public void generateDeadBody(Location loc, @Nullable Player deadPlayer) {
        ArmorStand armorStand = loc.getWorld().spawn(loc.add(0.0, -1.15, 0.0), ArmorStand.class);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setArms(false);
        armorStand.setHeadPose(new EulerAngle(Math.PI/2, 0.0, Math.PI));
        armorStand.setBodyPose(new EulerAngle(Math.PI/2, 0.0, Math.PI));
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        if (deadPlayer != null) {
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            headMeta.setOwningPlayer(deadPlayer);
            head.setItemMeta(headMeta);
        }
        armorStand.getEquipment().setHelmet(head);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        armorMeta.setColor(Color.fromRGB(0x990000));
        chestplate.setItemMeta(armorMeta);
        armorStand.getEquipment().setChestplate(chestplate);
        armorStand.getPersistentDataContainer().set(ImpostorUtil.DEAD_BODY_KEY, PersistentDataType.INTEGER, 1);
    }

    // TODO: will you need to stop sounds on players?

}
