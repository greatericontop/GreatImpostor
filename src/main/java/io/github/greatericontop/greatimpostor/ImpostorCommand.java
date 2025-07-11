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
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
            return List.of("start", "tutorial", "debug");
        } else if (args.length == 2 && args[0].equals("start")) {
            return List.of("<number of impostors>");
        } else {
            return List.of();
        }
    }

}
