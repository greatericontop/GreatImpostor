package io.github.greatericontop.greatimpostor.meeting;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.profiles.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class MeetingManager {
    private static int MEETING_TIME_TICKS = 2100; // 1m 45s

    private int startTime = -1;
    private boolean meetingActive = false;
    private boolean postMeetingActive = false;
    private boolean doVoteCheck = true;
    public final Map<PlayerProfile, PlayerProfile> votes = new HashMap<>();
    public final Set<PlayerProfile> skips = new HashSet<>();

    private GreatImpostorMain plugin;
    public MeetingManager(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    public boolean isMeetingActive() {
        return meetingActive;
    }
    public boolean isVotingPhaseActive() {
        return meetingActive && !postMeetingActive;
    }

    public void callEmergencyMeeting(Player callingPlayer) {
        if (isMeetingActive()) {
            callingPlayer.sendMessage("§cThere is already a meeting in progress!");
            return;
        }
        PlayerProfile profile = plugin.playerProfiles.get(callingPlayer.getUniqueId());
        if (profile == null) {
            callingPlayer.sendMessage("§cCouldn't get your profile!");
            return;
        }
        if (!profile.isAlive()) {
            callingPlayer.sendMessage("§cYou can't call meetings while dead!");
            return;
        }
        if (plugin.sabotageManager.isDisruptiveSabotageActive()) {
            callingPlayer.sendMessage("§cSabotage! You can't call a meeting right now!");
            return;
        }
        if (profile.getMeetingsCalled() >= plugin.getConfig().getInt("max-meetings-per-player")) {
            callingPlayer.sendMessage("§cYou have already called the maximum number of meetings!");
            return;
        }
        profile.incrementMeetingsCalled();
        callingPlayer.sendMessage(String.format("§7%d/%d available meetings called", profile.getMeetingsCalled(), plugin.getConfig().getInt("max-meetings-per-player")));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showTitle(Title.title(
                    Component.text("§cEmergency Meeting"),
                    Component.text("§6" + callingPlayer.getName() + " called a meeting!"),
                    Title.Times.times(Duration.ofMillis(1500L), Duration.ofMillis(5000L), Duration.ofMillis(1500L))
            ));
        }
        startNewMeeting(true);
    }

    public void startNewMeeting(boolean isEmergency) {
        startTime = plugin.getClock();
        meetingActive = true;
        postMeetingActive = false;
        doVoteCheck = true;
        votes.clear();
        skips.clear();
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
        playMeetingSound();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        int number = plugin.gameManager.removeAllBodiesAndFakePlayers();
        if (number > 0) {
            if (isEmergency) {
                Bukkit.broadcast(Component.text(String.format("§e%d §6player(s) died between the last meeting and right now!", number)));
                Bukkit.broadcast(Component.text(""));
            } else {
                Bukkit.broadcast(Component.text(String.format("§6In addition to the reported body, §e%d §6player(s) died (§e%d §6died in total)", number, number+1)));
                Bukkit.broadcast(Component.text(""));
            }
        }
        List<String> alivePlayers = new ArrayList<>();
        List<String> deadPlayers = new ArrayList<>();
        for (PlayerProfile profile : plugin.playerProfiles.values()) {
            if (profile.isAlive()) {
                alivePlayers.add(profile.renderNameDisplay("§x§2§2§c§c§2§2"));
            } else {
                deadPlayers.add(profile.renderNameDisplay("§x§c§c§2§2§2§2"));
            }
            // Remove all players from cameras/vents
            if (profile instanceof ImpostorProfile impostorProfile && impostorProfile.isInVent) {
                impostorProfile.isInVent = false; // (This is actually all we need. Reference: VentManager.exitVent)
            }
            if (profile.isInCameras) {
                plugin.securityCameraManager.exitCameras(profile, profile.getPlayer());
            }
        }
        Bukkit.broadcastMessage(String.format("§x§2§2§c§c§2§2Alive §3Players: %s", String.join(", ", alivePlayers)));
        Bukkit.broadcastMessage(String.format("§x§c§c§2§2§2§2Dead §3Players: %s", String.join(", ", deadPlayers)));
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));
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
                impMessage = String.format("§e%s §bwas The Impostor.", toEject.getPlayer().getName());
            } else {
                impMessage = String.format("§e%s §bwas not The Impostor.", toEject.getPlayer().getName());
            }
            Bukkit.broadcast(Component.text(impMessage));
        }
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));

        if (toEject != null) {
            toEject.die(); // perform eject
        }

        postMeetingActive = true;
    }

    public void setMeetingActionBar(Player player) {
        if (postMeetingActive) {
            player.sendActionBar(Component.text("§6Continuing soon!"));
            return;
        }
        int secondsLeft = (MEETING_TIME_TICKS + startTime - plugin.getClock() + 19) / 20;
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
                int ticksLeft = MEETING_TIME_TICKS + startTime - plugin.getClock();
                if (ticksLeft == 0) {
                    endMeeting();
                }
                if (doVoteCheck && hasEveryoneVoted()) {
                    if (ticksLeft > 100) {
                        startTime = plugin.getClock() - MEETING_TIME_TICKS + 100; // adjust such that there are 100 ticks left
                        Bukkit.broadcast(Component.text("§aEveryone has voted! The meeting will end in 5 seconds."));
                    } else {
                        Bukkit.broadcast(Component.text("§aEveryone has voted! The meeting will end soon."));
                    }
                    doVoteCheck = false;
                }
                // actually put players back in game 5 seconds after the results are announced
                if (ticksLeft == -100) {
                    for (PlayerProfile profile : plugin.playerProfiles.values()) {
                        if (profile.isImpostor()) {
                            ImpostorProfile impostorProfile = (ImpostorProfile) profile;
                            impostorProfile.resetKillCooldown(false);
                            impostorProfile.resetSabotageCooldown(false);
                        }
                        profile.getPlayer().setGameMode(GameMode.ADVENTURE);
                        profile.getPlayer().teleport(plugin.getStartingLocation());
                    }
                    meetingActive = false;
                    postMeetingActive = false;
                }
                // while isMeetingActive is no longer true, this will still lock players in position for the next 5 seconds
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(plugin.getStartingLocation());
                }
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

    private boolean hasEveryoneVoted() {
        int alivePlayerCount = 0;
        for (PlayerProfile profile : plugin.playerProfiles.values()) {
            if (profile.isAlive()) {
                alivePlayerCount++;
            }
        }
        return votes.size() + skips.size() == alivePlayerCount;
    }

    /*
     * Kills the meeting. Used when the game ends
     */
    public void killMeeting() {
        meetingActive = false;
        postMeetingActive = false;
    }

}
