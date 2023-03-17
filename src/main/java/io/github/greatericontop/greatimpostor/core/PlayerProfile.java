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

    public boolean isFullyCompleted(TaskType taskType) {
        // find the index
        int taskIndex = -1;
        for (int i = 0; i < tasks.size(); i++) { // TODO doing this search every time is a little messy, maybe have better data structure for this
            if (tasks.get(i).getFullTask() == taskType) {
                taskIndex = i;
                break;
            }
        }
        if (taskIndex == -1) {
            return false; // not found
        }
        return subtasksCompletedPerTask[taskIndex] >= taskType.getRequiredSubtaskCount();
    }

    public void processSubtaskCompleted(TaskType taskType) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getFullTask() == taskType) {
                processSubtaskCompleted(i, true);
                return;
            }
        }
        throw new IllegalArgumentException("The given subtask couldn't be found in the list for this profile");
    }
    public void processSubtaskCompleted(int i, boolean shouldUpdateInventory) {
        subtasksCompletedPerTask[i]++;
        tasksAlreadyCompleted.add(tasks.get(i));
        TaskType fullTask = tasks.get(i).getFullTask();
        if (subtasksCompletedPerTask[i] < fullTask.getRequiredSubtaskCount()) {
            // (If you already have completed enough)
            //     This will simply show a green piece of glass with the last task shown as completed.
            Subtask[] possibleNextSubtasks = fullTask.getPossibleNextSubtasks(subtasksCompletedPerTask[i]);
            Subtask nextSubtask;
            do {
                nextSubtask = possibleNextSubtasks[random.nextInt(possibleNextSubtasks.length)];
            } while (fullTask.doAlreadyCompletedCheck() && tasksAlreadyCompleted.contains(nextSubtask));
            tasks.set(i, nextSubtask);
        }
        if (shouldUpdateInventory) {
            setInventory();
        }
    }

    public abstract boolean isImpostor();

    public abstract void setInventory();

}
