package Duke;

import Duke.Task.Deadline;
import Duke.Task.Event;
import Duke.Task.ToDo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
public class Storage {

    private final String filePath;

    private File initStorageFile() {
        File directory = new File("data");
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
                char taskType = line.charAt(0);
                char taskStatus = line.charAt(4);
                int startOfTaskDes = 8;
                int taskDescIdentifier  = line.indexOf('|', startOfTaskDes);

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
                } else {
                    tasks.addTask(new ToDo(taskDesc));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * Overwrites and save new task list in data/duke.txt file when there is changes such as deleting and adding task.
     */
    public void saveData(List<String> encodedTasks) {
        try {
            FileWriter myWriter = new FileWriter(filePath);
            for (String encodedTask: encodedTasks) {
                myWriter.write(encodedTask + '\n');
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Storage(String filePath) {
        this.filePath = filePath;
    }
}