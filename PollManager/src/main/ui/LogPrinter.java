package ui;

import model.Event;
import model.EventLog;

public class LogPrinter {

    public LogPrinter(){ 
    }

    public void printLog(EventLog el) {
        System.out.println("Event Log:");
        for (Event e : el) {
            System.out.println(e.toString());
        }
        System.out.println("End Of Event Log");
    }
}
