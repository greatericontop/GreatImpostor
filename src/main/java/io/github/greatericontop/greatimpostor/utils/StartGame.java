package io.github.greatericontop.greatimpostor.utils;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.CrewmateProfile;
import io.github.greatericontop.greatimpostor.core.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Collection;
import java.util.Random;

public class StartGame {

    public static void startGame(GreatImpostorMain plugin, int numberImpostors, @Nullable Player responsiblePlayer) {
        Random random = new Random();
        Collection<Player> playersRaw = (Collection<Player>) Bukkit.getOnlinePlayers();
        Player[] players = playersRaw.toArray(new Player[0]);
        Shuffler.shuffle(players, random);

        // add everyone to the team
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam("greatimpostor_players");
        if (team == null) {
            team = scoreboard.registerNewTeam("greatimpostor_players");
        }
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        team.addEntities(players);

        // re-show all players
        for (Player p1 : players) {
            for (Player p2 : players) {
                if (!p1.equals(p2)) {
                    p1.showPlayer(plugin, p2);
                }
            }
        }

        for (int i = 0; i < players.length; i++) {
            Player currentPlayer = players[i];
            PlayerProfile newProfile;
            String title;
            String subtitle;
            if (i < numberImpostors) {
                newProfile = new ImpostorProfile(plugin, currentPlayer);
                title = "§cIMPOSTOR";
                subtitle = "§eKill off the crew!";
            } else {
                newProfile = new CrewmateProfile(plugin, currentPlayer);
                title = "§bCREWMATE";
                subtitle = "§eDo your tasks and find the impostor!";
            }
            plugin.playerProfiles.put(currentPlayer.getUniqueId(), newProfile);

            currentPlayer.getInventory().clear();
            currentPlayer.setGameMode(GameMode.ADVENTURE);
            currentPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 160, 0));
            currentPlayer.teleport(plugin.getStartingLocation());
            currentPlayer.showTitle(Title.title(
                    Component.text(title),
                    Component.text(subtitle),
                    Title.Times.times(Duration.ofMillis(1000L), Duration.ofMillis(7000L), Duration.ofMillis(2000L))
            ));
            currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
        }

        // remove old bodies
        if (responsiblePlayer != null) {
            responsiblePlayer.sendMessage("§7Removing old dead bodies. If your server has too many entities, it might lag for a few seconds.");
        }
        int amount = 0;
        for (Entity entity : plugin.getStartingLocation().getWorld().getEntities()) {
            if (entity instanceof ArmorStand armorStand) {
                if (armorStand.getPersistentDataContainer().has(ImpostorUtil.DEAD_BODY_KEY, PersistentDataType.INTEGER)) {
                    armorStand.remove();
                    amount++;
                }
            }
        }
        if (responsiblePlayer != null) {
            responsiblePlayer.sendMessage(String.format("§7Finished removing %d dead bodies.", amount));
        }

        new BukkitRunnable() {
            int i = 8;
            public void run() {
                if (i <= 0) {
                    cancel();
                    return;
                }
                if (i > 1) {
                    Bukkit.broadcast(Component.text(String.format("§eGame will begin in §b%d §eseconds!", i-1)));
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.teleport(plugin.getStartingLocation()); // tp back because we can't exactly stop them from moving
                }
                i--;
            }
        }.runTaskTimer(plugin, 20L, 20L);

        new BukkitRunnable() {
            public void run() {
                for (PlayerProfile profile : plugin.playerProfiles.values()) {
                    profile.setInitialTasks();
                    profile.setInventory();
                    if (profile.isImpostor()) {
                        ImpostorProfile impostorProfile = (ImpostorProfile) profile;
                        impostorProfile.resetKillCooldown(true);
                        impostorProfile.resetSabotageCooldown(true);
                    }
                }
            }
        }.runTaskLater(plugin, 160L);

    }

}
