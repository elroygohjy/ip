package Duke;

import Duke.Exception.InvalidIndexException;
import Duke.Exception.NoDateException;
import Duke.Exception.NoTaskDescriptionException;
import Duke.Exception.NoDateIndicatorException;
import java.util.ArrayList;
import java.util.List;

import Duke.Task.Task;
import Duke.Task.Event;
import Duke.Task.ToDo;
import Duke.Task.Deadline;


public class TaskList {

    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Add task to list.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes the task from list.
     *
     * @param task task
     */
    private void deleteTask(Task task) {
        tasks.remove(task);
    }

    /**
     * Marks the task as complete and prints out the task, throws error if done command is not properly formatted.
     *
     * @param userInput the index of the task in list
     */
    public void completeAndPrintTask(String userInput) {
        String[] tokens = userInput.split(" ");
        int taskNumber;
        boolean isTaskDoneCommand = tokens.length == 2 && tokens[0].equals("done");
        try {
            try {
                taskNumber = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException err) {
                throw new InvalidIndexException("done");
            }
            if (!isTaskDoneCommand || taskNumber > tasks.size()) {
                throw new InvalidIndexException("done");
            }
        } catch (InvalidIndexException err) {
            UI.printErrorMessage(err);
            return;
        }

        UI.printLine();
        UI.printRobotMsg("You have completed the following task:");
        tasks.get(taskNumber - 1).completeTask();
        UI.printMsg(tasks.get(taskNumber - 1).toString());
        UI.printLine();
    }

    /**
     * Delete the task and prints out the task and current number of tasks,
     * throws error if done command is not properly formatted.
     *
     * @param userInput the index of the task in list
     */
    public void deleteAndPrintTask(String userInput) {
        String[] tokens = userInput.split(" ");
        int taskNumber;
        boolean isTaskDeleteCommand = tokens.length == 2 && tokens[0].equals("delete");
        try {
            try {
                taskNumber = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException err) {
                throw new InvalidIndexException("delete");
            }

            if (!isTaskDeleteCommand || taskNumber > tasks.size()) {
                throw new InvalidIndexException("delete");
            }
        } catch (InvalidIndexException err) {
            UI.printErrorMessage(err);
            return;
        }

        UI.printLine();
        UI.printRobotMsg("You have removed the following task:");
        UI.printMsg(tasks.get(taskNumber - 1).toString());
        deleteTask(tasks.get(taskNumber - 1));
        UI.printRobotMsg("You have " + tasks.size() + " task/s left.");
        UI.printLine();
    }

    /**
     * Prints all the tasks in the list.
     */
    public void printList() {
        UI.printLine();
        UI.printRobotMsg("Your list contains the following task/s:");
        for (int i = 0; i < tasks.size(); i++) {
            UI.printMsg((i + 1) + ". " + tasks.get(i).toString());
        }
        UI.printLine();
    }

    /**
     * Returns newly created Duke.Task.Event Duke.Task.ToDo task otherwise throw an appropriate error.
     *
     * @param userInput user input String
     * @return todo task.
     * @throws NoTaskDescriptionException if there is no task description, it will result in this error.
     */
    public ToDo makeToDoTask(String userInput) throws NoTaskDescriptionException {
        if (userInput.length() == "todo".length()) {
            throw new NoTaskDescriptionException("todo");
        }
        return new ToDo(userInput.substring("todo".length() + 1));
    }

    /**
     * Returns newly created Duke.Task.Event task otherwise throw an appropriate error.
     *
     * @param userInput user input String
     * @return event task
     * @throws NoTaskDescriptionException if there is no task description, it will result in this error.
     * @throws NoDateIndicatorException if there is no /by indicator, it will result in this error.
     * @throws NoDateException if there is no date, it will result in this error.
     */
    public Task makeEventTask(String userInput) throws
            NoTaskDescriptionException, NoDateIndicatorException, NoDateException {
        int indexOfAt = userInput.indexOf("/at");
        int EndIndexForEvent = "event".length();
        boolean doesAtExist = indexOfAt != -1;
        boolean doesTimeExist = userInput.length() > indexOfAt + "/at".length() + 1;
        boolean doesEventDescriptionExist = indexOfAt != EndIndexForEvent + 1;

        if (!doesAtExist) {
            throw new NoDateIndicatorException("/at");
        }
        if (!doesTimeExist) {
            throw new NoDateException("event");
        }
        if (!doesEventDescriptionExist) {
            throw new NoTaskDescriptionException("event");
        }

        return new Event(userInput.substring("event".length() + 1, indexOfAt - 1),
                userInput.substring(indexOfAt + "/at".length() + 1));
    }

    /**
     * Returns newly created Duke.Task.Event Duke.Task.Deadline task otherwise throw an appropriate error.
     *
     * @param userInput user input String
     * @return deadline task
     * @throws NoTaskDescriptionException if there is no task description, it will result in this error.
     * @throws NoDateIndicatorException if there is no /by indicator, it will result in this error.
     * @throws NoDateException if there is no date, it will result in this error.
     */
    public Deadline makeDeadlineTask(String userInput) throws NoTaskDescriptionException,
            NoDateIndicatorException, NoDateException {
        int indexOfBy = userInput.indexOf("/by");
        int EndIndexForDeadline = "deadline".length();
        boolean doesByExist = indexOfBy != -1;
        boolean doesDateExist = userInput.length() > indexOfBy + "/by".length() + 1;
        boolean doesDeadlineDescriptionExist = indexOfBy != EndIndexForDeadline + 1;

        if (!doesByExist) {
            throw new NoDateIndicatorException("/by");
        }
        if (!doesDateExist) {
            throw new NoDateException("deadline");
        }

        if (!doesDeadlineDescriptionExist) {
            throw new NoTaskDescriptionException("deadline");
        }
        String stringDate = userInput.substring(indexOfBy + "/by".length() + 1);
        return new Deadline(userInput.substring("deadline".length() + 1, indexOfBy - 1), stringDate);

    }

    /**
     * Returns the number of tasks.
     *
     * @return number of tasks
     */
    public int noOfTasks() {
        return tasks.size();
    }

    /**
     * Returns encoded string list for saving
     *
     */
    public List<String> encodeTasks() {
        ArrayList<String> encodedTasks = new ArrayList<>();
        for (Task task: tasks) {
            encodedTasks.add(task.toStringSave());
        }
        return encodedTasks;
    }

}