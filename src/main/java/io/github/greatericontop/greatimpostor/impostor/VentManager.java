package io.github.greatericontop.greatimpostor.impostor;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import io.github.greatericontop.greatimpostor.utils.PartialCoordinates;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VentManager implements Listener {

    private final GreatImpostorMain plugin;
    public VentManager(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onShift(PlayerToggleSneakEvent event) {
        if (!event.isSneaking())  return; // only runs when shift is toggled ON
        Player player = event.getPlayer();
        PlayerProfile profileGeneric = plugin.playerProfiles.get(player.getUniqueId());
        if (profileGeneric == null)  return;
        if (!profileGeneric.isImpostor())  return;
        ImpostorProfile profile = (ImpostorProfile) profileGeneric;
        if (profile.isInVent) {
            exitVent(player, profile);
        } else {
            enterVent(player, profile);
        }
    }

    @EventHandler()
    public void onSpace(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        PlayerProfile profileGeneric = plugin.playerProfiles.get(player.getUniqueId());
        if (profileGeneric == null)  return;
        if (!profileGeneric.isImpostor())  return;
        ImpostorProfile profile = (ImpostorProfile) profileGeneric;
        if (profile.isInVent) {
            cycleVent(player, profile);
        }
    }




    private void exitVent(Player player, ImpostorProfile profile) {
        profile.isInVent = false;

        PartialCoordinates coordinates = plugin.gameManager.getVent(profile.ventSystem, profile.ventNumber);
        player.teleport(coordinates.teleportLocation(player.getWorld()));

        // un-vanish
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            p1.showPlayer(plugin, player);
        }

        player.sendMessage("§7[D] exiting vent");
    }

    private void enterVent(Player player, ImpostorProfile profile) {
        Location playerLocation = player.getLocation();
        PartialCoordinates coordinates = PartialCoordinates.ofLocation(playerLocation);
        int[] ventData = plugin.gameManager.findVentSystem(coordinates);
        if (ventData == null)  return;
        int ventSystem = ventData[0];
        int ventNumber = ventData[1];

        profile.isInVent = true;
        profile.ventSystem = ventSystem;
        profile.ventNumber = ventNumber;
        profile.applyVentEntrancePenalty();

        // vanish
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 0));
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            p1.hidePlayer(plugin, player);
        }

        player.sendMessage(String.format("[D] entering §7ventSystem=%d ventIndex=%d", ventSystem, ventNumber));
    }

    private void cycleVent(Player player, ImpostorProfile profile) {
        int ventSystem = profile.ventSystem;
        int ventNumber = profile.ventNumber;
        int nextVentNumber = (ventNumber + 1) % plugin.gameManager.getVentCount(ventSystem);
        profile.ventNumber = nextVentNumber;
        PartialCoordinates coordinates = plugin.gameManager.getVent(ventSystem, nextVentNumber);
        player.teleport(coordinates.teleportLocation(player.getWorld()));
        player.sendMessage(String.format("[D] §eCYCLE §7ventSystem=%d ventIndex=%d", ventSystem, nextVentNumber));
    }

    public void setBackVentedImpostor(ImpostorProfile profile) {
        if (!profile.isInVent)  return;
        PartialCoordinates coordinates = plugin.gameManager.getVent(profile.ventSystem, profile.ventNumber);
        Player player = profile.getPlayer();
        if (coordinates.isClose(PartialCoordinates.ofLocation(player.getLocation())))  return;
        player.teleport(coordinates.teleportLocation(player.getWorld()));
    }

}
