package io.github.greatericontop.greatimpostor;

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

import io.github.greatericontop.greatimpostor.core.events.AntiVandalism;
import io.github.greatericontop.greatimpostor.core.events.BodyReportingListener;
import io.github.greatericontop.greatimpostor.core.GameManager;
import io.github.greatericontop.greatimpostor.core.events.DeadChatHandler;
import io.github.greatericontop.greatimpostor.core.events.PlayerJoinHandler;
import io.github.greatericontop.greatimpostor.core.events.SecurityCameraManager;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import io.github.greatericontop.greatimpostor.core.impostor.ImpostorKillListener;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskAnalyzeSample;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskPrimeShields;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskSubmitScan;
import io.github.greatericontop.greatimpostor.task.sabotage.SabotageManager;
import io.github.greatericontop.greatimpostor.core.impostor.VentManager;
import io.github.greatericontop.greatimpostor.meeting.MeetingManager;
import io.github.greatericontop.greatimpostor.meeting.VotingCommand;
import io.github.greatericontop.greatimpostor.task.SignListener;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskAcceptPower;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskAdjustSteering;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskCleanOxygenFilter;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskClearAsteroids;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskDownloadData;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskEmptyTrash;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskFetchFuel;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskFuelEngines;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskRedirectPower;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskStabilizeNavigation;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskStartReactor;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskSwipeCard;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskUnlockManifolds;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskUploadData;
import io.github.greatericontop.greatimpostor.task.maintaskexecutors.TaskWiring;
import io.github.greatericontop.greatimpostor.task.sabotagetaskexecutors.SabotageCommunications;
import io.github.greatericontop.greatimpostor.task.sabotagetaskexecutors.SabotageFixLights;
import io.github.greatericontop.greatimpostor.task.sabotagetaskexecutors.SabotageOxygen;
import io.github.greatericontop.greatimpostor.task.sabotagetaskexecutors.SabotageReactor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GreatImpostorMain extends JavaPlugin {

    public TaskWiring taskWiring;
    public TaskRedirectPower taskRedirectPower;
    public TaskStartReactor taskStartReactor;
    public TaskEmptyTrash taskEmptyTrash;
    public TaskAdjustSteering taskAdjustSteering;
    public TaskAcceptPower taskAcceptPower;
    public TaskCleanOxygenFilter taskCleanOxygenFilter;
    public TaskClearAsteroids taskClearAsteroids;
    public TaskUnlockManifolds taskUnlockManifolds;
    public TaskStabilizeNavigation taskStabilizeNavigation;
    public TaskDownloadData taskDownloadData;
    public TaskUploadData taskUploadData;
    public TaskSwipeCard taskSwipeCard;
    public TaskFetchFuel taskFetchFuel;
    public TaskFuelEngines taskFuelEngines;
    public TaskSubmitScan taskSubmitScan;
    public TaskPrimeShields taskPrimeShields;
    public TaskAnalyzeSample taskAnalyzeSample;

    public SabotageFixLights sabotageFixLights;
    public SabotageReactor sabotageReactor;
    public SabotageOxygen sabotageOxygen;
    public SabotageCommunications sabotageCommunications;

    public MeetingManager meetingManager;
    public SabotageManager sabotageManager;
    public VentManager ventManager;
    public SecurityCameraManager securityCameraManager;

    public GameManager gameManager;

    public final Map<UUID, PlayerProfile> playerProfiles = new HashMap<>();

    private int clock;
    public int getClock() {
        return clock;
    }

    @Override
    public void onEnable() {

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders(this).register();
        } else {
            getLogger().warning("PlaceholderAPI not found");
        }

        this.getServer().getPluginManager().registerEvents(new AntiVandalism(this), this);
        this.getServer().getPluginManager().registerEvents(new BodyReportingListener(this), this);
        this.getServer().getPluginManager().registerEvents(new DeadChatHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new ImpostorKillListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinHandler(this), this);
        ventManager = new VentManager(this);
        this.getServer().getPluginManager().registerEvents(ventManager, this);
        sabotageManager = new SabotageManager(this);
        this.getServer().getPluginManager().registerEvents(sabotageManager, this);
        securityCameraManager = new SecurityCameraManager(this);
        this.getServer().getPluginManager().registerEvents(securityCameraManager, this);
        meetingManager = new MeetingManager(this);
        meetingManager.registerMeetingRunnable();

        // main tasks
        this.getServer().getPluginManager().registerEvents(new SignListener(this), this);
        taskWiring = new TaskWiring(this);
        this.getServer().getPluginManager().registerEvents(taskWiring, this);
        taskRedirectPower = new TaskRedirectPower(this);
        this.getServer().getPluginManager().registerEvents(taskRedirectPower, this);
        taskStartReactor = new TaskStartReactor(this);
        this.getServer().getPluginManager().registerEvents(taskStartReactor, this);
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
        taskUnlockManifolds = new TaskUnlockManifolds(this);
        this.getServer().getPluginManager().registerEvents(taskUnlockManifolds, this);
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
        taskSubmitScan = new TaskSubmitScan(this);
        this.getServer().getPluginManager().registerEvents(taskSubmitScan, this);
        taskPrimeShields = new TaskPrimeShields(this);
        this.getServer().getPluginManager().registerEvents(taskPrimeShields, this);
        taskAnalyzeSample = new TaskAnalyzeSample(this);
        this.getServer().getPluginManager().registerEvents(taskAnalyzeSample, this);
        // sabotage tasks
        sabotageFixLights = new SabotageFixLights(this);
        this.getServer().getPluginManager().registerEvents(sabotageFixLights, this);
        sabotageReactor = new SabotageReactor(this);
        this.getServer().getPluginManager().registerEvents(sabotageReactor, this);
        sabotageOxygen = new SabotageOxygen(this);
        this.getServer().getPluginManager().registerEvents(sabotageOxygen, this);
        sabotageCommunications = new SabotageCommunications(this);
        this.getServer().getPluginManager().registerEvents(sabotageCommunications, this);

        //
        //
        //

        gameManager = new GameManager(this);
        gameManager.registerGameRunnable();
        gameManager.loadVents();

        ImpostorCommand impostorCommand = new ImpostorCommand(this);
        this.getCommand("impostor").setExecutor(impostorCommand);
        this.getCommand("impostor").setTabCompleter(impostorCommand);
        VotingCommand votingCommand = new VotingCommand(this);
        this.getCommand("vote").setExecutor(votingCommand);
        this.getCommand("vote").setTabCompleter(votingCommand);

        clock = 1;
        new BukkitRunnable() {
            public void run() {
                clock++;
            }
        }.runTaskTimer(this, 1L, 1L);

        this.getLogger().info("GreatImpostor finished setting up!");
    }

    public Location getStartingLocation() {
        World world = this.getServer().getWorld(this.getConfig().getString("starting-location.world-name"));
        if (world == null) {
            this.getLogger().warning("The world specified in the config does not exist! Using the default world instead.");
            world = this.getServer().getWorlds().get(0);
        }
        return new Location(
                world,
                this.getConfig().getDouble("starting-location.x"),
                this.getConfig().getDouble("starting-location.y"),
                this.getConfig().getDouble("starting-location.z")
        );
    }

}
