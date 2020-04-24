package com.company;


import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("database.txt");
        file.createNewFile();

        Database db = new Database(file);
		
        Thread phoneReader = new Thread(new PhonesReader(db), "Phone Reader");
        Thread nameReader = new Thread(new NamesReader(db), "Name Reader");
        Thread writer = new Thread(new Writer(db), "Writer");
        phoneReader.start();
        nameReader.start();
        writer.start();
    }
}

