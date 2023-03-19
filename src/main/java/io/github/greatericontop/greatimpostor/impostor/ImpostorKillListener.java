package io.github.greatericontop.greatimpostor.impostor;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;

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
        if (!profile.isImpostor()) {
            event.setCancelled(true);
            player.sendMessage("§cYou can't attack others!");
            return;
        }
        ImpostorProfile impostorProfile = (ImpostorProfile) profile;

        if (impostorProfile.getCanKill()) {
            event.setCancelled(true);
            impostorProfile.resetKillCooldown(false);
            ((Damageable) event.getEntity()).setHealth(0.0);
            generateDeadBody(event.getEntity().getLocation());
        } else {
            event.setCancelled(true);
            player.sendMessage("§cYou can't kill right now!");
        }
    }

    private void generateDeadBody(Location loc) {
        ArmorStand armorStand = loc.getWorld().spawn(loc.add(0.0, -1.15, 0.0), ArmorStand.class);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setArms(false);
        armorStand.setHeadPose(new EulerAngle(Math.PI/2, 0.0, Math.PI));
        armorStand.setBodyPose(new EulerAngle(Math.PI/2, 0.0, Math.PI));
        armorStand.getEquipment().setHelmet(new ItemStack(Material.PLAYER_HEAD, 1));
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        LeatherArmorMeta im = (LeatherArmorMeta) chestplate.getItemMeta();
        im.setColor(Color.fromRGB(0x990000));
        chestplate.setItemMeta(im);
        armorStand.getEquipment().setChestplate(chestplate);
        armorStand.getPersistentDataContainer().set(ImpostorUtil.DEAD_BODY_KEY, PersistentDataType.INTEGER, 1);
    }

    // TODO: will you need to stop sounds on players?

}
