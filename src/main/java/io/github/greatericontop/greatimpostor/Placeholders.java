package io.github.greatericontop.greatimpostor;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Placeholders extends PlaceholderExpansion {

    private final GreatImpostorMain plugin;
    public Placeholders(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "greateric";
    }

    @Override
    public String getIdentifier() {
        return "GreatImpostor";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String args) {

        if (args.equals("player_list")) {
            StringBuilder sb = new StringBuilder();
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                sb.append(p.getName()).append(", ");
            }
            return sb.substring(0, sb.length() - 2);
        }

        return null;
    }

}
