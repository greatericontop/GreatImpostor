package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.task.Subtask;
import io.github.greatericontop.greatimpostor.task.TaskType;
import io.github.greatericontop.greatimpostor.task.TaskUtil;
import io.github.greatericontop.greatimpostor.utils.Shuffler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class PlayerProfile {
    public static final int TASKS_PER = 4;

    protected final Random random;
    public final List<Subtask> tasks = new ArrayList<>(); // TODO change back to protected
    protected int[] subtasksCompletedPerTask = null;
    protected final Set<Subtask> tasksAlreadyCompleted = new HashSet<>();
    protected boolean alive;

    protected GreatImpostorMain plugin;
    protected final Player player;

    public Player getPlayer() {
        return player;
    }
    public boolean isAlive() {
        return alive;
    }

    public PlayerProfile(GreatImpostorMain plugin, Player player) {
        this.plugin = plugin;
        this.random = new Random();
        this.player = player;
        this.alive = true;
    }

    //

    /*
     * Get the number of tasks completed and the number that need to be completed
     */
    public static int[] getTaskStatus(Collection<PlayerProfile> profiles) {
        int completed = 0;
        int total = 0;
        for (PlayerProfile p : profiles) {
            if (p.isImpostor())  continue;
            for (int i = 0; i < p.tasks.size(); i++) {
                if (p.isFullyCompleted(i)) {
                    completed++;
                }
                total++;
            }
        }
        return new int[]{completed, total};
    }

    //

    public void setInitialTasks() {
        tasksAlreadyCompleted.clear();
        TaskType[] initialTasks = TaskUtil.INITIAL_TASKS.clone();

        do {
            tasks.clear();
            Shuffler.shuffle(initialTasks, random);
            for (int i = 0; i < TASKS_PER; i++) {
                Subtask[] possible = initialTasks[i].getPossibleNextSubtasks(0);
                tasks.add(possible[random.nextInt(possible.length)]);
            }
        } while (
                (getFrequentTaskCount() == 0 && random.nextDouble() < 0.85) // 85% chance to roll again if no frequent tasks
                || (getFrequentTaskCount() == 1 && random.nextDouble() < 0.3) // 30% chance to roll again if 1 frequent task
        );

        subtasksCompletedPerTask = new int[]{0, 0, 0, 0};
    }
    private int getFrequentTaskCount() {
        int count = 0;
        for (Subtask subtask : tasks) {
            if (subtask.getFullTask().isFrequent()) {
                count++;
            }
        }
        return count;
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
        return isFullyCompleted(taskIndex);
    }
    public boolean isFullyCompleted(int i) {
        return subtasksCompletedPerTask[i] >= tasks.get(i).getFullTask().getRequiredSubtaskCount();
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

    protected void dieGeneric() {
        alive = false;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            p.hidePlayer(plugin, player);
        }
        player.showTitle(Title.title(
                Component.text("§cYou Died!"),
                Component.text("§cYou Died!"),
                Title.Times.times(Duration.ofMillis(1000L), Duration.ofMillis(8000L), Duration.ofMillis(1000L))
        ));
        player.sendMessage("§7TEST MESSAGE");
    }

    public abstract void die();

    public abstract boolean isImpostor();

    public abstract void setActionBar();

    public abstract void setInventory();

}
