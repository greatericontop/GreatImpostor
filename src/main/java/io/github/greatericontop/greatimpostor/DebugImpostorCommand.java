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

import io.github.greatericontop.greatimpostor.core.profiles.CrewmateProfile;
import io.github.greatericontop.greatimpostor.core.profiles.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import io.github.greatericontop.greatimpostor.core.impostor.ImpostorKillListener;
import io.github.greatericontop.greatimpostor.task.SignListener;
import io.github.greatericontop.greatimpostor.core.StartGame;
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
import io.github.greatericontop.greatimpostor.utils.ItemMaker;
import io.github.greatericontop.greatimpostor.utils.PlayerColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
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
            sender.sendMessage("§7spawnDeadBody / spawnFakePlayer / fixSabotage / die / start / deleteme");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to use this command!");
            return true;
        }

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
            CrewmateProfile c = new CrewmateProfile(plugin, player, PlayerColor.RED);
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
            ImpostorProfile imp = new ImpostorProfile(plugin, player, PlayerColor.RED);
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

        if (args[0].equals("spawnDeadBody")) {
            new ImpostorKillListener(plugin).generateDeadBody(player.getLocation(), player);
            return true;
        }

        if (args[0].equals("spawnFakePlayer")) {
            PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
            ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            armorStand.setGravity(false);
            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            headMeta.setOwningPlayer(player);
            head.setItemMeta(headMeta);
            armorStand.getEquipment().setHelmet(head);
            armorStand.getEquipment().setChestplate(ItemMaker.createLeatherArmor(Material.LEATHER_CHESTPLATE, profile.getColor().getColorCode(), ""));
            armorStand.getEquipment().setLeggings(ItemMaker.createLeatherArmor(Material.LEATHER_LEGGINGS, profile.getColor().getColorCode(), ""));
            armorStand.getEquipment().setBoots(ItemMaker.createLeatherArmor(Material.LEATHER_BOOTS, profile.getColor().getColorCode(), ""));
            armorStand.getPersistentDataContainer().set(ImpostorUtil.FAKE_PLAYER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
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

        if (args[0].equals("deleteme")) {
            plugin.playerProfiles.remove(player.getUniqueId());
            player.sendMessage("§3You have been removed from the game.");
            return true;
        }

        return false;
    }

}
