package io.github.greatericontop.greatimpostor.meeting;

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
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VotingCommand implements CommandExecutor, TabCompleter {

    private final GreatImpostorMain plugin;
    public VotingCommand(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cPlayers only!");
            return true;
        }
        if (!plugin.meetingManager.isVotingPhaseActive()) {
            sender.sendMessage("§cYou can only vote during meetings!");
            return true;
        }
        PlayerProfile playerProfile = plugin.playerProfiles.get(player.getUniqueId());
        if (playerProfile == null) {
            sender.sendMessage("§cCouldn't get your profile!");
            return true;
        }
        if (!playerProfile.isAlive()) {
            sender.sendMessage("§cYou're dead! You can't vote!");
            return true;
        }

        if (args[0].equalsIgnoreCase("skip")) {
            plugin.meetingManager.votes.remove(playerProfile);
            plugin.meetingManager.skips.add(playerProfile);
            player.sendMessage("§dYou voted to skip!");
        } else {
            Player targetPlayer = plugin.getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage("§cCouldn't find that player!");
                return true;
            }
            PlayerProfile targetPlayerProfile = plugin.playerProfiles.get(targetPlayer.getUniqueId());
            if (targetPlayerProfile == null) {
                sender.sendMessage("§cCouldn't get that player's profile!");
                return true;
            }
            if (!targetPlayerProfile.isAlive()) {
                sender.sendMessage("§cThat player is dead! You can't vote for them!");
                return true;
            }
            plugin.meetingManager.skips.remove(playerProfile);
            plugin.meetingManager.votes.put(playerProfile, targetPlayerProfile);
            player.sendMessage(String.format("§dYou voted to eject §e%s§d!", targetPlayer.getName()));
        }

        int alivePlayerCount = 0;
        for (PlayerProfile profile : plugin.playerProfiles.values()) {
            if (profile.isAlive()) {
                alivePlayerCount++;
            }
        }
        Bukkit.broadcast(Component.text(String.format("§b%s §3voted (§e%d§3/§e%d§3)",
                player.getName(),
                plugin.meetingManager.votes.size() + plugin.meetingManager.skips.size(),
                alivePlayerCount)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> alivePlayers = new ArrayList<>();
            for (PlayerProfile profile : plugin.playerProfiles.values()) {
                if (profile.isAlive()) {
                    alivePlayers.add(profile.getPlayer().getName());
                }
            }
            alivePlayers.add("skip");
            return alivePlayers;
        }
        return List.of();
    }

}
