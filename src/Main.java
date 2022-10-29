package src;

import src.storage_systems.FileOutputStreamStorage;
import src.storage_systems.SQLiteStorageSystem;

public class Main {
    public static void main(String[] args) {
        new SQLiteStorageSystem("SQLiteStorageSystem", 100, 50);
        new FileOutputStreamStorage(50, 100);
    }
}
