package io.github.greatericontop.greatimpostor;

import io.github.greatericontop.greatimpostor.task.TaskAdjustSteering;
import io.github.greatericontop.greatimpostor.task.TaskEmptyTrash;
import io.github.greatericontop.greatimpostor.task.TaskEnterPassword;
import io.github.greatericontop.greatimpostor.task.TaskRedirectPower;
import io.github.greatericontop.greatimpostor.task.TaskWiring;
import org.bukkit.plugin.java.JavaPlugin;

public class GreatImpostorMain extends JavaPlugin {
    public TaskWiring taskWiring;
    public TaskRedirectPower taskRedirectPower;
    public TaskEnterPassword taskEnterPassword;
    public TaskEmptyTrash taskEmptyTrash;
    public TaskAdjustSteering taskAdjustSteering;

    @Override
    public void onEnable() {

        taskWiring = new TaskWiring();
        this.getServer().getPluginManager().registerEvents(taskWiring, this);
        taskRedirectPower = new TaskRedirectPower();
        this.getServer().getPluginManager().registerEvents(taskRedirectPower, this);
        taskEnterPassword = new TaskEnterPassword();
        this.getServer().getPluginManager().registerEvents(taskEnterPassword, this);
        taskEmptyTrash = new TaskEmptyTrash(this);
        this.getServer().getPluginManager().registerEvents(taskEmptyTrash, this);
        taskAdjustSteering = new TaskAdjustSteering(this);
        this.getServer().getPluginManager().registerEvents(taskAdjustSteering, this);

        this.getCommand("impostor").setExecutor(new ImpostorCommand(this));

        this.getLogger().info("GreatImpostor finished setting up!");
    }

}
