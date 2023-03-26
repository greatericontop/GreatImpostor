package io.github.greatericontop.greatimpostor;

import io.github.greatericontop.greatimpostor.utils.StartGame;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ImpostorCommand implements CommandExecutor {

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

        if (args[0].equals("debug")) {
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            new DebugImpostorCommand(plugin).onCommand(sender, command, label, newArgs);
        }

        if (args[0].equals("tutorial")) {
            player.openBook(Book.book(
                    Component.text("Tutorial"),
                    Component.text("GreatImpostor"),
                    Component.text("§dWelcome to GreatImpostor!\n\n\n§0This is a tutorial to help you start."),
                    // crew
                    Component.text("§3§lCREWMATE\n\n\n§0Do your tasks!\n\nYou'll have 4 pieces of glass indicating what tasks to do. They'll change color as you complete them."),
                    Component.text("§0Right click signs to interact with them.\n\n§0You can vote during meetings using §6/vote§0."),
                    Component.text("§0Right click the horn to report a body.\n\nYou'll win if everyone finishes their tasks, or if the impostors are found and ejected."),
                    // impostor
                    Component.text("§c§lIMPOSTOR\n\n\n§0Kill everyone!\n\nAttack a crewmate with the sword to kill them."),
                    Component.text("§6HOTKEY §0to the TNT to choose a sabotage, and §6HOTKEY §0to the redstone to activate it.\n\n§6SNEAK §0over a vent to use it. Then §6SPACE §0to move around or §6SNEAK §0again to exit."),
                    Component.text("§0You'll win if you kill enough crewmates, or if the crew fails to repair a sabotage.")
            ));
            return true;
        }

        if (args[0].equals("start")) {
            StartGame.startGame(plugin, 1);
            return true;
        }

        return false;
    }

}
