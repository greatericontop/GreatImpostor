package io.github.greatericontop.greatimpostor;

import io.github.greatericontop.greatimpostor.task.SwipeCard;
import io.github.greatericontop.greatimpostor.task.TaskAcceptPower;
import io.github.greatericontop.greatimpostor.task.TaskAdjustSteering;
import io.github.greatericontop.greatimpostor.task.TaskCleanOxygenFilter;
import io.github.greatericontop.greatimpostor.task.TaskClearAsteroids;
import io.github.greatericontop.greatimpostor.task.TaskDownloadData;
import io.github.greatericontop.greatimpostor.task.TaskEmptyTrash;
import io.github.greatericontop.greatimpostor.task.TaskEnterPassword;
import io.github.greatericontop.greatimpostor.task.TaskFetchFuel;
import io.github.greatericontop.greatimpostor.task.TaskFuelEngines;
import io.github.greatericontop.greatimpostor.task.TaskRedirectPower;
import io.github.greatericontop.greatimpostor.task.TaskStabilizeNavigation;
import io.github.greatericontop.greatimpostor.task.TaskStartReactor;
import io.github.greatericontop.greatimpostor.task.TaskUploadData;
import io.github.greatericontop.greatimpostor.task.TaskWiring;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GreatImpostorMain extends JavaPlugin {

    public TaskWiring taskWiring;
    public TaskRedirectPower taskRedirectPower;
    public TaskEnterPassword taskEnterPassword;
    public TaskEmptyTrash taskEmptyTrash;
    public TaskAdjustSteering taskAdjustSteering;
    public TaskAcceptPower taskAcceptPower;
    public TaskCleanOxygenFilter taskCleanOxygenFilter;
    public TaskClearAsteroids taskClearAsteroids;
    public TaskStartReactor taskStartReactor;
    public TaskStabilizeNavigation taskStabilizeNavigation;
    public TaskDownloadData taskDownloadData;
    public TaskUploadData taskUploadData;
    public SwipeCard taskSwipeCard;
    public TaskFetchFuel taskFetchFuel;
    public TaskFuelEngines taskFuelEngines;

    private int clock;
    public int getClock() {
        return clock;
    }

    @Override
    public void onEnable() {

        taskWiring = new TaskWiring();
        this.getServer().getPluginManager().registerEvents(taskWiring, this);
        taskRedirectPower = new TaskRedirectPower();
        this.getServer().getPluginManager().registerEvents(taskRedirectPower, this);
        taskEnterPassword = new TaskEnterPassword(this);
        this.getServer().getPluginManager().registerEvents(taskEnterPassword, this);
        taskEmptyTrash = new TaskEmptyTrash(this);
        this.getServer().getPluginManager().registerEvents(taskEmptyTrash, this);
        taskAdjustSteering = new TaskAdjustSteering(this);
        this.getServer().getPluginManager().registerEvents(taskAdjustSteering, this);
        taskAcceptPower = new TaskAcceptPower();
        this.getServer().getPluginManager().registerEvents(taskAcceptPower, this);
        taskCleanOxygenFilter = new TaskCleanOxygenFilter();
        this.getServer().getPluginManager().registerEvents(taskCleanOxygenFilter, this);
        taskClearAsteroids = new TaskClearAsteroids();
        this.getServer().getPluginManager().registerEvents(taskClearAsteroids, this);
        taskStartReactor = new TaskStartReactor();
        this.getServer().getPluginManager().registerEvents(taskStartReactor, this);
        taskStabilizeNavigation = new TaskStabilizeNavigation();
        this.getServer().getPluginManager().registerEvents(taskStabilizeNavigation, this);
        taskDownloadData = new TaskDownloadData(this);
        this.getServer().getPluginManager().registerEvents(taskDownloadData, this);
        taskUploadData = new TaskUploadData(this);
        this.getServer().getPluginManager().registerEvents(taskUploadData, this);
        taskSwipeCard = new SwipeCard(this);
        this.getServer().getPluginManager().registerEvents(taskSwipeCard, this);
        taskFetchFuel = new TaskFetchFuel(this);
        this.getServer().getPluginManager().registerEvents(taskFetchFuel, this);
        taskFuelEngines = new TaskFuelEngines(this);
        this.getServer().getPluginManager().registerEvents(taskFuelEngines, this);

        this.getCommand("impostor").setExecutor(new ImpostorCommand(this));

        clock = 1;
        new BukkitRunnable() {
            @Override
            public void run() {
                clock++;
            }
        }.runTaskTimer(this, 1L, 1L);

        this.getLogger().info("GreatImpostor finished setting up!");
    }

}
