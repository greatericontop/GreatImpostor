package io.github.greatericontop.greatimpostor;

import io.github.greatericontop.greatimpostor.task.TaskWiring;
import org.bukkit.plugin.java.JavaPlugin;

public class GreatImpostorMain extends JavaPlugin {
    public TaskWiring taskWiring;

    @Override
    public void onEnable() {

        taskWiring = new TaskWiring();
        this.getServer().getPluginManager().registerEvents(taskWiring, this);

        this.getCommand("impostor").setExecutor(new ImpostorCommand(this));

        this.getLogger().info("GreatImpostor finished setting up!");
    }

}
