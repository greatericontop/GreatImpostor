package io.github.greatericontop.greatimpostor;

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

import io.github.greatericontop.greatimpostor.core.StartGame;
import io.github.greatericontop.greatimpostor.core.profiles.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ImpostorCommand implements CommandExecutor, TabCompleter {

    private final GreatImpostorMain plugin;
    public ImpostorCommand(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0)  return false;

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player to use this command!");
            return true;
        }
        Player player = (Player) sender;

        if (args[0].equals("tutorial")) {
            player.openBook(Book.book(
                    Component.text("Tutorial"),
                    Component.text("GreatImpostor"),
                    Component.text("§dWelcome to GreatImpostor!\n\n\n§0This is a tutorial to help you start."),
                    // crew
                    Component.text("§3§lCREWMATE\n\n\n§0Do your tasks!\n\nYou'll have 4 pieces of glass indicating what tasks to do. Check their names to see what room to go to and what to do. They'll change color as you complete them."),
                    Component.text("§0Right click signs to interact with them.\n\n§0You can vote during meetings using §6/vote§0."),
                    Component.text("§0Right click the horn to report a body.\n\nYou'll win if everyone finishes their tasks, or if the impostors are found and ejected."),
                    Component.text("§0If you can't find the room, look around, and there should be directions on the walls."),
                    // impostor
                    Component.text("§c§lIMPOSTOR\n\n\n§0Kill everyone!\n\nAttack a crewmate with the sword to kill them."),
                    Component.text("§6HOTKEY §0to the TNT to choose a sabotage, and §6HOTKEY §0to the redstone to activate it.\n\n§6SNEAK §0over a vent to use it. Then §6JUMP §0to move around or §6SNEAK §0again to exit."),
                    Component.text("§0You'll win if you kill enough crewmates, or if the crew fails to repair a sabotage.")
            ));
            return true;
        }

        if (args[0].equals("start")) {
            if (!player.hasPermission("impostor.admin")) {
                player.sendMessage("§cYou need §6impostor.admin §cto access these commands!");
                return true;
            }
            int impostorCount = 1;
            if (args.length >= 2) {
                try {
                    impostorCount = Integer.parseInt(args[1]);
                    StartGame.startGame(plugin, impostorCount, player);
                    return true;
                } catch (NumberFormatException e) {
                    player.sendMessage("§cInvalid number of players specified!");
                    return true;
                }
            }
            StartGame.startGame(plugin, impostorCount, player);
            return true;
        }

        if (args[0].equals("config")) {
            if (args.length == 1) {
                player.sendMessage("§9--------------------------------------------------");
                player.sendMessage("§bGame Config");
                player.sendMessage("");
                int meetingTimeTicks = plugin.getConfig().getInt("meeting-time-ticks");
                player.sendMessage(Component.text(String.format("§3Meeting time: §e%d§3t    ", meetingTimeTicks))
                        .append(Component.text("§7[edit]")
                                .clickEvent(ClickEvent.suggestCommand(String.format("/impostor config meeting-time-ticks %d", meetingTimeTicks)))));
                int criticalSabotageFixTicks = plugin.getConfig().getInt("critical-sabotage-fix-ticks");
                player.sendMessage(Component.text(String.format("§3Critical sabotage fix time: §e%d§3t    ", criticalSabotageFixTicks))
                        .append(Component.text("§7[edit]")
                                .clickEvent(ClickEvent.suggestCommand(String.format("/impostor config critical-sabotage-fix-ticks %d", criticalSabotageFixTicks)))));
                int maxMeetingsPerPlayer = plugin.getConfig().getInt("max-meetings-per-player");
                player.sendMessage(Component.text(String.format("§3Max meetings per player: §e%d§3    ", maxMeetingsPerPlayer))
                        .append(Component.text("§7[edit]")
                                .clickEvent(ClickEvent.suggestCommand(String.format("/impostor config max-meetings-per-player %d", maxMeetingsPerPlayer)))));
                player.sendMessage("");
                int cooldownKillGameStart = plugin.getConfig().getInt("impostor-cooldowns.kill-game-start");
                player.sendMessage(Component.text(String.format("§3Cooldown kill (game start): §e%d§3t    ", cooldownKillGameStart))
                        .append(Component.text("§7[edit]")
                                .clickEvent(ClickEvent.suggestCommand(String.format("/impostor config cooldowns.kill-game-start %d", cooldownKillGameStart)))));
                int cooldownSabotageGameStart = plugin.getConfig().getInt("impostor-cooldowns.sabotage-game-start");
                player.sendMessage(Component.text(String.format("§3Cooldown sabotage (game start): §e%d§3t    ", cooldownSabotageGameStart))
                        .append(Component.text("§7[edit]")
                                .clickEvent(ClickEvent.suggestCommand(String.format("/impostor config cooldowns.sabotage-game-start %d", cooldownSabotageGameStart)))));
                int cooldownKillAfterUse = plugin.getConfig().getInt("impostor-cooldowns.kill-after-use");
                player.sendMessage(Component.text(String.format("§3Cooldown kill (after use): §e%d§3t    ", cooldownKillAfterUse))
                        .append(Component.text("§7[edit]")
                                .clickEvent(ClickEvent.suggestCommand(String.format("/impostor config cooldowns.kill-after-use %d", cooldownKillAfterUse)))));
                int cooldownSabotageAfterUse = plugin.getConfig().getInt("impostor-cooldowns.sabotage-after-use");
                player.sendMessage(Component.text(String.format("§3Cooldown sabotage (after use): §e%d§3t    ", cooldownSabotageAfterUse))
                        .append(Component.text("§7[edit]")
                                .clickEvent(ClickEvent.suggestCommand(String.format("/impostor config cooldowns.sabotage-after-use %d", cooldownSabotageAfterUse)))));
                int cooldownKillAfterMeeting = plugin.getConfig().getInt("impostor-cooldowns.kill-after-meeting");
                player.sendMessage(Component.text(String.format("§3Cooldown kill (after meeting): §e%d§3t    ", cooldownKillAfterMeeting))
                        .append(Component.text("§7[edit]")
                                .clickEvent(ClickEvent.suggestCommand(String.format("/impostor config cooldowns.kill-after-meeting %d", cooldownKillAfterMeeting)))));
                int cooldownSabotageAfterMeeting = plugin.getConfig().getInt("impostor-cooldowns.sabotage-after-meeting");
                player.sendMessage(Component.text(String.format("§3Cooldown sabotage (after meeting): §e%d§3t    ", cooldownSabotageAfterMeeting))
                        .append(Component.text("§7[edit]")
                                .clickEvent(ClickEvent.suggestCommand(String.format("/impostor config cooldowns.sabotage-after-meeting %d", cooldownSabotageAfterMeeting)))));
                player.sendMessage("§9--------------------------------------------------");
                return true;
            } else if (args[1].equals("meeting-time-ticks") || args[1].equals("critical-sabotage-fix-ticks") || args[1].equals("max-meetings-per-player")
                    || args[1].equals("cooldowns.kill-game-start") || args[1].equals("cooldowns.sabotage-game-start") || args[1].equals("cooldowns.kill-after-use") || args[1].equals("cooldowns.sabotage-after-use") || args[1].equals("cooldowns.kill-after-meeting") || args[1].equals("cooldowns.sabotage-after-meeting")
            ) {
                if (args.length == 2) {
                    player.sendMessage("§cSpecify a value!");
                    return true;
                }
                int value;
                try {
                    value = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cInvalid number specified!");
                    return true;
                }
                if (value <= 0) {
                    player.sendMessage("§cValue must be positive!");
                    return true;
                }
                if (args[1].startsWith("cooldowns.")) {
                    args[1] = args[1].replace("cooldowns.", "impostor-cooldowns.");
                }
                plugin.getConfig().set(args[1], value);
                plugin.saveConfig();
                player.sendMessage(String.format("§3Successfully set §b%s §3to §b%d.", args[1], value));
                return true;
            } else {
                player.sendMessage("§cThat's not an option!");
                return true;
            }
        }

        if (args[0].equals("listimpostors")) {
            PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
            if (profile == null) {
                player.sendMessage("§cYou are not in a game!");
                return true;
            }
            if (!(profile instanceof ImpostorProfile)) {
                player.sendMessage("§cYou can only use this command as an impostor!");
                return true;
            }
            List<String> impostorNames = new ArrayList<>();
            for (PlayerProfile p : plugin.playerProfiles.values()) {
                if (p instanceof ImpostorProfile && !profile.equals(p)) {
                    impostorNames.add(String.format("§c%s§3", p.getPlayer().getName()));
                }
            }
            if (impostorNames.isEmpty()) {
                player.sendMessage("§3You are the only impostor!");
            } else {
                player.sendMessage(String.format("§3Your Fellow Impostors: %s", String.join(", ", impostorNames)));
                if (args.length > 1 && args[1].equals("_showhint_")) {
                    player.sendMessage("§3Use §b/impostor listimpostors §cto show this message again.");
                }
            }
            return true;
        }

        if (args[0].equals("debug")) {
            if (!player.hasPermission("impostor.admin")) {
                player.sendMessage("§cYou need §6impostor.admin §cto access these commands!");
                return true;
            }
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            new DebugImpostorCommand(plugin).onCommand(sender, command, label, newArgs);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("start", "tutorial", "config", "listimpostors", "debug");
        } else if (args.length == 2 && args[0].equals("start")) {
            return List.of("<number of impostors>");
        } else if (args.length >= 2 && args[0].equals("config")) {
            if (args.length == 2) {
                return List.of("meeting-time-ticks", "critical-sabotage-fix-ticks", "max-meetings-per-player",
                        "cooldowns.kill-game-start", "cooldowns.sabotage-game-start", "cooldowns.kill-after-use", "cooldowns.sabotage-after-use", "cooldowns.kill-after-meeting", "cooldowns.sabotage-after-meeting");
            } else if (args[1].equals("meeting-time-ticks") || args[1].equals("critical-sabotage-fix-ticks") || args[1].equals("max-meetings-per-player")
                    || args[1].equals("cooldowns.kill-game-start") || args[1].equals("cooldowns.sabotage-game-start") || args[1].equals("cooldowns.kill-after-use") || args[1].equals("cooldowns.sabotage-after-use") || args[1].equals("cooldowns.kill-after-meeting") || args[1].equals("cooldowns.sabotage-after-meeting")
            ) {
                return List.of("<integer>");
            } else {
                return List.of();
            }
        } else {
            return List.of();
        }
    }

}
