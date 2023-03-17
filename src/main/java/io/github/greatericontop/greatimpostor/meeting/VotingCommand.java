package io.github.greatericontop.greatimpostor.meeting;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VotingCommand implements CommandExecutor {

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
        if (!plugin.meetingManager.isMeetingActive()) {
            sender.sendMessage("§cYou can only vote during meetings!");
            return true;
        }
        PlayerProfile playerProfile = plugin.playerProfiles.get(player.getUniqueId());
        if (playerProfile == null) {
            sender.sendMessage("§cCouldn't get your profile!");
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
            plugin.meetingManager.skips.remove(playerProfile);
            plugin.meetingManager.votes.put(playerProfile, targetPlayerProfile);
            player.sendMessage(String.format("§dYou voted to eject §e%s§d!", targetPlayer.getName()));
        }
        return true;
    }

}
