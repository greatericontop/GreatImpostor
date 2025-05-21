package io.github.greatericontop.greatimpostor;

import io.github.greatericontop.greatimpostor.core.CrewmateProfile;
import io.github.greatericontop.greatimpostor.core.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import io.github.greatericontop.greatimpostor.task.SignListener;
import io.github.greatericontop.greatimpostor.utils.StartGame;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class DebugImpostorCommand implements CommandExecutor {

    private final GreatImpostorMain plugin;
    public DebugImpostorCommand(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7task[Wiring RedirectPower StartReactor EmptyTrash AdjustSteering AcceptPower CleanOxygenFilter ClearAsteroids UnlockManifolds StabilizeNavigation DownloadData UploadData SwipeCard FetchFuel FuelEngines]");
            sender.sendMessage("§7test[Crewmate1 CrewmateTaskComplete Impostor1]");
            sender.sendMessage("§7setSign <task>");
            sender.sendMessage("§7fixSabotage");
            sender.sendMessage("§7die");
            sender.sendMessage("§7start");
            return false;
        }

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
        if (args[0].equals("taskStartReactor")) {
            plugin.taskStartReactor.startTask(player);
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
        if (args[0].equals("taskUnlockManifolds")) {
            plugin.taskUnlockManifolds.startTask(player);
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

        if (args[0].equals("testCrewmate1")) {
            CrewmateProfile c = new CrewmateProfile(plugin, player);
            c.setInitialTasks();
            player.sendMessage(String.valueOf(c.tasks));
            c.setInventory();
            plugin.playerProfiles.put(player.getUniqueId(), c);
            return true;
        }
        if (args[0].equals("testCrewmateTaskComplete")) {
            CrewmateProfile c = (CrewmateProfile) plugin.playerProfiles.get(player.getUniqueId());
            c.processSubtaskCompleted(Integer.parseInt(args[1]), false);
            c.setInventory();
            return true;
        }
        if (args[0].equals("testImpostor1")) {
            ImpostorProfile imp = new ImpostorProfile(plugin, player);
            imp.setInitialTasks();
            imp.setInventory();
            plugin.playerProfiles.put(player.getUniqueId(), imp);
            return true;
        }

        if (args[0].equals("setSign")) {
            Block lookingAt = player.getTargetBlock(10);
            if (lookingAt == null || (lookingAt.getType() != Material.OAK_WALL_SIGN && lookingAt.getType() != Material.OAK_SIGN)) {
                player.sendMessage("§cYou must be looking at a sign!");
                return true;
            }
            Sign sign = (Sign) lookingAt.getState(false); // get real state as we're going to modify it
            sign.getPersistentDataContainer().set(SignListener.TASK_SIGN_KEY, PersistentDataType.STRING, args[1]);
            player.sendMessage("§aSet your sign to be: " + args[1]);
            return true;
        }

        if (args[0].equals("fixSabotage")) {
            plugin.sabotageManager.forceEndSabotage();
            return true;
        }

        if (args[0].equals("die")) {
            PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
            profile.die();
            return true;
        }

        if (args[0].equals("start")) {
            StartGame.startGame(plugin, 1, player);
            return true;
        }

        return false;
    }

}
