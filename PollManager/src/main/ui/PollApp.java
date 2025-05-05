package ui;

import model.Poll;
import model.Question;
import model.Response;
import model.Responder;

import persistence.JsonReader;
import persistence.JsonWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// The poll app
// code based on: CPSC 210 TellerApp, CPSC 210 Lab 4 FlashcardReviewer, and CPSC 210 Json Serialization Starter
public class PollApp {
    private static final String JSON_STORE = "./data/SavedPoll.json";
    private Poll currentPoll;
    private Scanner input;
    private boolean keepGoing;
    private int currentQuestionIndex;
    private int currentResponderIndex;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    // EFFECTS: runs the PollApp
    public PollApp() throws FileNotFoundException {
        currentPoll = new Poll("My Poll");
        this.currentQuestionIndex = 0;
        this.currentResponderIndex = 0;
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        runPollApp();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runPollApp() {
        keepGoing = true;
        while (keepGoing) {
            init();
            handleMenu();
        }
    }

    private void handleMenu() {
        String command = "";
        displayMainMenu();
        command = input.nextLine();
        processCommand(command);
    }

    // EFFECTS: displays menu of options to choose
    private void displayMainMenu() {
        System.out.println("Welcome to " + currentPoll.getPollName() + " Manager");
        System.out.println("1. Take A Poll");
        System.out.println("2. Manage Poll");
        System.out.println("3. View And Manage Poll Answer Statistics");
        System.out.println("4. Manage Poll Responders");
        System.out.println("5. Change Poll Name");
        System.out.println("6 Save Current Poll");
        System.out.println("7 Load Saved Poll");
        System.out.println("8. Exit Out");
        System.out.println("Enter Choice:         ");
    }

    // MODIFIES: this
    // EFFECTS: processes user's input
    @SuppressWarnings("methodlength")
    private void processCommand(String command) {
        switch (command) {
            case "1":
                takePoll();
                break;
            case "2":
                managePoll();
                break;
            case "3":
                manageStats();
                break;
            case "4":
                manageResponders();
                break;
            case "5":
                changeName();
                break;
            case "6":
                savePoll();
                break;
            case "7":
                loadPoll();
                break;
            case "8":
                confirmExit();
                break;
            default:
                System.out.println("Invalid Response");
        }
    }

    // MODIFIES: this
    // EFFECTS: loads Poll from SavedPoll.json and sets it as the currentPoll
    private void loadPoll() {
        try {
            currentPoll = jsonReader.readPoll();
            System.out.println("Loaded " + currentPoll.getPollName() + " from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: Writes the currentPoll as a json file in SavedPoll.json
    private void savePoll() {
        try {
            jsonWriter.open();
            jsonWriter.write(currentPoll);
            jsonWriter.close();
            System.out.println("Saved " + currentPoll.getPollName() + " to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    private void init() {
        input = new Scanner(System.in);
        input.useDelimiter("\r?\n|\r");
    }

    // MODIFIES: this
    // EFFECTS: user selects what responder is answering(if no responders exists
    // they make one)
    // and answers the poll questions logging each response
    // if the Poll does not have at least 1 question and each question does not have
    // at least 2 possible Answers user is sent to Main Menu instead
    private void takePoll() {
        boolean skip = false;
        if (!currentPoll.isReadyPoll()) {
            System.out.println("Poll Must Have At Least 1 Question");
            System.out.println("And Each Question Must Have At Least 2 Possible Answers");
        } else {
            if (currentPoll.getPollResponders().size() == 0) {
                makeFirstResponder();
                currentQuestionIndex = 0;
                if (currentPoll.getPollResponders().size() > 0) {
                    beginPoll();
                    System.out.println("Thank You For Taking The Poll!");
                }
                skip = true;
            }
            if (currentPoll.getPollResponders().size() > 0 && !skip) {
                currentQuestionIndex = 0;
                selectResponders();
                beginPoll();
                System.out.println("Thank You For Taking The Poll!");
            }
        }
        currentQuestionIndex = 0;
        currentResponderIndex = 0;
    }

    // MODIFIES: this
    // EFFECTS: user selects which responder they "are" or user makes a new one
    @SuppressWarnings("methodlength")
    private void selectResponders() {
        boolean keepGoing = true;
        displayResponders();
        while (keepGoing) {
            int validMax = currentPoll.getPollResponders().size();
            System.out.println("Pick Responder # From List Or Enter 0 To Add A New Responder");
            String command = null;
            boolean proceed = true;
            command = input.next();
            int intCommand = -1;
            try {
                Integer.parseInt(command);
                intCommand = Integer.parseInt(command);
                proceed = (0 <= intCommand && intCommand <= validMax);
            } catch (Exception numberFormatException) {
                proceed = false;
            }
            if (proceed) {
                if (intCommand == 0) {
                    makeResponder();
                    currentResponderIndex = validMax;
                    break;
                } else {
                    currentResponderIndex = intCommand - 1;
                    break;
                }
            } else {
                System.out.println("Invalid Response");
            }
        }
    }

    // EFFECTS: displays all the responders so user can select one
    private void displayResponders() {
        int count = 1;
        System.out.println();
        System.out.println("Responders:");
        for (Responder responder : currentPoll.getPollResponders()) {
            System.out.println(count + ". " + responder.getFirstName() + " " + responder.getLastName());
            count++;
        }
    }

    // MODIFIES: this
    // EFFECTS: user takes the poll individualy through each question selecting
    // answers
    // the answers are recorded in the current Responder as Responses
    private void beginPoll() {
        String command = "";
        int validMax = currentPoll.getPollQuestions().size();
        while (currentQuestionIndex <= validMax - 1) {
            Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);
            displayPollQuestion(currentQuestion);
            command = input.next();
            processPollResponse(currentQuestion, command);
        }
    }

    // EFFECTS: Displays the current poll question and its choosable answers
    private void displayPollQuestion(Question currentQuestion) {
        Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
        System.out.println(currentResponder.getFirstName() + " " + currentResponder.getLastName()
                + getPossesion(currentResponder.getLastName()) + " Poll Instance: ");
        System.out.println("Question Number: " + (currentQuestionIndex + 1));
        System.out.println(currentQuestion.getPollQuestion() + getQuestionMark(currentQuestion.getPollQuestion()));
        System.out.println("Options:");
        displayQuestionAnswers(currentQuestion);
    }

    // EFFECTS: If pollQuestion ends in "?" returns "", otherwisse "?"
    private String getQuestionMark(String pollQuestion) {
        int length = pollQuestion.length();
        if (pollQuestion.substring(length - 1, length).equals("?")) {
            return "";
        }
        return "?";
    }

    // EFFECTS: If name ends in s returns "'", or if name is 0 characters long
    // return "", otherwise "'s"
    private String getPossesion(String lastName) {
        int length = lastName.length();
        if (length == 0) {
            return "";
        }
        if (lastName.substring(length - 1, length).equals("s")) {
            return "'";
        }
        return "'s";
    }

    // MODIFIES: this
    // EFFECTS: if command is valid index for the poll question's choosable answers,
    // a response is created and assigned to the Responder
    private void processPollResponse(Question currentQuestion, String command) {
        boolean proceed = true;
        int intCommand = -1;
        int validMax = currentQuestion.getPollAnswers().size();
        try {
            Integer.parseInt(command);
            intCommand = Integer.parseInt(command);
            proceed = (0 <= intCommand && intCommand <= validMax);
        } catch (Exception numberFormatException) {
            proceed = false;
        }
        if (proceed) {
            String answer = currentQuestion.getPollAnswers().get(intCommand - 1);
            currentPoll.responderAnswered(currentResponderIndex, currentQuestion, answer);
            currentQuestionIndex++;
        } else {
            System.out.println("Invalid Response");
        }
    }

    // MODIFIES: this
    // EFFECTS: displays menu where user can: change poll name, add/edit/remove poll
    // questions
    private void managePoll() {
        if (currentPoll.getPollQuestions().size() == 0) {
            makeFirstQuestion();
        }
        if (currentPoll.getPollQuestions().size() > 0) {
            pollMenu();
        }

    }

    // MODIFIES: this
    // EFFECTS: user prompted to create first poll question or return to main menu
    private void makeFirstQuestion() {
        String command = "";
        while (!command.equals("2") && currentPoll.getPollQuestions().isEmpty()) {
            System.out.println("It Looks Like You Don't Have Any Questions Yet!");
            System.out.println("1. Make First Poll Question");
            System.out.println("2. Return To Main Menu");
            command = input.next();
            handleFirstQuestionCommand(command);
        }
    }

    private void handleFirstQuestionCommand(String command) {
        switch (command) {
            case "1":
                makeQuestion();
                break;
            case "2":
                break;
            default:
                System.out.println("Invalid response");
        }
    }

    // MODIFIES: this
    // EFFECTS: user is prompted to enter a new poll question question prompt and
    // create a new poll question
    // loops if question prompt is already being used by another question
    private void makeQuestion() {
        boolean keepGoing = true;
        while (keepGoing) {
            String command = null;
            System.out.println("Enter New Question Prompt: ");
            command = input.next();
            if (command.length() == 0) {
                System.out.println("Question Prompt Must Be Minimum 1 Characters Long");
                System.out.println();
            } else if (currentPoll.isNoDuplicate(command)) {
                Question newQuestion = new Question(command);
                currentPoll.addQuestion(newQuestion);
                System.out.println("Question Added");
                System.out.println();
                keepGoing = false;
            } else if (!currentPoll.isNoDuplicate(command)) {
                System.out.println("Question Prompt Must Be Unique");
                System.out.println();
            }
        }
    }

    // EFFECTS: displays the current Poll Question and gives options
    // to: remove/add/edit poll questions,
    // go to next display of poll question in the list(cannot go back in list if
    // this is first question in list and cannot go forward if last question)
    // if all questions are deleted managePoll() is called to create a new first
    // question or exit to main menu
    private void pollMenu() {
        String command = "";
        while (!command.equals("8") && currentPoll.getPollQuestions().size() > 0) {
            Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);
            displayPollMenu(currentQuestion);

            command = input.next();
            processPollCommand(command);
        }
        currentQuestionIndex = 0;
        if (currentPoll.getPollQuestions().size() == 0) {
            managePoll();
        }
    }

    private void displayPollMenu(Question chosenQuestion) {
        System.out.println("Current Question# " + (currentQuestionIndex + 1));
        System.out.println("Prompt: " + chosenQuestion.getPollQuestion());
        System.out.println("Possible Answers: ");
        displayQuestionAnswers(chosenQuestion);
        System.out.println();
        // print options
        System.out.println("Options:");
        if (!(currentQuestionIndex == 0)) {
            System.out.println("1. Go To Previous Question");
        }
        if (!(currentQuestionIndex >= currentPoll.getPollQuestions().size() - 1)) {
            System.out.println("2. Go To Next Question");
        }
        System.out.println("3. Change Current Question Prompt");
        System.out.println("4. Add New Answer");
        System.out.println("5. Remove Current Answer");
        System.out.println("6. Add New Question");
        System.out.println("7. Remove Current Question");
        System.out.println("8. Return To Main Menu");
        System.out.println("Enter Choice: ");
    }

    private void displayQuestionAnswers(Question currentQuestion) {
        int count = 1;
        for (String answer : currentQuestion.getPollAnswers()) {
            System.out.println(count + ". " + answer);
            count++;
        }
    }

    // EFFECTS: processes user input in manage poll instance
    @SuppressWarnings("methodlength")
    private void processPollCommand(String command) {
        switch (command) {
            case "3":
                changeQuestionPrompt();
                break;
            case "4":
                addAnswer();
                break;
            case "5":
                if (currentPoll.getPollQuestions().get(currentQuestionIndex).getPollAnswers().size() != 0) {
                    removeAnswer();
                } else {
                    System.out.println("Question Has No Answers");
                }
                break;
            case "6":
                makeQuestion();
                currentQuestionIndex = currentPoll.getPollQuestions().size() - 1;
                break;
            case "7":
                removeQuestion();
                break;
            case "8":
                break;
            case "1":
                if (!(currentQuestionIndex == 0)) { // if false default
                    currentQuestionIndex--;
                    break;
                }
            case "2":
                if (!(currentQuestionIndex >= currentPoll.getPollQuestions().size() - 1)) { // if false default
                    currentQuestionIndex++;
                    break;
                }
            default:
                System.out.println("Invalid Response");
                System.out.println();
        }
    }

    // MODIFIES: this
    // EFFECTS: changes question prompt of question
    // if the new question prompt is already being used by another question or is
    // less than 1 characters long the prompt is not changed
    private void changeQuestionPrompt() {
        Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);
        boolean keepGoing = true;
        while (keepGoing) {
            String command = null;
            System.out.println("Enter New Question Prompt For Question# " + (currentQuestionIndex + 1) + ": ");
            command = input.next();
            if (command.equals(currentQuestion.getPollQuestion())) {
                System.out.println("Question Was Unchanged");
                System.out.println();
                keepGoing = false;
            } else if (command.length() == 0) {
                System.out.println("Question Prompt Must Be Minimum 1 Characters Long");
                System.out.println();
            } else if (currentPoll.isNoDuplicate(command)) {
                currentQuestion.setQuestion(command);
                System.out.println("Question Updated");
                System.out.println();
                keepGoing = false;
            } else if (!currentPoll.isNoDuplicate(command)) {
                System.out.println("Question Prompt Must Be Unique");
                System.out.println();
            }
        }

    }

    // MODIFIES: this
    // EFFECTS: removes current question from the poll and any responses containg
    // that question from poll responders
    private void removeQuestion() {
        boolean keepGoing = true;
        while (keepGoing) {
            String command = null;
            System.out.println("Enter \"C\" To Delete Question: ");
            command = input.next().toLowerCase();
            if (command.equals("c")) {
                System.out.println("Question Was Removed");
                System.out.println();
                currentPoll.removeQuestion(currentQuestionIndex);
                if (currentQuestionIndex == currentPoll.getPollQuestions().size()) {
                    currentQuestionIndex--;
                }
                break;
            }
            System.out.println("Deletion Canceled");
            break;
        }
        keepGoing = false;
    }

    // MODIFIES: this
    // EFFECTS: removes chosen answer from current question, answer is not removed
    // if user fails to confirm or chooses an invalid index
    @SuppressWarnings("methodlength")
    private void removeAnswer() {
        boolean keepGoing = true;
        while (keepGoing) {
            Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);
            int validMax = currentPoll.getPollQuestions().get(currentQuestionIndex).getPollAnswers().size();
            String command = null;
            boolean proceed = true;
            System.out.println("Enter Answer Number You Want To Delete: ");
            command = input.next();
            int intCommand = -1;
            try {
                Integer.parseInt(command);
                intCommand = Integer.parseInt(command);
                proceed = (1 <= intCommand && intCommand <= validMax);
            } catch (Exception numberFormatException) {
                proceed = false;
            }
            if (proceed) {
                System.out.println("Enter \"C\" To Delete Answer: ");
                command = input.next().toLowerCase();
                if (command.equals("c")) {
                    System.out.println("Answer Was Removed");
                    System.out.println();
                    currentQuestion.removeAnswer(intCommand - 1);
                    keepGoing = false;
                }
                System.out.println("Deletion Cancelled");
                keepGoing = false;
            }
            if (!proceed) {
                System.out.println("Invalid Input");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: adds new answer to current question, if answer like it already
    // exists it is not added
    private void addAnswer() {
        Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);
        boolean keepGoing = true;
        while (keepGoing) {
            String command = null;
            System.out.println("Enter New Question Answer: ");
            command = input.next();

            if (!currentQuestion.getPollAnswers().contains(command)) {
                currentQuestion.addAnswer(command);
                System.out.println("Answer Added");
                System.out.println();
                keepGoing = false;
            }
            System.out.println("Question Answer Must Be Unique");
            System.out.println();
        }
    }

    // MODIFIES: this
    // EFFECTS: displays menu where user can view its statistics(% of Responders
    // chose each answer),
    // user is able to filter Responders based on
    // attributes(age,occupation,income,etc)
    private void manageStats() {
        if (currentPoll.getPollQuestions().size() <= 0) {
            makeFirstQuestion();
        }
        if (currentPoll.getPollQuestions().size() > 0) {
            runStats();
            currentPoll.clearFilteredResponders();
            currentQuestionIndex = 0;
        }
    }

    // MODIFIES: this
    // EFFECTS: displays menu where user can view Poll statistsics on a question by
    // question basis
    // and can apply filters
    private void runStats() {
        String command = "";
        while (!command.equals("8")) {
            Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);
            List<Responder> beforeList = currentPoll.getFilteredResponders();
            displayStatsMenu(currentQuestion);
            command = input.next();
            processStatsCommand(command);
            int after = currentPoll.getFilteredResponders().size();
            if (after == 0 && !(command.equals("7") || command.equals("1") || (command.equals("2")))) {
                currentPoll.setFilteredPollResponders(beforeList);
                System.out.println("No Results Found, Filter Not Applied");
            }
        }
    }

    // EFFECTS: displays stats of current question and options to filter/exit/change
    // questions
    private void displayStatsMenu(Question currentQuestion) {
        System.out.println("Question# " + (currentQuestionIndex + 1));
        System.out.println(currentQuestion.getPollQuestion() + getQuestionMark(currentQuestion.getPollQuestion()));
        List<String> filteredAnswers = currentPoll.extractResponderAnswers(currentQuestionIndex);
        if (filteredAnswers.isEmpty()) {
            System.out.println();
            System.out.println("No Stats For This Question");
        } else {
            System.out.println("Response Stats:");
            int count = 1;
            for (String answer : currentQuestion.getPollAnswers()) {
                System.out
                        .println(count + ". " + answer + " Frequency: " + ((int) getFrequency(answer, filteredAnswers))
                                + "%");
                count++;
            }
        }
        printStatOptions();
    }

    // EFFECTS: prints stats menu options
    // option 1 only shows up if currentQuestion index is 0
    // option 2 only shows up if current question index is at max of poll questions
    private void printStatOptions() {
        System.out.println("Options:");
        if (!(currentQuestionIndex == 0)) {
            System.out.println("1. Go To Previous Question"); // option 1
        }
        if (!(currentQuestionIndex >= currentPoll.getPollQuestions().size() - 1)) {
            System.out.println("2. Go To Next Question"); // option 2
        }
        System.out.println("3. Filter By Income");
        System.out.println("4. Filter By Age");
        System.out.println("5. Filter By Gender Id");
        System.out.println("6. Filter By Occupations");
        System.out.println("7. Reset Filters");
        System.out.println("8. Return To Main Menu");
        System.out.println("Enter Choice: ");
    }

    // HELPER for displayStatsMenu()
    // EFFECTS: returns number of instances of answer in filteredAnswers
    private double getFrequency(String answer, List<String> filteredAnswers) {
        double result = 0.0;
        for (String listAnswer : filteredAnswers) {
            if (listAnswer.equals(answer)) {
                result++;
            }
        }
        return ((result / filteredAnswers.size()) * 100);
    }

    // EFFECTS: processes user input from stat menu
    @SuppressWarnings("methodlength")
    private void processStatsCommand(String command) {
        switch (command) {
            case "3":
                filterByIncome();
                break;
            case "4":
                filterByAge();
                break;
            case "5":
                filterByGenderId();
                break;
            case "6":
                filterByOccupation();
                break;
            case "7":
                currentPoll.clearFilteredResponders();
                System.out.println("Cleared Filters");
                break;
            case "8":
                break;
            case "1":
                if (!(currentQuestionIndex == 0)) { // if false default
                    currentQuestionIndex--;
                    currentPoll.clearFilteredResponders();
                    break;
                }
            case "2":
                if (!(currentQuestionIndex >= currentPoll.getPollQuestions().size() - 1)) { // if false default
                    currentQuestionIndex++;
                    currentPoll.clearFilteredResponders();
                    break;
                }
            default:
                System.out.println("Invalid Response");
                System.out.println();
        }
    }

    // MODIFIES: this
    // EFFECTS: user inputs upper and lower threshold of income and responders are
    // then filtered keeping all those within threshold
    private void filterByIncome() {
        System.out.println("Preparing Income Filter");
        int lower = getLower();
        int higher = getHigher(lower);
        currentPoll.respondersWithinIncomeInterval(lower, higher);
        System.out.println("Income Filter Applied");
    }

    // MODIFIES: this
    // EFFECTS: user inputs upper and lower threshold of age and responders are then
    // filtered keeping all those within threshold
    private void filterByAge() {
        System.out.println("Preparing Age Filter");
        int lower = getLower();
        int higher = getHigher(lower);
        currentPoll.respondersWithinAgeInterval(lower, higher);
        System.out.println("Age Filter Applied");
    }

    // EFFECTS: user inputs index treshold for filter treshold
    private int getLower() {
        boolean keepGoing = true;
        int intCommand = -1;
        while (keepGoing) {
            String command = null;
            boolean proceed = true;
            System.out.println("Enter Lower Index:");
            command = input.next();
            try {
                Integer.parseInt(command);
                intCommand = Integer.parseInt(command);
            } catch (Exception numberFormatException) {
                proceed = false;
            }
            if (intCommand < 0) {
                proceed = false;
            }
            if (proceed) {
                keepGoing = false;
            } else {
                System.out.println("Invalid Input");
            }
        }
        return intCommand;
    }

    // EFFECTS: user inputs index treshold for filter treshold, cannot enter input
    // that is less than lower
    private int getHigher(int lower) {
        boolean keepGoing = true;
        int intCommand = -1;
        while (keepGoing) {
            String command = null;
            boolean proceed = true;
            System.out.println("Enter Higher Index:");
            command = input.next();
            try {
                Integer.parseInt(command);
                intCommand = Integer.parseInt(command);
            } catch (Exception numberFormatException) {
                proceed = false;
            }
            if (intCommand < lower) {
                proceed = false;
            }
            if (proceed) {
                keepGoing = false;
            } else {
                System.out.println("Invalid Input");
            }
        }
        return intCommand;
    }

    // MODIFIES: this
    // EFFECTS: user inputs gender id and responders are filtered keeping only
    // responders with chosen gender
    private void filterByGenderId() {
        boolean keepGoing = true;
        int output = 0;
        while (keepGoing) {
            String command = null;
            System.out.println(
                    "Enter \"Man\",\"Woman\" or \"Other\" As Gender Of To Keep In Filtering :");
            command = input.next().toLowerCase();
            System.out.println(command);
            if (command.equals("man")) {
                output = 1;
                keepGoing = false;
            } else if (command.equals("woman")) {
                output = 2;
                keepGoing = false;
            } else if (command.equals("other")) {
                output = 3;
                keepGoing = false;
            } else {
                System.out.println("Invalid Input");
            }
        }
        currentPoll.respondersOfGenders(output);
        System.out.println("Filtered By Gender");
    }

    // MODIFIES: this
    // EFFECTS: user inputs occupations and responders are filteed keeping only
    // those whose occupation is contained in occupation list
    private void filterByOccupation() {
        boolean keepGoing = true;
        String command = null;
        List<String> occupationList = new ArrayList<>();
        while (keepGoing) {
            System.out.println("Enter An Occupation Or 0 To Apply Filter:");
            command = input.next();
            if (command.equals("0")) {
                if (occupationList.size() == 0) {
                    System.out.println("Must Have At Least 1 Occupation To Filter");
                } else {
                    currentPoll.respondersOfOccupations(occupationList);
                    break;
                }
            } else {
                occupationList.add(command);
                System.out.println("Added " + command + " Occupation To Filter List");
            }
        }
        System.out.println("Filtered By Occupations");
    }

    // MODIFIES: this
    // EFFECTS: displays menu where user can view poll responders and
    // add/edit/remove them
    private void manageResponders() {
        if (currentPoll.getPollResponders().size() == 0) {
            makeFirstResponder();
        }

        if (currentPoll.getPollResponders().size() > 0) {
            respondersMenu();
        }
    }

    // MODIFIES: this
    // EFFECTS: Presents menu where user can choose to make the first responder or
    // exit to main menu
    private void makeFirstResponder() {
        String command = "";
        while (!command.equals("2") && currentPoll.getPollResponders().isEmpty()) {
            System.out.println("It Looks Like You Don't Have Any Responders Yet!");
            System.out.println("1. Add First Poll Responder");
            System.out.println("2. Return To Main Menu");
            command = input.next();
            handleFirstResponderCommand(command);
        }
    }

    // EFFECTS: handles user input in first responder menu
    private void handleFirstResponderCommand(String command) {
        switch (command) {
            case "1":
                makeResponder();
                break;
            case "2":
                break;
            default:
                System.out.println("Invalid response");
        }
    }

    // MODIFIES: this
    // EFFECTS: user enters variables for new responder, new responder is then added
    // to Responder list
    private void makeResponder() {
        System.out.println("Enter New Responder First Name: ");
        String newFirst = input.next();
        System.out.println("Enter New Responder Last Name: ");
        String newLast = input.next();
        long newNumber = enterValidPhoneNumber();
        String newEmail = enterValidEmail();
        int newAge = enterValidAge();
        int newGenderId = enterGender();
        int newIncome = enterValidIncome();
        System.out.println("Enter New Responder Occupation: ");
        String newOccupation = input.next();
        Responder newResponder = new Responder(newFirst, newLast, newNumber, newEmail, newAge, newGenderId,
                newIncome, newOccupation);
        currentPoll.addResponder(newResponder);
    }

    // EFFECTS: User sees menu where they enter a valid phonenumber for new
    // Responder
    private long enterValidPhoneNumber() {
        boolean keepGoing = true;
        long longCommand = -1;
        while (keepGoing) {
            String command = null;
            boolean proceed = true;
            System.out.println("Enter New Responder Phone Number, Input It As XXXXXXXXXXX With Nothing In Between: ");
            command = input.next();
            try {
                Long.parseLong(command);
                longCommand = Long.parseLong(command);
            } catch (Exception numberFormatException) {
                proceed = false;
            }

            if (String.valueOf(longCommand).length() != 10) {
                proceed = false;
            }
            if (proceed) {
                keepGoing = false;
            } else {
                System.out.println("Invalid Input");
            }
        }
        return longCommand;
    }

    // EFFECTS: User sees menu where they enter a valid Email for new Responder
    private String enterValidEmail() {
        boolean keepGoing = true;
        String command = null;
        while (keepGoing) {
            System.out.println("Enter A Valid Email Ending in .com or .ca: ");
            command = input.next();
            int length = command.length();
            if (command.contains("@") && (command.substring(length - 4, length).equals(".com")
                    || command.substring(length - 3, length).equals(".ca"))) {
                keepGoing = false;
            } else {
                System.out.println("Invalid Input");
            }
        }
        return command;
    }

    // EFFECTS: User sees menu where they enter a valid age for new Responder
    private int enterValidAge() {
        boolean keepGoing = true;
        int intCommand = -1;
        while (keepGoing) {
            String command = null;
            boolean proceed = true;
            System.out.println("Enter New Responder Age :");
            command = input.next();
            try {
                Integer.parseInt(command);
                intCommand = Integer.parseInt(command);
            } catch (Exception numberFormatException) {
                proceed = false;
            }
            if (intCommand < 0) {
                proceed = false;
            }
            if (proceed) {
                keepGoing = false;
            } else {
                System.out.println("Invalid Input");
            }
        }
        return intCommand;
    }

    // EFFECTS: User sees menu where they select gender of new Responder
    private int enterGender() {
        boolean keepGoing = true;
        int output = 0;
        while (keepGoing) {
            String command = null;
            System.out.println(
                    "Enter \"Man\",\"Woman\" or \"Other\" As Gender Of New Responder :");
            command = input.next().toLowerCase();
            if (command.equals("man")) {
                output = 1;
                keepGoing = false;
            } else if (command.equals("woman")) {
                output = 2;
                keepGoing = false;
            } else if (command.equals("other")) {
                output = 3;
                keepGoing = false;
            } else {
                System.out.println("Invalid Input");
            }
        }
        return output;
    }

    // EFFECTS: User sees menu where they enter a valid income
    private int enterValidIncome() {
        boolean keepGoing = true;
        int intCommand = -1;
        while (keepGoing) {
            String command = null;
            boolean proceed = true;
            System.out.println("Enter New Responder Income : ");
            command = input.next();
            try {
                Integer.parseInt(command);
                intCommand = Integer.parseInt(command);
            } catch (Exception numberFormatException) {
                proceed = false;
            }
            if (intCommand < 0) {
                proceed = false;
            }
            if (proceed) {
                keepGoing = false;
            } else {
                System.out.println("Invalid Input");
            }
        }
        return intCommand;
    }

    // MODIFIES: this
    // EFFECTS: Presents menu where user can view and add/edit/delete Responders one
    // at a time,
    // if user deletes all responders they are prompted to make one or exit to main
    // menu
    private void respondersMenu() {
        String command = "";
        while (!command.equals("7") && currentPoll.getPollResponders().size() > 0) {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            displayResponderMenu(currentResponder);

            command = input.next();
            processResponderCommand(command);
        }
        currentResponderIndex = 0;
        if (currentPoll.getPollResponders().size() == 0) {
            manageResponders();
        }
    }

    // EFFECTS: displays current responder and input options
    private void displayResponderMenu(Responder currentResponder) {
        // displays current responder number in list
        System.out.println("Current Responder# " + (currentResponderIndex + 1));
        // displays current responder First and Last name
        System.out.println("Name: " + currentResponder.getFirstName() + " " + currentResponder.getLastName());
        // displays current responder Age and Gender
        System.out.println("Age: " + currentResponder.getAge() + " ,Gender: " + currentResponder.getGender());
        // displays current responder Occupation and Income
        System.out.println(
                "Occupation: " + currentResponder.getOccupation() + " ,Income: " + currentResponder.getIncome());
        // displays current responder Phone# and Email
        System.out.println(
                "Email: " + currentResponder.getEmail() + " ,Phone Number: " + currentResponder.getPhoneNumber());
        // displays current responder's responses
        System.out.println("Responses: ");
        displayResponses(currentResponder);
        System.out.println();
        // print options
        System.out.println("Options:");
        if (!(currentResponderIndex == 0)) {
            System.out.println("1. Go To Previous Responder");
        }
        if (!(currentResponderIndex >= currentPoll.getPollResponders().size() - 1)) {
            System.out.println("2. Go To Next Responder");
        }
        System.out.println("3. Edit Responder Info");
        System.out.println("4. Remove A Response");
        System.out.println("5. Add New Responder");
        System.out.println("6. Remove Current Responder");
        System.out.println("7. Return To Main Menu");
        System.out.println("Enter Choice: ");
    }

    // EFFECTS: displays the responses of the Responder
    private void displayResponses(Responder currentResponder) {
        int count = 1;
        for (Response response : currentResponder.getResponses()) {
            System.out.println(count + ". Q: " + response.getResponseQuestion().getPollQuestion()
                    + "/ A: " + response.getResponseAnswer());
            count++;
        }
    }

    // EFFECTS: handles user input in responder menu
    @SuppressWarnings("methodlength")
    private void processResponderCommand(String command) {
        switch (command) {
            case "3":
                editResponder();
                break;
            case "4":
                if (currentPoll.getPollResponders().get(currentResponderIndex).getResponses().size() != 0) {
                    removeResponse();
                } else {
                    System.out.println("Responder Has No Responses");
                }
                break;
            case "5":
                makeResponder();
                currentResponderIndex = currentPoll.getPollResponders().size() - 1;
                break;
            case "6":
                removeResponder();
                break;
            case "7":
                break;
            case "1":
                if (!(currentResponderIndex == 0)) { // if false default
                    currentResponderIndex--;
                    break;
                }
            case "2":
                if (!(currentResponderIndex >= currentPoll.getPollResponders().size() - 1)) { // if false default
                    currentResponderIndex++;
                    break;
                }
            default:
                System.out.println("Invalid Response");
                System.out.println();
        }
    }

    // MODIFIES: this
    // EFFECTS: user is presented menu to Confirm if they want to delete the current
    // responder
    private void removeResponder() {
        boolean keepGoing = true;
        while (keepGoing) {
            String command = null;
            System.out.println("Enter \"C\" To Delete Responder: ");
            command = input.next().toLowerCase();
            if (command.equals("c")) {
                System.out.println("Responder Was Removed");
                System.out.println();
                currentPoll.removeResponder(currentResponderIndex);
                if (currentResponderIndex == currentPoll.getPollResponders().size()) {
                    currentResponderIndex--;
                }
                break;
            }
            System.out.println("Deletion Canceled");
            break;
        }
        keepGoing = false;
    }

    // MODIFIES: this
    // EFFECTS: user is asked what response number to remove and is then aksed to
    // confirm, if confirmed chosen index response is removed from current Responder
    @SuppressWarnings("methodlength")
    private void removeResponse() {
        boolean keepGoing = true;
        while (keepGoing) {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            int validMax = currentPoll.getPollResponders().get(currentResponderIndex).getResponses().size();
            String command = null;
            System.out.println("Enter Response Number You Want To Delete: ");
            boolean proceed = true;
            command = input.next();
            int intCommand = -1;
            try {
                Integer.parseInt(command);
                intCommand = Integer.parseInt(command);
                proceed = (1 <= intCommand && intCommand <= validMax);
            } catch (Exception numberFormatException) {
                proceed = false;
            }
            if (proceed) {
                System.out.println("Enter \"C\" To Delete Answer: ");
                command = input.next().toLowerCase();
                if (command.equals("c")) {
                    System.out.println("Response Was Removed");
                    System.out.println();
                    currentResponder.removeResponse(intCommand - 1);
                    keepGoing = false;
                }
                System.out.println("Deletion Cancelled");
                keepGoing = false;
            } else {
                System.out.println("Invalid Input");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: user is presented menu to choose what element of responder to
    // change(excluding response list)
    private void editResponder() {
        String command = "";
        while (!command.equals("0")) {
            System.out.println("Select Responder Attribute To Change");
            System.out.println("0. Return To Current Responder");
            System.out.println("1. Change First Name");
            System.out.println("2. Change Last Name");
            System.out.println("3. Change Phone Number");
            System.out.println("4. Change Email");
            System.out.println("5. Change Age");
            System.out.println("6. Change Gender");
            System.out.println("7. Change Income");
            System.out.println("8. Change Occupation");
            System.out.println("Enter Choice");
            command = input.next();
            handleEditResponderCommand(command);
        }
    }

    // EFFECTS: handles user input for edit responder menu
    @SuppressWarnings("methodlength")
    private void handleEditResponderCommand(String command) {
        Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
        switch (command) {
            case "0":
                break;
            case "1":
                System.out.println("Enter New Responder First Name: ");
                String newFirst = input.next();
                currentResponder.setFirstName(newFirst);
                break;
            case "2":
                System.out.println("Enter New Responder Last Name: ");
                String newLast = input.next();
                currentResponder.setLastName(newLast);
                break;
            case "3":
                long newNumber = enterValidPhoneNumber();
                currentResponder.setPhoneNumber(newNumber);
                break;
            case "4":
                String newEmail = enterValidEmail();
                currentResponder.setEmail(newEmail);
                break;
            case "5":
                int newAge = enterValidAge();
                currentResponder.setAge(newAge);
                break;
            case "6":
                int newGenderId = enterGender();
                currentResponder.setGenderId(newGenderId);
                break;
            case "7":
                int newIncome = enterValidIncome();
                currentResponder.setIncome(newIncome);
                break;
            case "8":
                System.out.println("Enter New Responder First Name: ");
                String newOccupation = input.next();
                currentResponder.setOccupation(newOccupation);
                break;
            default:
                System.out.println("Invalid Response");
                System.out.println();
        }
    }

    // MODIFIES: this
    // EFFECTS: prompts user to input a new name for the poll
    private void changeName() {
        boolean keepGoing = true;
        while (keepGoing) {
            String command = null;
            System.out.println("Enter New Poll Name: ");
            command = input.next();
            if (command.length() == 0) {
                System.out.println("Poll Name Must Be Minimum 1 Characters Long");
                System.out.println();
            } else {
                currentPoll.setPollName(command);
                System.out.println("Poll Name Updated");
                keepGoing = false;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: prompts user to confirm that they want to exit, if confirmed
    // application exits.
    private void confirmExit() {
        System.out.println("Enter \"Exit\" to exit application");
        String input = this.input.next().toLowerCase();
        if (input.equals("exit")) {
            System.out.println("\nExiting!");
            keepGoing = false;
        }
    }
}