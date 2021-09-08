package duke;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import duke.task.Deadline;
import duke.task.Event;
import duke.task.ToDo;


/**
 * Storage saves tasks added by user and save it in a file as indicated by filepath.
 */
public class Storage {
    private static final int startOfTaskDes = 8;
    private static final int taskTypeIndex = 0;
    private static final int taskStatusIndex = 4;
    private final String filePath;

    /**
     * Constructs an instance of Storage which saves data for Duke.
     *
     * @param filePath directory of saved memory for Duke.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }


    /**
     * Creates new file at filePath if the file does not exist.
     *
     * @return File to write
     */
    private File initStorageFile() {
        String dirName = filePath.split("/duke.txt")[0];
        File directory = new File(dirName);
        directory.mkdir();
        File storageFile = new File(filePath);
        try {
            storageFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Error has occurred.");
        }
        return storageFile;
    }

    /**
     * Reads from storage file all the previously saved tasks info and add all the tasks to the current tracking list.
     */
    public TaskList loadTasks() {
        TaskList tasks = new TaskList();
        try {
            Scanner s = new Scanner(initStorageFile());
            while (s.hasNext()) {
                String line = s.nextLine();
                char taskType = line.charAt(taskTypeIndex);
                char taskStatus = line.charAt(taskStatusIndex);
                int taskDescIdentifier = line.indexOf('|', startOfTaskDes);

                String taskDesc = line.substring(startOfTaskDes, taskDescIdentifier - 1);
                if (taskType == 'E') {
                    String date = line.substring(taskDescIdentifier + 2);
                    Event task = new Event(taskDesc, date);
                    if (taskStatus == '1') {
                        task.completeTask();
                    }
                    tasks.addTask(task);

                } else if (taskType == 'D') {
                    String time = line.substring(taskDescIdentifier + 2);
                    Deadline task = new Deadline(taskDesc, time);
                    if (taskStatus == '1') {
                        task.completeTask();
                    }
                    tasks.addTask(task);
                } else if (taskType == 'T') {
                    tasks.addTask(new ToDo(taskDesc));
                }
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * Overwrites and saves new task list in data/duke.txt file when there is changes such as deleting and adding task.
     */
    public void saveData(List<String> encodedTasks) {
        try {
            FileWriter myWriter = new FileWriter(filePath);
            for (String encodedTask : encodedTasks) {
                myWriter.write(encodedTask + '\n');
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
