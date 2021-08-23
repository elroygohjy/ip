import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Duke is a simple bot that allows users to keep track of different types of tasks.
 */
public class Duke {

    private final static String ROBOT_ICON = "[~o_o~]";
    private final static String BYE = "bye";
    private final static String LINE = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
    private final static String ROBOT_TEXT_SPACE = "         ";

    private static final ArrayList<Task> list = new ArrayList<>();

    /**
     * Prints greeting text of the robotFriend.
     */
    private static void greet() {
        System.out.println(LINE);
        System.out.println(ROBOT_ICON + ": Hello I am your RobotFriend.\n" + ROBOT_TEXT_SPACE + "How can i help you?");
        System.out.println(ROBOT_TEXT_SPACE + "Commands:");
        System.out.println(ROBOT_TEXT_SPACE + "bye: terminate session.");
        System.out.println(ROBOT_TEXT_SPACE + "list: view all tasks in the list.");
        System.out.println(ROBOT_TEXT_SPACE + "done i: mark task number i as complete and view the task.");
        System.out.println(ROBOT_TEXT_SPACE + "delete i: delete the task at the given index i.");
        System.out.println(ROBOT_TEXT_SPACE + "todo *description*: add todo task.");
        System.out.println(ROBOT_TEXT_SPACE + "event *description* /at *date*: add event task.");
        System.out.println(ROBOT_TEXT_SPACE + "deadline *description* /by *timing*: add deadline task.");
        System.out.println(LINE);
    }

    /**
     * Echos the user inputted String in a robot way.
     *
     * @param userInput user inputted String
     */
    private static void echo(String userInput) {
        System.out.println(LINE);
        System.out.println(ROBOT_ICON + ": " + userInput);
        System.out.println(LINE);
    }

    /**
     * Prints the exiting text in a robot way.
     */
    private static void bye() {
        System.out.println(LINE);
        System.out.println(ROBOT_ICON + ": " + "Bye~ Hope to see you soon :)");
        System.out.println(LINE);
    }


    /**
     * Prints current number of tasks.
     */
    private static void printCurrentNumberOfTask() {
        System.out.println(ROBOT_ICON + ": " + "Now you have " + list.size() + " tasks!");
    }

    /**
     * Add task to list.
     */
    private static void addTask(Task task) {
        list.add(task);
    }

    /**
     * Prints error message.
     *
     * @param e error
     */
    private static void printErrorMessage(Exception e) {
        System.out.println(LINE);
        System.out.println(e.getMessage());
        System.out.println(LINE);
    }

    /**
     * Returns ToDo task otherwise throw an appropriate error.
     *
     * @param userInput user input String
     * @return todo task.
     * @throws NoTaskDescriptionException if there is no task description, it will result in this error.
     */
    private static ToDo makeToDoTask(String userInput) throws NoTaskDescriptionException {
        if (userInput.length() == "todo".length()) {
            throw new NoTaskDescriptionException("todo");
        }
        return new ToDo(userInput.substring("todo".length() + 1));
    }

    /**
     * Returns Event task otherwise throw an appropriate error.
     *
     * @param userInput user input String
     * @return event task
     * @throws NoTaskDescriptionException if there is no task description, it will result in this error.
     * @throws NoDateIndicatorException if there is no /by indicator, it will result in this error.
     * @throws NoDateException if there is no date, it will result in this error.
     */
    private static Task makeEventTask(String userInput) throws
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
     * Returns Deadline task otherwise throw an appropriate error.
     *
     * @param userInput user input String
     * @return deadline task
     * @throws NoTaskDescriptionException if there is no task description, it will result in this error.
     * @throws NoDateIndicatorException if there is no /by indicator, it will result in this error.
     * @throws NoDateException if there is no date, it will result in this error.
     */
    private static Deadline makeDeadlineTask(String userInput) throws NoTaskDescriptionException,
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
        return new Deadline(userInput.substring("deadline".length() + 1, indexOfBy - 1),
                userInput.substring(indexOfBy + "/by".length() + 1));
    }

    /**
     * Overwrites and save new task list in data/duke.txt file when there is changes such as deleting and adding task.
     */
    private static void saveData() {
        try {
            FileWriter myWriter = new FileWriter("data/duke.txt");
            for (Task task: list) {
                myWriter.write(task.toStringSave() + '\n');
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds user inputted String to list and prints the user added tasks and the current number of tasks, else
     * print error message for the error
     *
     * @param userInput user inputted String
     */
    private static void otherCommands(String userInput) {
        Task newTask;
        String[] tokens = userInput.split(" ");
        try {
            switch (tokens[0]) {
            case "done":
                completeAndPrintTask(userInput);
                return;
            case "delete":
                deleteAndPrintTask(userInput);
                return;
            case "todo":
                newTask = makeToDoTask(userInput);
                break;
            case "deadline":
                newTask = makeDeadlineTask(userInput);
                break;
            case "event":
                newTask = makeEventTask(userInput);
                break;
            default:
                throw new InvalidCommandException();
            }
        } catch (InvalidCommandException | NoTaskDescriptionException | NoDateIndicatorException | NoDateException err) {
            printErrorMessage(err);
            return;
        }
        addTask(newTask);
        saveData();
        System.out.println(LINE);
        System.out.println(ROBOT_ICON + ": " + "You have added this following task to the list:");
        System.out.println(newTask.toString());
        printCurrentNumberOfTask();
        System.out.println(LINE);
    }

    /**
     * Prints all the tasks in the list.
     */
    private static void printList() {
        System.out.println(LINE);
        System.out.println(ROBOT_ICON + ": " + "Your list contains the following task/s:");
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + list.get(i).toString());
        }
        System.out.println(LINE);
    }

    /**
     * Deletes the task from list.
     *
     * @param task task
     */
    private static void deleteTask(Task task) {
        list.remove(task);
        saveData();
    }

    /**
     * Marks the task as complete and prints out the task, throws error if done command is not properly formatted.
     *
     * @param userInput the index of the task in list
     */
    private static void completeAndPrintTask(String userInput) {
        String[] tokens = userInput.split(" ");
        int taskNumber;
        boolean isTaskDoneCommand = tokens.length == 2 && tokens[0].equals("done");
        try {
            try {
                taskNumber = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException err) {
                throw new InvalidIndexException("done");
            }
            if (!isTaskDoneCommand || taskNumber > list.size()) {
                throw new InvalidIndexException("done");
            }
        } catch (InvalidIndexException err) {
            printErrorMessage(err);
            return;
        }

        System.out.println(LINE);
        System.out.println(ROBOT_ICON + ": " + "You have completed the following task:");
        list.get(taskNumber - 1).completeTask();
        saveData();
        System.out.println(list.get(taskNumber - 1).toString());
        System.out.println(LINE);
    }

    /**
     * Delete the task and prints out the task and current number of tasks,
     * throws error if done command is not properly formatted.
     *
     * @param userInput the index of the task in list
     */
    private static void deleteAndPrintTask(String userInput) {
        String[] tokens = userInput.split(" ");
        int taskNumber;
        boolean isTaskDeleteCommand = tokens.length == 2 && tokens[0].equals("delete");
        try {
            try {
                taskNumber = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException err) {
                throw new InvalidIndexException("delete");
            }

            if (!isTaskDeleteCommand || taskNumber > list.size()) {
                throw new InvalidIndexException("delete");
            }
        } catch (InvalidIndexException err) {
            printErrorMessage(err);
            return;
        }

        System.out.println(LINE);
        System.out.println(ROBOT_ICON + ": " + "You have removed the following task:");
        System.out.println(list.get(taskNumber - 1).toString());
        deleteTask(list.get(taskNumber - 1));
        System.out.println(ROBOT_ICON + ": " + "You have " + list.size() + " task/s left.");
        System.out.println(LINE);
    }

    /**
     * Returns Duke's memory file in data/Duke.txt else create and return a new file in the directory.
     *
     * @return memory file for Duke
     */
    private static File loadData() {
        File directory = new File("data");
        directory.mkdir();
        File storageFile = new File("data/duke.txt");
        try {
            storageFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Error has occurred.");
        }
        return storageFile;
    }

    /**
     * Reads from storage file all the previously saved tasks info and add all the tasks to the current tracking list.
     *
     * @param storageFile stores all the previously saved tasks info.
     */
    private static void readFile(File storageFile) {
        try {
            Scanner s = new Scanner(storageFile);
            while (s.hasNext()) {
                String line = s.nextLine();
                char taskType = line.charAt(0);
                int taskStatus = line.charAt(4);
                int startOfTaskDes = 8;
                int taskDescIdentifier  = line.indexOf('|', startOfTaskDes);

                String taskDesc = line.substring(startOfTaskDes, taskDescIdentifier - 1);
                if (taskType == 'E') {
                    String date = line.substring(taskDescIdentifier + 2);
                    Event task = new Event(taskDesc, date);
                    if (taskStatus == 1) {
                        task.completeTask();
                    }
                    addTask(task);

                } else if (taskType == 'D') {
                    String time = line.substring(taskDescIdentifier + 2);
                    Deadline task = new Deadline(taskDesc, time);
                    if (taskStatus == 1) {
                        task.completeTask();
                    }
                    addTask(new Deadline(taskDesc, time));
                } else {
                    addTask(new ToDo(taskDesc));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)  {
        greet();
        File storageFile = loadData();
        readFile(storageFile);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String userInput = scanner.nextLine();
            String[] tokens = userInput.split(" ");
            if (userInput.equals(BYE)) {
                bye();
                break;
            } else if (userInput.equals("list")) {
                printList();
            } else {
                otherCommands(userInput);
            }
        }
    }
}

