package ui;

import java.io.FileNotFoundException;

//code based on CPSC 210 Json Serialization Starter
public class Main {
    public static void main(String[] args) {
        try {
            //new PollApp(); //terminal user interface
            new GuiPollApp(); // graphical user interface
        } catch (FileNotFoundException e) {
            System.out.println("Unable to run application: file not found");
        }
    }
}
