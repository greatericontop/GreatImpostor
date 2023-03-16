package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.task.Subtask;
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
    public final List<Subtask> tasks = new ArrayList<>(); // TODO change back to protected
    protected int[] subtasksCompletedPerTask = null;
    protected final Set<Subtask> tasksAlreadyCompleted = new HashSet<>();

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
        Subtask[] possible0 = initialTasks[0].getPossibleNextSubtasks(0);
        tasks.add(possible0[random.nextInt(possible0.length)]);
        Subtask[] possible1 = initialTasks[1].getPossibleNextSubtasks(0);
        tasks.add(possible1[random.nextInt(possible1.length)]);
        Subtask[] possible2 = initialTasks[2].getPossibleNextSubtasks(0);
        tasks.add(possible2[random.nextInt(possible2.length)]);
        subtasksCompletedPerTask = new int[]{0, 0, 0};
    }

    public void processSubtaskCompleted(Subtask subtask) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).equals(subtask)) {
                processSubtaskCompleted(i);
            }
        }
        throw new IllegalArgumentException("The given subtask couldn't be found in the list for this profile");
    }
    public void processSubtaskCompleted(int i) {
        subtasksCompletedPerTask[i]++;
        tasksAlreadyCompleted.add(tasks.get(i));
        if (subtasksCompletedPerTask[i] >= tasks.get(i).getFullTask().getRequiredSubtaskCount()) {
            // This will simply show a green piece of glass with the last task shown as completed.
            return;
        }
        Subtask[] possibleNextSubtasks = tasks.get(i).getFullTask().getPossibleNextSubtasks(subtasksCompletedPerTask[i]);
        Subtask nextSubtask;
        do {
            nextSubtask = possibleNextSubtasks[random.nextInt(possibleNextSubtasks.length)];
        } while (tasksAlreadyCompleted.contains(nextSubtask));
        tasks.set(i, nextSubtask);
    }

    public abstract void setInventory();

}
