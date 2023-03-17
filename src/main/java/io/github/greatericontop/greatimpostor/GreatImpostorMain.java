package io.github.greatericontop.greatimpostor;

import io.github.greatericontop.greatimpostor.core.AntiVandalism;
import io.github.greatericontop.greatimpostor.core.BodyReportingListener;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import io.github.greatericontop.greatimpostor.impostor.ImpostorKillListener;
import io.github.greatericontop.greatimpostor.meeting.MeetingManager;
import io.github.greatericontop.greatimpostor.meeting.VotingCommand;
import io.github.greatericontop.greatimpostor.task.SignListener;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskAcceptPower;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskAdjustSteering;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskCleanOxygenFilter;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskClearAsteroids;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskDownloadData;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskEmptyTrash;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskEnterPassword;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskFetchFuel;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskFuelEngines;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskRedirectPower;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskStabilizeNavigation;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskStartReactor;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskSwipeCard;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskUploadData;
import io.github.greatericontop.greatimpostor.task.taskexecutors.TaskWiring;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    public TaskSwipeCard taskSwipeCard;
    public TaskFetchFuel taskFetchFuel;
    public TaskFuelEngines taskFuelEngines;

    public MeetingManager meetingManager;

    public final Map<UUID, PlayerProfile> playerProfiles = new HashMap<>();

    private int clock;
    public int getClock() {
        return clock;
    }

    @Override
    public void onEnable() {

        this.getServer().getPluginManager().registerEvents(new AntiVandalism(this), this);
        this.getServer().getPluginManager().registerEvents(new BodyReportingListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ImpostorKillListener(this), this);

        meetingManager = new MeetingManager(this);
        meetingManager.registerMeetingRunnable();

        this.getServer().getPluginManager().registerEvents(new SignListener(this), this);
        taskWiring = new TaskWiring(this);
        this.getServer().getPluginManager().registerEvents(taskWiring, this);
        taskRedirectPower = new TaskRedirectPower(this);
        this.getServer().getPluginManager().registerEvents(taskRedirectPower, this);
        taskEnterPassword = new TaskEnterPassword(this);
        this.getServer().getPluginManager().registerEvents(taskEnterPassword, this);
        taskEmptyTrash = new TaskEmptyTrash(this);
        this.getServer().getPluginManager().registerEvents(taskEmptyTrash, this);
        taskAdjustSteering = new TaskAdjustSteering(this);
        this.getServer().getPluginManager().registerEvents(taskAdjustSteering, this);
        taskAcceptPower = new TaskAcceptPower(this);
        this.getServer().getPluginManager().registerEvents(taskAcceptPower, this);
        taskCleanOxygenFilter = new TaskCleanOxygenFilter(this);
        this.getServer().getPluginManager().registerEvents(taskCleanOxygenFilter, this);
        taskClearAsteroids = new TaskClearAsteroids(this);
        this.getServer().getPluginManager().registerEvents(taskClearAsteroids, this);
        taskStartReactor = new TaskStartReactor(this);
        this.getServer().getPluginManager().registerEvents(taskStartReactor, this);
        taskStabilizeNavigation = new TaskStabilizeNavigation(this);
        this.getServer().getPluginManager().registerEvents(taskStabilizeNavigation, this);
        taskDownloadData = new TaskDownloadData(this);
        this.getServer().getPluginManager().registerEvents(taskDownloadData, this);
        taskUploadData = new TaskUploadData(this);
        this.getServer().getPluginManager().registerEvents(taskUploadData, this);
        taskSwipeCard = new TaskSwipeCard(this);
        this.getServer().getPluginManager().registerEvents(taskSwipeCard, this);
        taskFetchFuel = new TaskFetchFuel(this);
        this.getServer().getPluginManager().registerEvents(taskFetchFuel, this);
        taskFuelEngines = new TaskFuelEngines(this);
        this.getServer().getPluginManager().registerEvents(taskFuelEngines, this);

        this.getCommand("impostor").setExecutor(new ImpostorCommand(this));
        this.getCommand("vote").setExecutor(new VotingCommand(this));

        clock = 1;
        new BukkitRunnable() {
            public void run() {
                clock++;
            }
        }.runTaskTimer(this, 1L, 1L);

        new BukkitRunnable() {
            public void run() {
                for (PlayerProfile profile : playerProfiles.values()) {
                    if (meetingManager.isMeetingActive()) {
                        meetingManager.setMeetingActionBar(profile.getPlayer());
                    } else {
                        profile.setActionBar();
                    }
                }
            }
        }.runTaskTimer(this, 1L, 1L);

        this.getLogger().info("GreatImpostor finished setting up!");
    }

}
