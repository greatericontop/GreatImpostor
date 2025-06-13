package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class AntiVandalism implements Listener {

    private final GreatImpostorMain plugin;
    public AntiVandalism(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.playerProfiles.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage("§cYou can't break blocks during a game!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.playerProfiles.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage("§cYou can't place blocks during a game!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        if (plugin.playerProfiles.containsKey(event.getViewers().get(0).getUniqueId())) {
            event.setCancelled(true); // disallows player to modify inventory (in most cases)
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        // separate event from dropping items *in the inventory*
        if (plugin.playerProfiles.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAttackItemFrame(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame && plugin.playerProfiles.containsKey(event.getDamager().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
