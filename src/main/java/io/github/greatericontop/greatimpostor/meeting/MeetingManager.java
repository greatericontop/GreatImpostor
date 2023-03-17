package io.github.greatericontop.greatimpostor.meeting;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class MeetingManager {
    private static int MEETING_TIME_TICKS = 600; //2100; // 1m 45s

    private int startTime;
    private Map<PlayerProfile, PlayerProfile> votes = new HashMap<>();

    private GreatImpostorMain plugin;
    public MeetingManager(GreatImpostorMain plugin) {
        this.plugin = plugin;
        startTime = -2;
    }

    public boolean isMeetingActive() {
        return startTime != -2;
    }

    public void startNewMeeting() {
        startTime = plugin.getClock();
        votes.clear();
        // TODO teleport to some central location & freeze all players
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§4GreatImpostor"));
        Bukkit.broadcast(Component.text("§6----- §aA meeting was called! §6-----"));
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§3Who is the Impostor?"));
        Bukkit.broadcast(Component.text("§3Use §b/vote §3to vote."));
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));
        playMeetingSound();
    }

    public void endMeeting() {
        // TODO check votes & eject person
        startTime = -2;
    }

    public void setMeetingActionBar(Player player) {
        int secondsLeft = (startTime + MEETING_TIME_TICKS - plugin.getClock() + 19) / 20;
        String timeLeft = String.format("%d:%02d", secondsLeft / 60, secondsLeft % 60);
        player.sendActionBar(Component.text(String.format("§6[Meeting] §3%s", timeLeft)));
    }

    private void playMeetingSound() {
        new BukkitRunnable() {
            int i = 0;
            public void run() {
                if (i >= 20) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                }
                i++;
            }
        }.runTaskTimer(plugin, 1L, 3L);
    }

    public void registerMeetingRunnable() {
        new BukkitRunnable() {
            public void run() {
                if (!isMeetingActive())  return;
                if (startTime + MEETING_TIME_TICKS <= plugin.getClock()) {
                    endMeeting();
                }
                // end meeting if enough votes were cast
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

}
