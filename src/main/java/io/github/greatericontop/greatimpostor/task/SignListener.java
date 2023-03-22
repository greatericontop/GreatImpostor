package io.github.greatericontop.greatimpostor.task;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import io.github.greatericontop.greatimpostor.impostor.SabotageSubtask;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SignListener implements Listener {
    public static final NamespacedKey TASK_SIGN_KEY = new NamespacedKey("greatimpostor", "task_sign");

    private final GreatImpostorMain plugin;
    public SignListener(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onSignClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)  return;
        if (event.getClickedBlock() == null)  return;
        if (event.getClickedBlock().getType() != Material.OAK_SIGN && event.getClickedBlock().getType() != Material.OAK_WALL_SIGN)  return;
        Sign signBlock = (Sign) event.getClickedBlock().getState();
        PersistentDataContainer pdc = signBlock.getPersistentDataContainer();
        if (!pdc.has(TASK_SIGN_KEY, PersistentDataType.STRING))  return;
        String subtaskName = pdc.get(TASK_SIGN_KEY, PersistentDataType.STRING);
        Player player = event.getPlayer();
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null) {
            player.sendMessage("§cCouldn't get your profile!");
            return;
        }

        if (subtaskName.equalsIgnoreCase("@emergency")) {
            plugin.meetingManager.haveEmergencyMeeting(player);
            return;
        }
        if (subtaskName.startsWith("@sabotage=")) {
            executeSabotageTask(player, subtaskName.replaceFirst("@sabotage=", "")); // note: the regex "@sabotage=" just matches the literal
            return;
        }
        executeMainTask(profile, player, subtaskName);
    }

    private void executeSabotageTask(Player player, String sabotageName) {
        SabotageSubtask sabotage = SabotageSubtask.valueOf(sabotageName);
        // Check if it's activated
        if (plugin.sabotageManager.getActiveSabotage() != sabotage.getFullSabotage()) {
            player.sendMessage("§cThis sabotage does not need to be fixed!");
            return;
        }
        BaseSabotageTask baseSabotageTask = TaskUtil.getSabotageTaskClass(plugin, sabotage.getFullSabotage());
        baseSabotageTask.startTask(player, sabotage);
    }

    private void executeMainTask(PlayerProfile profile, Player player, String subtaskName) {
        Subtask subtask = Subtask.valueOf(subtaskName);
        // Check if it's in the list
        if (!profile.tasks.contains(subtask)) {
            player.sendMessage("§cYou don't have this task!");
            return;
        }
        // Check if it's already completed
        if (profile.isFullyCompleted(subtask.getFullTask())) {
            player.sendMessage("§cYou already fully completed this task!");
            return;
        }
        BaseTask baseTask = TaskUtil.getTaskClass(plugin, subtask);
        baseTask.startTask(player);
    }

}
