package io.github.greatericontop.greatimpostor.task.maintaskexecutors;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import io.github.greatericontop.greatimpostor.task.BaseTask;
import io.github.greatericontop.greatimpostor.task.TaskType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskSubmitScan extends BaseTask {

    public TaskSubmitScan(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBMIT_SCAN;
    }

    @Override
    public void startTask(Player player) {
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null) {
            player.sendMessage("§cCouldn't get your profile! This should never happen.");
            return;
        }
        if (profile.isImpostor()) {
            player.sendMessage("§cYou can't submit a scan if you're an impostor!");
            return;
        }

        Location playerLocation = player.getLocation();
        player.sendMessage("§3Starting scan, please don't move...");

        new BukkitRunnable() {
            int tickNum = 0;
            public void run() {
                tickNum++;
                if (tickNum == 240) { // 12 sec total
                    TaskSubmitScan.this.playSuccessSound(player);
                    TaskSubmitScan.this.taskSuccessful(player);
                    this.cancel();
                    return;
                }
                if (tickNum % 15 == 0) { // spawn particles every 0.75s
                    int i = tickNum / 15;
                    double yOffset;
                    if (i > 8) {
                        // moving down (2) 1.75 -> 0.25
                        yOffset = 2.0 - (i-8)*0.25;
                    } else {
                        // moving up 0.25 -> 2 (8 particle spawns @ 0.75s = 6 seconds)
                        yOffset = i * 0.25;
                    }
                    final int DIVISIONS = 60;
                    for (int angle = 0; angle < DIVISIONS; angle++) {
                        double radians = angle * 2 * Math.PI / DIVISIONS;
                        double xOffset = Math.cos(radians) * 0.9;
                        double zOffset = Math.sin(radians) * 0.9;
                        player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, playerLocation.clone().add(xOffset, yOffset, zOffset), 1, 0.0, 0.0, 0.0);
                    }
                }
                if (player.getLocation().distanceSquared(playerLocation) >= 0.01) { // (0.1)
                    player.sendMessage("§cPlease don't move while you're being scanned!");
                    TaskSubmitScan.this.playFailSound(player);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

}
