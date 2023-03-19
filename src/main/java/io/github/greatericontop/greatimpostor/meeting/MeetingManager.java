package io.github.greatericontop.greatimpostor.meeting;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class MeetingManager {
    private static int MEETING_TIME_TICKS = 800; //2100; // 1m 45s

    private int startTime;
    public final Map<PlayerProfile, PlayerProfile> votes = new HashMap<>();
    public final Set<PlayerProfile> skips = new HashSet<>();

    private GreatImpostorMain plugin;
    public MeetingManager(GreatImpostorMain plugin) {
        this.plugin = plugin;
        startTime = -2;
    }

    public boolean isMeetingActive() {
        return startTime != -2;
    }

    public void haveEmergencyMeeting(Player callingPlayer) {
        if (isMeetingActive()) {
            callingPlayer.sendMessage("§cThere is already a meeting in progress!");
            return;
        }
        PlayerProfile profile = plugin.playerProfiles.get(callingPlayer.getUniqueId());
        if (profile == null) {
            callingPlayer.sendMessage("§cCouldn't get your profile!");
            return;
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showTitle(Title.title(
                    Component.text("§cEmergency Meeting"),
                    Component.text("§6" + callingPlayer.getName() + " called a meeting!"),
                    Title.Times.times(Duration.ofMillis(1500L), Duration.ofMillis(5000L), Duration.ofMillis(1500L))
            ));
        }
        startNewMeeting();
    }

    public void startNewMeeting() {
        startTime = plugin.getClock();
        votes.clear();
        skips.clear();
        // TODO teleport to some central location & freeze all players
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§4GreatImpostor"));
        Bukkit.broadcast(Component.text("§6----- §aA meeting was called! §6-----"));
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§3Who is the Impostor?"));
        Bukkit.broadcast(Component.text("§3Use §b/vote §3to vote."));
        Bukkit.broadcast(Component.text("§3You can vote for a player, e.g. §b/vote Notch"));
        Bukkit.broadcast(Component.text("§3Or skip the vote, e.g. §b/vote skip"));
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));
        playMeetingSound();
    }

    public void endMeeting() {
        PlayerProfile toEject = doHighestVoted();
        Bukkit.broadcast(Component.text(""));
        if (toEject == null) {
            Bukkit.broadcast(Component.text("§bNobody was ejected!"));
        } else {
            Bukkit.broadcast(Component.text(String.format("§e%s §bwas ejected!", toEject.getPlayer().getName())));
            String impMessage;
            if (toEject.isImpostor()) {
                impMessage = String.format("§c%s §6was The Impostor.", toEject.getPlayer().getName());
            } else {
                impMessage = String.format("§c%s §6was not The Impostor.", toEject.getPlayer().getName());
            }
            Bukkit.broadcast(Component.text(impMessage));
            toEject.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));

        for (PlayerProfile profile : plugin.playerProfiles.values()) {
            if (profile.isImpostor()) {
                ((ImpostorProfile) profile).resetCooldown(false);
            }
        }
        startTime = -2;
    }

    public void setMeetingActionBar(Player player) {
        int secondsLeft = (startTime + MEETING_TIME_TICKS - plugin.getClock() + 19) / 20;
        String timeLeft = String.format("%d:%02d", secondsLeft / 60, secondsLeft % 60);
        player.sendActionBar(Component.text(String.format("§6Meeting - %s", timeLeft)));
    }

    private void playMeetingSound() {
        new BukkitRunnable() {
            int i = 0;
            public void run() {
                if (i >= 35) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                }
                i++;
            }
        }.runTaskTimer(plugin, 1L, 2L);
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

    /*
     * Return the highest voted profile, or `null` if the majority is to skip or there's a tie.
     */
    public PlayerProfile doHighestVoted() {
        Map<PlayerProfile, Integer> voteCount = new HashMap<>();
        for (PlayerProfile targetProfile : votes.values()) {
            voteCount.put(targetProfile, voteCount.getOrDefault(targetProfile, 0) + 1);
        }
        // initialize with stats of skip
        int maxVotes = skips.size();
        boolean isTied = false;
        PlayerProfile highestVoted = null;
        // go through the vote counts
        for (Map.Entry<PlayerProfile, Integer> entry : voteCount.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                highestVoted = entry.getKey();
                isTied = false;
            } else if (entry.getValue() == maxVotes) {
                isTied = true;
            }
        }
        doMessages(voteCount, skips.size());

        if (isTied) {
            return null;
        }
        return highestVoted;
    }

    private void doMessages(Map<PlayerProfile, Integer> voteCount, int skipCount) {
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text(String.format("§aSkip§7: §b%d", skipCount)));
        Bukkit.broadcast(Component.text(""));

        SortedMap<Integer, Set<PlayerProfile>> sortedVotes = new TreeMap<>(Comparator.reverseOrder());
        for (Map.Entry<PlayerProfile, Integer> entry : voteCount.entrySet()) {
            sortedVotes.putIfAbsent(entry.getValue(), new HashSet<>());
            sortedVotes.get(entry.getValue()).add(entry.getKey());
        }
        for (Map.Entry<Integer, Set<PlayerProfile>> entry : sortedVotes.entrySet()) {
            for (PlayerProfile profile : entry.getValue()) {
                Bukkit.broadcast(Component.text(String.format("§e%s§7: §b%d", profile.getPlayer().getName(), entry.getKey())));
            }
        }

        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));
    }

}
