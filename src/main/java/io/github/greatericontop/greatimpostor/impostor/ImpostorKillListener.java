package io.github.greatericontop.greatimpostor.impostor;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ImpostorKillListener implements Listener {

    private final GreatImpostorMain plugin;
    public ImpostorKillListener(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player))  return;
        // if (!(event.getEntity() instanceof Player victim))  return;
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null)  return;
        if (!profile.isImpostor())  return;
        ImpostorProfile impostorProfile = (ImpostorProfile) profile;

        if (impostorProfile.getCanKill()) {
            event.setCancelled(true);
            impostorProfile.resetCooldown(false);
            ((Damageable) event.getEntity()).setHealth(0.0);
            // TODO: leave behind a head/armor stand/whatever
        } else {
            event.setCancelled(true);
            player.sendMessage("§cYou can't kill right now!");
        }
    }

    // TODO: will you need to stop sounds on players?

}
