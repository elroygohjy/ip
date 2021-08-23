package Duke;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {
    @Test
    void writeTest() {
        Storage storage = new Storage("data/Duke.txt");
        ArrayList<String> input = new ArrayList<>();
        input.add("hello world");
        storage.saveData(input);
        File file = new File("data/Duke.txt");
        try {
            Scanner s = new Scanner(file);
            assertEquals("hello world", s.nextLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}