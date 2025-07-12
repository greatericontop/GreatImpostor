package io.github.greatericontop.greatimpostor.core;

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

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.profiles.CrewmateProfile;
import io.github.greatericontop.greatimpostor.core.profiles.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import io.github.greatericontop.greatimpostor.utils.PlayerColor;
import io.github.greatericontop.greatimpostor.utils.Shuffler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
        plugin.gameManager.showAllPlayers();

        // who gets what color
        PlayerColor[] colors = PlayerColor.values();
        Shuffler.shuffle(colors, random);
        if (players.length > colors.length) {
            if (responsiblePlayer != null) {
                responsiblePlayer.sendMessage("§cToo many players for the available number of player colors!");
            }
            return;
        }

        // any task handlers that need resets
        plugin.taskAnalyzeSample.resetSelf();

        for (int i = 0; i < players.length; i++) {
            Player currentPlayer = players[i];
            PlayerProfile newProfile;
            String title;
            String subtitle;
            if (i < numberImpostors) {
                newProfile = new ImpostorProfile(plugin, currentPlayer, colors[i]);
                title = "§cIMPOSTOR";
                subtitle = "§eKill off the crew!";
            } else {
                newProfile = new CrewmateProfile(plugin, currentPlayer, colors[i]);
                title = "§bCREWMATE";
                subtitle = "§eDo your tasks and find the impostor!";
            }
            plugin.playerProfiles.put(currentPlayer.getUniqueId(), newProfile);

            currentPlayer.getInventory().clear();
            currentPlayer.setGameMode(GameMode.ADVENTURE);
            currentPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
            currentPlayer.teleport(plugin.getStartingLocation());
            currentPlayer.showTitle(Title.title(
                    Component.text(title),
                    Component.text(subtitle),
                    Title.Times.times(Duration.ofMillis(1000L), Duration.ofMillis(3000L), Duration.ofMillis(2000L))
            ));
            currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0F, 1.0F);
            currentPlayer.sendMessage(String.format("§bYour color is: §3[%s§3]§b.", colors[i].getDisplayName()));
        }

        // remove old bodies (after a few ticks (1 tick delay is not enough), when the chunks have loaded)
        new BukkitRunnable() {
            public void run() {
                if (responsiblePlayer != null) {
                    responsiblePlayer.sendMessage("§7Removing old dead bodies. If your server has too many entities, it might lag for a few seconds.");
                }
                long start = System.currentTimeMillis();
                int amount = plugin.gameManager.removeAllBodiesAndFakePlayers();
                if (responsiblePlayer != null) {
                    responsiblePlayer.sendMessage(String.format("§7Finished removing %d dead bodies in %d ms.", amount, System.currentTimeMillis()-start));
                }
            }
        }.runTaskLater(plugin, 5L);

        new BukkitRunnable() {
            int i = 4;
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
                    profile.getPlayer().getInventory().setHeldItemSlot(0); // force reset held item so impostors don't reveal themselves
                    if (profile.isImpostor()) {
                        ImpostorProfile impostorProfile = (ImpostorProfile) profile;
                        impostorProfile.resetKillCooldown(true);
                        impostorProfile.getPlayer().chat("/impostor listimpostors _showhint_");
                    }
                }
                plugin.gameManager.resetAllSabotageCooldowns(true);
            }
        }.runTaskLater(plugin, 80L);

    }

}
