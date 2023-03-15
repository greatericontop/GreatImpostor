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
            return true;
        }
        if (args[0].equals("taskRedirectPower")) {
            plugin.taskRedirectPower.startTask(player);
            return true;
        }
        if (args[0].equals("taskEnterPassword")) {
            plugin.taskEnterPassword.startTask(player);
            return true;
        }
        if (args[0].equals("taskEmptyTrash")) {
            plugin.taskEmptyTrash.startTask(player);
            return true;
        }
        if (args[0].equals("taskAdjustSteering")) {
            plugin.taskAdjustSteering.startTask(player);
            return true;
        }
        if (args[0].equals("taskAcceptPower")) {
            plugin.taskAcceptPower.startTask(player);
            return true;
        }
        if (args[0].equals("taskCleanOxygenFilter")) {
            plugin.taskCleanOxygenFilter.startTask(player);
            return true;
        }
        if (args[0].equals("taskClearAsteroids")) {
            plugin.taskClearAsteroids.startTask(player);
            return true;
        }
        if (args[0].equals("taskStartReactor")) {
            plugin.taskStartReactor.startTask(player);
            return true;
        }
        if (args[0].equals("taskStabilizeNavigation")) {
            plugin.taskStabilizeNavigation.startTask(player);
            return true;
        }
        if (args[0].equals("taskDownloadData")) {
            plugin.taskDownloadData.startTask(player);
            return true;
        }
        if (args[0].equals("taskUploadData")) {
            plugin.taskUploadData.startTask(player);
            return true;
        }
        if (args[0].equals("taskSwipeCard")) {
            plugin.taskSwipeCard.startTask(player);
            return true;
        }
        if (args[0].equals("taskFetchFuel")) {
            plugin.taskFetchFuel.startTask(player);
            return true;
        }
        if (args[0].equals("taskFuelEngines")) {
            plugin.taskFuelEngines.startTask(player);
            return true;
        }

        return false;
    }

}
