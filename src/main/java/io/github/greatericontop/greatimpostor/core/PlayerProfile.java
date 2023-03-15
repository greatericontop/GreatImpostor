package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.task.TaskType;
import io.github.greatericontop.greatimpostor.task.TaskUtil;
import io.github.greatericontop.greatimpostor.utils.Shuffler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class PlayerProfile {
    protected final Random random;
    protected final List<TaskType> tasks = new ArrayList<>();
    protected int[] subtasksCompletedPerTask = null;
    protected final Set<TaskType> tasksAlreadyCompleted = new HashSet<>();

    private final Player player;
    public Player getPlayer() {
        return player;
    }

    public PlayerProfile(Player player) {
        this.random = new Random();
        this.player = player;
    }

    //

    public void setInitialTasks() {
        tasks.clear();
        tasksAlreadyCompleted.clear();
        TaskType[] initialTasks = TaskUtil.INITIAL_TASKS.clone();
        Shuffler.shuffle(initialTasks, random);
        tasks.add(initialTasks[0]);
        tasks.add(initialTasks[1]);
        tasks.add(initialTasks[2]);
        subtasksCompletedPerTask = new int[]{0, 0, 0};
    }

    public abstract void setInventory();

}
