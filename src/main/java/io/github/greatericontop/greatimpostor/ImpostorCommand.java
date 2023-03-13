package io.github.greatericontop.greatimpostor;

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

        if (args[0].equals("taskWiring")) {
            plugin.taskWiring.startTask(player);
        }

        if (args[0].equals("taskRedirectPower")) {
            plugin.taskRedirectPower.startTask(player);
        }

        if (args[0].equals("taskEnterPassword")) {
            plugin.taskEnterPassword.startTask(player);
        }

        return false;
    }

}
