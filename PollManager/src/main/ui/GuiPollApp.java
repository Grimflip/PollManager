package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.PieStyler.LabelType;

import model.EventLog;
import model.Poll;
import model.Question;
import model.Responder;
import model.Response;
import persistence.JsonReader;
import persistence.JsonWriter;

// The Gui Poll App
public class GuiPollApp extends JPanel
        implements ActionListener, ListSelectionListener, DocumentListener, WindowListener
        
 {
    private int currentQuestionIndex;
    private int currentResponderIndex;
    private int currentPollQuestionIndex;
    private int currentStatQuestionIndex;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private JTabbedPane tabbedPane;
    private static final String JSON_STORE = "PollManager/data/SavedPoll.json";
    private Poll currentPoll;
    private JFrame frame;
    private JPanel mainMenu;
    private DefaultListModel answerListModel;
    private DefaultListModel responseListModel;
    private DefaultListModel pollAnswerListModel;
    private DefaultListModel pollResponderListModel;
    private DefaultListModel occupationListModel;
    private JList responseList;
    private JList answerList;
    private JList pollAnswerList;
    private JList responderList;
    private JList occupationList;
    private JTextField answer;
    private JTextField occupation;
    private boolean questionEntryMode;
    private boolean questionEditMode;
    private boolean responderEntryMode;
    private boolean pollResponderEntryMode;
    private boolean responderEditMode;
    private boolean editPollName;
    private boolean runPoll;
    private boolean hasFiltered;
    private boolean filteringOccupations;
    private boolean filteringGender;
    private boolean filteringIncome;
    private boolean filteringAge;
    private List<Component> responderAttributes;
    private List<Component> interval;
    private JComboBox filterGenderBox;
    private boolean doReload;
    private Responder answeringResponder;

    // The poll app
    // code based on: CPSC 210 AlarmSystem, CPSC 210 Lab 4 FlashcardReviewer,CPSC
    // 210 Json Serialization Starter
    // and various examples from
    // https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html
    // pie charts made using XChart: https://github.com/knowm/XChart
    public GuiPollApp() throws FileNotFoundException {
        super(new GridLayout(1, 1));
        this.currentPoll = new Poll("My New Poll");
        startUp();
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        generatePanes();
        startApp();
    }

    // MODIFIES: this
    // EFFECCTS: Starts up the poll
    private void startUp() {
        this.currentQuestionIndex = 0;
        this.currentResponderIndex = 0;
        this.currentStatQuestionIndex = 0;
        this.questionEntryMode = false;
        this.questionEditMode = false;
        this.responderEntryMode = false;
        this.responderEditMode = false;
        this.editPollName = false;
        this.doReload = true;
        this.hasFiltered = false;
        answeringResponder = null;
        occupationListModel = new DefaultListModel<>();
        this.responderAttributes = new ArrayList<>(); // {firstName, LastName,number,email,age,gender,income,occupation}
        this.interval = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: generates the tabs of the poll manager
    private void generatePanes() {
        this.responderAttributes = new ArrayList<>(); // {firstName, LastName,number,email,age,gender,income,occupation}
        tabbedPane = new JTabbedPane();
        JComponent panel1 = handleMainMenu();
        tabbedPane.addTab("Poll", null, panel1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        JComponent panel2 = managePollTab();
        tabbedPane.addTab("Manage Questions", null, panel2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        JComponent panel3 = manageRespondersTab();
        tabbedPane.addTab("Manage Responders", null, panel3);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_4);
        JComponent panel4 = manageStatisticsTab();
        tabbedPane.addTab("Poll Statistics", null, panel4);
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_3);
        tabbedPane.setEnabled(!runPoll);
    }

    // EFFECTS: initializes the manageStatistics tab or if poll has no questions a
    // JPanel telling the user to make one
    private JComponent manageStatisticsTab() {
        if (currentPoll.getPollQuestions().size() == 0) {
            return makeBlankStatTab();
        } else if (filteringOccupations) {
            return filterProffesionsTab();
        } else if (filteringGender) {
            return filterGenderTab();
        } else if (filteringIncome || filteringAge) {
            interval.clear();
            return filterIntervalTab();
        } else {
            return makeStatisticsTab();
        }
    }

    // EFFECTS: returns a panel where user can input intervals and hit confirm to
    // apply filters
    private JComponent filterIntervalTab() {
        JPanel filterIntervalPanel = new JPanel(new GridLayout(0, 1));
        filterIntervalPanel.add(makeBoundPanel("Lower Bound:"));
        filterIntervalPanel.add(makeBoundPanel("Upper Bound:"));
        filterIntervalPanel.add(makeFilterConfirmButton());
        String filterType = "";
        if (filteringIncome) {
            filterType = "Income";
        } else {
            filterType = "Age";
        }
        filterIntervalPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Enter Upper and Lower bounds For Filtering " + filterType),
                        BorderFactory.createEmptyBorder(5, 150, 5, 150)));
        return filterIntervalPanel;
    }

    // EFFECTS: returns a confirm button for filtering
    private Component makeFilterConfirmButton() {
        JPanel finalizationPanel = new JPanel(new GridLayout(1, 1));
        JLabel finalizationLabel = new JLabel("");
        JButton confirmButton = new JButton("Confirm");
        finalizationLabel.setHorizontalAlignment(JLabel.CENTER);
        confirmButton.setActionCommand("3confirm");
        confirmButton.addActionListener(this);
        finalizationPanel.add(finalizationLabel);
        finalizationPanel.add(confirmButton);
        return finalizationPanel;
    }

    // MODIFIES: this
    // EFFECTS: Returns a panel where user can input an interval and adds it to
    // interval list
    private JComponent makeBoundPanel(String bound) {
        JTextField incomeField = new JTextField(10);
        incomeField.setToolTipText("Enter Integer For " + bound);
        JLabel incomeLabel = new JLabel(bound);
        incomeLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel incomePanel = new JPanel(new GridLayout(1, 1));
        incomePanel.add(incomeLabel);
        incomePanel.add(incomeField);
        interval.add(incomeField);
        return incomePanel;
    }

    // EFFECTS: returns genderTab panel where user can choose one of three gender
    // ids and hit confirm
    private JComponent filterGenderTab() {
        String[] genderArray = { "Man", "Woman", "Other" };
        filterGenderBox = new JComboBox(genderArray);
        filterGenderBox.setSelectedIndex(0);
        JPanel genderPanel = new JPanel(new GridLayout(0, 1));
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setActionCommand("3confirm");
        confirmButton.addActionListener(this);
        genderPanel.add(filterGenderBox);
        genderPanel.add(confirmButton);
        return genderPanel;
    }

    // EFFECTS: returns tab where user can create a list of proffesions to KEEP and
    // hit confirm to apply filters
    private JComponent filterProffesionsTab() {
        occupationList = new JList(occupationListModel);
        occupationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        occupationList.setSelectedIndex(answerListModel.size());
        occupationList.addListSelectionListener(this);
        occupationList.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(occupationList);
        listScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Enter Occupations"),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        occupation = new JTextField(10);
        occupation.setActionCommand("3input");
        occupation.addActionListener(this);
        occupation.getDocument().addDocumentListener(this);
        JPanel filterProffesionsTab = new JPanel(new GridLayout(0, 1));
        filterProffesionsTab.add(listScrollPane);
        filterProffesionsTab.add(occupation);
        filterProffesionsTab.add(makeFilterConfirmButton());
        return filterProffesionsTab;
    }

    // EFFECTS: returns a blank panel that tells user they have no questions in
    // current poll
    private JComponent makeBlankStatTab() {
        JLabel blank = new JLabel("Poll Has No Questions");
        blank.setHorizontalAlignment(JLabel.CENTER);
        JPanel blankPanel = new JPanel(new GridLayout(0, 1));
        blankPanel.add(blank);
        return blankPanel;
    }

    // EFFECTS: returns statistics tab with a pie chart detailing the responder
    // answer choices and buttons to go to next/previous question, buttons to apply
    // filters and a button to clear them
    private JComponent makeStatisticsTab() {
        Question currentQuestion = currentPoll.getPollQuestions().get(currentStatQuestionIndex);
        PieChart stats = getStats(currentQuestion);
        JPanel chartPanel = new XChartPanel<PieChart>(stats);
        JPanel statPanel = new JPanel();
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JPanel filterPanel = new JPanel(new GridLayout(1, 0));
        statPanel.add(chartPanel);
        statPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Question# " + (currentStatQuestionIndex + 1) + ": "
                                + currentQuestion.getPollQuestion()),
                        BorderFactory.createEmptyBorder(5, 150, 5, 150)));
        buttonPanel.add(makePrevStatButton());
        filterPanel.add(makeApplyAgeFilterButton());
        filterPanel.add(makeApplyIncomeFilterButton());
        filterPanel.add(makeApplyGenderFilterButton());
        filterPanel.add(makeApplyOccupationFilterButton());
        buttonPanel.add(makeClearFilterButton());
        buttonPanel.add(filterPanel);
        buttonPanel.add(makeNextStatButton());
        statPanel.add(buttonPanel);
        statPanel.add(filterPanel);
        return statPanel;
    }

    // EFFECTS: returns a pie chart based on the currentQuestion
    private PieChart getStats(Question currentQuestion) {
        PieChart stats = new PieChartBuilder().width((int) (Toolkit.getDefaultToolkit().getScreenSize().width / 2.25))
                .height(((int) (Toolkit.getDefaultToolkit().getScreenSize().height / 2.3)))
                .title("").build();
        List<String> filteredAnswers = currentPoll.extractResponderAnswers(currentStatQuestionIndex);
        for (String answer : currentQuestion.getPollAnswers()) {
            stats.addSeries(answer, getCount(answer, filteredAnswers));
        }
        stats.getStyler().setLabelType(LabelType.NameAndPercentage);
        return stats;
    }

    // EFFECTS: returns button that lets user begin filtering responses based on the
    // responder's occupation
    private Component makeApplyOccupationFilterButton() {
        JButton applyFilterButton = new JButton("Apply Occupation Filters");
        applyFilterButton.setActionCommand("3applyOccupation");
        applyFilterButton.addActionListener(this);
        return applyFilterButton;
    }

    // EFFECTS: returns button that lets user begin filtering responses based on the
    // responder's gender id
    private Component makeApplyGenderFilterButton() {
        JButton applyFilterButton = new JButton("Apply Gender Filters");
        applyFilterButton.setActionCommand("3applyGender");
        applyFilterButton.addActionListener(this);
        return applyFilterButton;
    }

    // EFFECTS: returns button that lets user begin filtering responses based on the
    // responder's ages with: lower <= responder(s) age(s) <= upper
    private Component makeApplyAgeFilterButton() {
        JButton applyFilterButton = new JButton("Apply Age Filters");
        applyFilterButton.setActionCommand("3applyAge");
        applyFilterButton.addActionListener(this);
        return applyFilterButton;
    }

    // EFFECTS: returns button that lets user begin filtering responses based on the
    // responder's incomes with: lower <= responder(s) incomes(s) <= upper
    private Component makeApplyIncomeFilterButton() {
        JButton applyFilterButton = new JButton("Apply Income Filters");
        applyFilterButton.setActionCommand("3applyIncome");
        applyFilterButton.addActionListener(this);
        return applyFilterButton;
    }

    // EFFECTS: returns button that lets user clear the filters thus resseting the
    // pie chart
    private Component makeClearFilterButton() {
        JButton clearFilterButton = new JButton("Clear Filters");
        clearFilterButton.setActionCommand("3clear");
        clearFilterButton.addActionListener(this);
        clearFilterButton.setEnabled(hasFiltered);
        return clearFilterButton;
    }

    // EFFECTS: returns button that lets user go to next question pie chart
    private Component makeNextStatButton() {
        JButton nextButton = new JButton("Next Question");
        nextButton.setActionCommand("3next");
        nextButton.addActionListener(this);
        nextButton.setEnabled(!(currentStatQuestionIndex >= currentPoll.getPollQuestions().size() - 1));
        return nextButton;
    }

    // EFFECTS: returns button that lets user go to previous question pie chart
    private Component makePrevStatButton() {
        JButton prevButton = new JButton("Prev Question");
        prevButton.setActionCommand("3prev");
        prevButton.addActionListener(this);
        prevButton.setEnabled(!(currentStatQuestionIndex == 0));
        return prevButton;
    }

    // HELPER for displayStatsMenu()
    // EFFECTS: returns number of instances of answer in filteredAnswers
    private int getCount(String answer, List<String> filteredAnswers) {
        int result = 0;
        for (String listAnswer : filteredAnswers) {
            if (listAnswer.equals(answer)) {
                result++;
            }
        }
        return result;
    }

    // MODIFIES: this
    // EFFECTS: initializes the manageResponders tab or if poll has no responder
    // prompt to make one
    private JComponent manageRespondersTab() {
        boolean doPrompt = (responderEntryMode || responderEditMode);
        if (currentPoll.getPollResponders().size() == 0 || doPrompt) {
            return newResponderPrompt();
        } else {
            return manageResponders();
        }
    }

    // MODIFIES: this
    // EFFECTS: returns Jpanel allowing user to view and go through each responder
    // and edit them
    private JComponent manageResponders() {
        Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
        JPanel manageResponders = new JPanel((new GridLayout(0, 1)));
        manageResponders.add(responderInfo(currentResponder));
        manageResponders.add(manageResponseList(currentResponder));
        manageResponders.add(responseDeleteButton());
        manageResponders.add(responderConsole());
        manageResponders.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Responder# " + (currentResponderIndex + 1)),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        return manageResponders;
    }

    // EFFECTS: returns button that deletes currently selected response
    private Component responseDeleteButton() {
        JButton deleteButton = new JButton("Delete Response");
        deleteButton.setActionCommand("2remove");
        deleteButton.addActionListener(this);
        return deleteButton;
    }

    // EFFECTS: returns the responder console with buttons for
    // previes,next,edit,remove,add responder
    private Component responderConsole() {
        JPanel responderConsole = new JPanel((new GridLayout(1, 1)));
        responderConsole.add(getResponderPrevButton());
        responderConsole.add(getResponderRemoveButton());
        responderConsole.add(getResponderEditButton());
        responderConsole.add(getResponderAddButton());
        responderConsole.add(getResponderNextButton());
        return responderConsole;
    }

    // EFFECTS: returns nextButton which goes to next question in current poll
    // question list
    private JButton getResponderNextButton() {
        JButton nextButton = new JButton("Next Responder");
        nextButton.setActionCommand("2next");
        nextButton.addActionListener(this);
        nextButton.setEnabled(!(currentResponderIndex >= currentPoll.getPollResponders().size() - 1));
        return nextButton;
    }

    // EFFECTS: returns addButton which prompts user to make a new question
    private JButton getResponderAddButton() {
        JButton addButton = new JButton("Add Responder");
        addButton.setActionCommand("2add");
        addButton.addActionListener(this);
        return addButton;
    }

    // EFFECTS: returns editButton which prompts user to edit current question
    private JButton getResponderEditButton() {
        JButton editButton = new JButton("Edit Responder");
        editButton.setActionCommand("2edit");
        editButton.addActionListener(this);
        return editButton;
    }

    // EFFECTS: returns removeButton which removes current question
    private JButton getResponderRemoveButton() {
        JButton removeButton = new JButton("Delete Responder");
        removeButton.setActionCommand("2del");
        removeButton.addActionListener(this);
        return removeButton;
    }

    // EFFECTS: returns prevButton which goes to previous question in current poll
    // question list
    private JButton getResponderPrevButton() {
        JButton prevButton = new JButton("Prev Responder");
        prevButton.setActionCommand("2prev");
        prevButton.addActionListener(this);
        prevButton.setEnabled(!(currentResponderIndex == 0));
        return prevButton;
    }

    // MODIFIES: this
    // EFFECTS: return list showing Responder's responses
    private Component manageResponseList(Responder currentResponder) {
        responseListModel = new DefaultListModel();
        int count = 0;
        for (Response response : currentResponder.getResponses()) {
            count++;
            responseListModel.addElement("Response " + count + ". Q: "
                    + response.getResponseQuestion().getPollQuestion() + " / A: " + response.getResponseAnswer());
        }
        responseList = new JList(responseListModel);
        responseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        responseList.setSelectedIndex(responseListModel.size());
        responseList.addListSelectionListener(this);
        responseList.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(responseList);
        listScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Responses"),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)));

        return listScrollPane;
    }

    // EFFECTS: Returns JPanel with responder attributes
    private Component responderInfo(Responder currentResponder) {
        JPanel responderInfo = new JPanel((new GridLayout(0, 1)));
        responderInfo.add(new JLabel("First Name: " + currentResponder.getFirstName()));
        responderInfo.add(new JLabel("Last Name: " + currentResponder.getLastName()));
        responderInfo.add(new JLabel("Phone Number: " + currentResponder.getPhoneNumber()));
        responderInfo.add(new JLabel("Email: " + currentResponder.getEmail()));
        responderInfo.add(new JLabel("Age: " + currentResponder.getAge()));
        responderInfo.add(new JLabel("Gender Id: " + currentResponder.getGender()));
        responderInfo.add(new JLabel("Income: " + currentResponder.getIncome()));
        responderInfo.add(new JLabel("Occupation: " + currentResponder.getOccupation()));
        return responderInfo;
    }

    // MODIFIES: this
    // EFFECTS: returns JPanel allowing user to add responders/ edit current
    // responder
    private JComponent newResponderPrompt() {
        JPanel insertResponderPanel = new JPanel(new GridLayout(0, 1));
        insertResponderPanel.add(getFirstNamePanel());
        insertResponderPanel.add(getLastNamePanel());
        insertResponderPanel.add(getNumberPanel());
        insertResponderPanel.add(getEmailPanel());
        insertResponderPanel.add(getAgePanel());
        insertResponderPanel.add(getGenderPanel());
        insertResponderPanel.add(getIncomePanel());
        insertResponderPanel.add(getOccupationPanel());
        insertResponderPanel.add(getFinalizationPanel());
        insertResponderPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Responder"),
                        BorderFactory.createEmptyBorder(5, 150, 5, 150)));
        return insertResponderPanel;
    }

    // MODIFIES: this
    // EFFECTS: returns numberPanel and adds numberField to responderAttributes
    private JPanel getNumberPanel() {
        JTextField numberField = new JTextField(10);
        if (responderEditMode && !pollResponderEntryMode) {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            numberField.setText(String.valueOf(currentResponder.getRawPhoneNumber()));
        }
        numberField.setToolTipText("Enter 10 Digit Phone Number as XXXXXXXXXX");
        JLabel numberLabel = new JLabel("Phone Number:");
        numberLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel numberPanel = new JPanel(new GridLayout(1, 1));
        numberPanel.add(numberLabel);
        numberPanel.add(numberField);
        responderAttributes.add(numberField);
        return numberPanel;
    }

    // MODIFIES: this
    // EFFECTS: returns finalizationPanel and adds it to responderAttributes
    private JPanel getFinalizationPanel() {
        JPanel finalizationPanel = new JPanel(new GridLayout(1, 1));
        JLabel finalizationLabel = new JLabel("Enter New Attributes and hit confirm");
        JButton confirmButton = new JButton("Confirm");
        finalizationLabel.setHorizontalAlignment(JLabel.CENTER);
        confirmButton.setActionCommand("2confirm");
        confirmButton.addActionListener(this);
        finalizationPanel.add(finalizationLabel);
        finalizationPanel.add(confirmButton);
        return finalizationPanel;
    }

    // MODIFIES: this
    // EFFECTS: returns occupationPanel so user can input a responders occupation
    // and adds the field to responderAttributes
    private JPanel getOccupationPanel() {
        JTextField occupationField = new JTextField(10);
        if (responderEditMode && !pollResponderEntryMode) {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            occupationField.setText(currentResponder.getOccupation());
        }
        occupationField.setToolTipText("Enter An Occupation");
        JLabel occupationLabel = new JLabel("Occupation:");
        occupationLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel occupationPanel = new JPanel(new GridLayout(1, 1));
        occupationPanel.add(occupationLabel);
        occupationPanel.add(occupationField);
        responderAttributes.add(occupationField);
        return occupationPanel;
    }

    // MODIFIES: this
    // EFFECTS: returns incomePanel so user can input a responders income
    // and adds the field to responderAttributes
    private JPanel getIncomePanel() {
        JTextField incomeField = new JTextField(10);
        if (responderEditMode && !pollResponderEntryMode) {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            incomeField.setText(String.valueOf(currentResponder.getIncome()));
        }
        incomeField.setToolTipText("Enter Integer Value Greater Or Equal To 0");
        JLabel incomeLabel = new JLabel("Income:");
        incomeLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel incomePanel = new JPanel(new GridLayout(1, 1));
        incomePanel.add(incomeLabel);
        incomePanel.add(incomeField);
        responderAttributes.add(incomeField);
        return incomePanel;
    }

    // MODIFIES: this
    // EFFECTS: returns genderPanel so user can select a gender from 3 options and
    // genderBox to responder attributes
    private JPanel getGenderPanel() {
        String[] genderArray = { "Man", "Woman", "Other" };
        JComboBox genderBox = new JComboBox(genderArray);
        genderBox.setSelectedIndex(0);
        if (responderEditMode && !pollResponderEntryMode) {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            genderBox.setSelectedIndex(currentResponder.getGenderId() - 1);
        }
        JLabel genderLabel = new JLabel("Gender ID:");
        genderLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel genderPanel = new JPanel(new GridLayout(1, 1));
        genderPanel.add(genderLabel);
        genderPanel.add(genderBox);
        responderAttributes.add(genderBox);
        return genderPanel;
    }

    // MODIFIES: this
    // EFFECTS: returns agePanel so user can input a responders age
    // and adds the field to responderAttributes
    private JPanel getAgePanel() {
        JTextField ageField = new JTextField(10);
        if (responderEditMode && !pollResponderEntryMode) {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            ageField.setText(String.valueOf(currentResponder.getAge()));
        }
        ageField.setToolTipText("Enter Integer Value Greater Than 0");
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel agePanel = new JPanel(new GridLayout(1, 1));
        agePanel.add(ageLabel);
        agePanel.add(ageField);
        responderAttributes.add(ageField);
        return agePanel;
    }

    // MODIFIES: this
    // EFFECTS: returns emailPanel so user can input a responders email
    // and adds the field to responderAttributes
    private JPanel getEmailPanel() {
        JTextField emailField = new JTextField(10);
        if (responderEditMode && !pollResponderEntryMode) {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            emailField.setText(currentResponder.getEmail());
        }
        emailField.setToolTipText("Enter A Valid Email Ending With .com or .ca");
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel emailPanel = new JPanel(new GridLayout(1, 1));
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        responderAttributes.add(emailField);
        return emailPanel;
    }

    // MODIFIES: this
    // EFFECTS: returns lastNamePanel so user can input a responders last name
    // and adds the field to responderAttributes
    private JPanel getLastNamePanel() {
        JTextField lastNameField = new JTextField(10);
        if (responderEditMode && !pollResponderEntryMode) {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            lastNameField.setText(currentResponder.getLastName());
        }
        lastNameField.setToolTipText("Enter Last Name");
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel lastNamePanel = new JPanel(new GridLayout(1, 1));
        lastNamePanel.add(lastNameLabel);
        lastNamePanel.add(lastNameField);
        responderAttributes.add(lastNameField);
        return lastNamePanel;
    }

    // MODIFIES: this
    // EFFECTS: returns firstNamePanel so user can input a responders first name
    // and adds the field to responderAttributes
    private JPanel getFirstNamePanel() {
        JTextField firstNameField = new JTextField(10);
        if (responderEditMode && !pollResponderEntryMode) {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            firstNameField.setText(currentResponder.getFirstName());
        }
        JLabel firstNameLabel = new JLabel("First Name: ");
        firstNameField.setToolTipText("Enter First Name");
        firstNameLabel.setHorizontalAlignment(JLabel.CENTER);
        JPanel firstNamePanel = new JPanel(new GridLayout(1, 1));
        firstNamePanel.add(firstNameLabel);
        firstNamePanel.add(firstNameField);
        responderAttributes.add(firstNameField);
        return firstNamePanel;
    }

    // MODIFIES: this
    // EFFECTS: initializes the managePoll tab or if poll has no questions prompt to
    // make one
    private JComponent managePollTab() {
        boolean doPrompt = (questionEntryMode || questionEditMode);
        if (currentPoll.getPollQuestions().size() == 0 || doPrompt) {
            return newQuestionPrompt();
        } else {
            return manageQuestions();
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes the question viewer/editor
    private JComponent manageQuestions() {
        Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);
        JPanel managePoll = new JPanel((new GridLayout(0, 1)));
        managePoll.add(makeP1(currentQuestion));
        managePoll.add(answerListViewer(currentQuestion));
        managePoll.add(answerConsole());
        managePoll.add(questionConsole());
        managePoll.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Question# " + (currentQuestionIndex + 1)),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        return managePoll;
    }

    // MODIFIES: this
    // EFFECTS: returns question console to change current question or edit/remove
    // question
    private Component questionConsole() {
        JPanel answerConsole = new JPanel((new GridLayout(1, 0)));
        answerConsole.add(getPrevButton());
        answerConsole.add(getRemoveButton());
        answerConsole.add(getEditButton());
        answerConsole.add(getAddButton());
        answerConsole.add(getNextButton());
        return answerConsole;
    }

    // EFFECTS: returns nextButton which goes to next question in current poll
    // question list
    private JButton getNextButton() {
        JButton nextButton = new JButton("Next Question");
        nextButton.setActionCommand("1next");
        nextButton.addActionListener(this);
        nextButton.setEnabled(!(currentQuestionIndex >= currentPoll.getPollQuestions().size() - 1));
        return nextButton;
    }

    // EFFECTS: returns addButton which prompts user to make a new question
    private JButton getAddButton() {
        JButton addButton = new JButton("Add New Question");
        addButton.setActionCommand("1add");
        addButton.addActionListener(this);
        return addButton;
    }

    // EFFECTS: returns editButton which prompts user to edit current question
    private JButton getEditButton() {
        JButton editButton = new JButton("Edit Question");
        editButton.setActionCommand("1edit");
        editButton.addActionListener(this);
        return editButton;
    }

    // EFFECTS: returns removeButton which removes current question
    private JButton getRemoveButton() {
        JButton removeButton = new JButton("Delete Question");
        removeButton.setActionCommand("1del");
        removeButton.addActionListener(this);
        return removeButton;
    }

    // EFFECTS: returns prevButton which goes to previous question in current poll
    // question list
    private JButton getPrevButton() {
        JButton prevButton = new JButton("Prev Question");
        prevButton.setActionCommand("1prev");
        prevButton.addActionListener(this);
        prevButton.setEnabled(!(currentQuestionIndex == 0));
        return prevButton;
    }

    // MODIFIES: this
    // EFFECTS: returns console to add/remove responses from question
    private Component answerConsole() {
        JPanel answerConsole = new JPanel((new GridLayout(1, 0)));
        JButton addButton = new JButton("Add Answer");
        addButton.setActionCommand("1input");
        addButton.addActionListener(this);
        addButton.setEnabled(true);
        JButton removeButton = new JButton("Delete Answer");
        removeButton.setActionCommand("1remove");
        removeButton.addActionListener(this);
        JPanel answerfieldPanel = new JPanel((new GridLayout(0, 1)));
        JLabel answerInstruction = new JLabel("Enter New Answer Below");
        answerInstruction.setHorizontalAlignment(JLabel.CENTER);
        answerfieldPanel.add(answerInstruction);
        answer = new JTextField(10);
        answer.setActionCommand("1input");
        answer.addActionListener(this);
        answer.getDocument().addDocumentListener(this);
        answerfieldPanel.add(answer);
        answerConsole.add(addButton);
        answerConsole.add(answerfieldPanel);
        answerConsole.add(removeButton);
        return answerConsole;
    }

    // MODIFIES: this
    // EFFECTS: returns scrollable list of currentQuestion answers
    private Component answerListViewer(Question currentQuestion) {

        answerListModel = new DefaultListModel();
        for (String answer : currentQuestion.getPollAnswers()) {
            answerListModel.addElement(answer);
        }
        answerList = new JList(answerListModel);
        answerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        answerList.setSelectedIndex(answerListModel.size());
        answerList.addListSelectionListener(this);
        answerList.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(answerList);
        listScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Answers"),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)));

        return listScrollPane;
    }

    // MODIFIES: this
    // EFFECTS: returns scrollable list of currentQuestion answers
    private Component pollAnswerListViewer(Question currentQuestion) {

        pollAnswerListModel = new DefaultListModel();
        for (String answer : currentQuestion.getPollAnswers()) {
            pollAnswerListModel.addElement(answer);
        }
        pollAnswerList = new JList(pollAnswerListModel);
        pollAnswerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pollAnswerList.setSelectedIndex(-1);
        pollAnswerList.addListSelectionListener(this);
        pollAnswerList.setVisibleRowCount(5);

        JScrollPane listScrollPane = new JScrollPane(pollAnswerList);
        listScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Answers"),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)));

        return listScrollPane;
    }

    // MODIFIES: this
    // EFFECTS: returns JPanel allowing user to add questions/ edit current question
    // prompt
    private JComponent newQuestionPrompt() {
        JLabel actionLabel = new JLabel("Enter new question prompt and hit enter");
        actionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JPanel textControlsPane = new JPanel((new GridLayout(0, 1)));
        JTextField textField = new JTextField(1);
        if (questionEditMode) {
            Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);
            textField.setText(currentQuestion.getPollQuestion());
        }
        textField.setActionCommand("1firstq");
        textField.addActionListener(this);
        textControlsPane.add(textField);
        textControlsPane.add(actionLabel);
        return textControlsPane;
    }

    // EFFECTS: creates jlabel with the current question prompt
    private JLabel makeP1(Question currentQuestion) {
        JLabel p1 = new JLabel("Question Prompt: " + currentQuestion.getPollQuestion());
        return p1;
    }

    // EFFECTS initializes the main menu tab
    private JComponent handleMainMenu() {
        if (editPollName) {
            return editPollNamePrompt();
        } else if (runPoll) {
            return runPoll();
        } else {
            return mainMenu();
        }
    }

    // EFFECTS: returns the tab corresponding to current state
    private JComponent runPoll() {
        if (pollResponderEntryMode) {
            return enterNewPollResponder();

        }
        if (answeringResponder == null) {
            return selectResponder();

        } else {
            return startPoll();

        }
    }

    // EFFECTS: returns panel where user can input in a new responder for the poll
    private JComponent enterNewPollResponder() {
        JPanel insertResponderPanel = new JPanel(new GridLayout(0, 1));
        insertResponderPanel.add(getFirstNamePanel());
        insertResponderPanel.add(getLastNamePanel());
        insertResponderPanel.add(getNumberPanel());
        insertResponderPanel.add(getEmailPanel());
        insertResponderPanel.add(getAgePanel());
        insertResponderPanel.add(getGenderPanel());
        insertResponderPanel.add(getIncomePanel());
        insertResponderPanel.add(getOccupationPanel());
        insertResponderPanel.add(getPollFinalizationPanel());
        insertResponderPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Responder"),
                        BorderFactory.createEmptyBorder(5, 150, 5, 150)));
        return insertResponderPanel;
    }

    // returns button for user to confirm the new poll responder inputs
    private Component getPollFinalizationPanel() {
        JPanel finalizationPanel = new JPanel(new GridLayout(1, 1));
        JLabel finalizationLabel = new JLabel("Enter new attributes and hit confirm");
        JButton confirmButton = new JButton("Confirm");
        finalizationLabel.setHorizontalAlignment(JLabel.CENTER);
        confirmButton.setActionCommand("0confirm");
        confirmButton.addActionListener(this);
        finalizationPanel.add(finalizationLabel);
        finalizationPanel.add(confirmButton);
        return finalizationPanel;
    }

    // EFFECTS: returns panel where viwer can see current question, pick an answer
    // to that question from a list and confirm it
    private JComponent startPoll() {
        int questionAmount = currentPoll.getPollQuestions().size();
        Question currentQuestion = currentPoll.getPollQuestions().get(currentPollQuestionIndex);
        JPanel pollViewer = new JPanel(new GridLayout(0, 1));
        pollViewer.add(new JLabel(getQuestionMark(currentQuestion.getPollQuestion())));
        pollViewer.add(pollAnswerListViewer(currentQuestion));
        JButton selectAnswerButton = new JButton("Confirm Answer");
        selectAnswerButton.setActionCommand("0confirmAnswer");
        selectAnswerButton.addActionListener(this);
        pollViewer.add(selectAnswerButton);
        pollViewer.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                answeringResponder.getFirstName() + " " + answeringResponder.getLastName()
                                        + " answering " + "Question# " + (currentPollQuestionIndex + 1) + " out of "
                                        + questionAmount + " from poll: " + currentPoll.getPollName()),
                        BorderFactory.createEmptyBorder(5, 150, 5, 150)));
        return pollViewer;
    }

    // EFFECTS: appends "?" to the end of pollQuestion if does not contain it at the
    // end of string
    private String getQuestionMark(String pollQuestion) {
        int length = pollQuestion.length();
        if (pollQuestion.substring(length - 1, length).equals("?")) {
            return pollQuestion;
        }
        return (pollQuestion + "?");
    }

    // returns panel where user can select a responder to run the poll with
    private JComponent selectResponder() {
        JPanel selectResponderPanel = new JPanel(new GridLayout(0, 1));
        selectResponderPanel.add(getResponderList());
        selectResponderPanel.add(getChooserConsole());
        return selectResponderPanel;
    }

    // EFECTS: returns panel where user can confirm the chosen responder or instead
    // make a new one
    private Component getChooserConsole() {
        JPanel chooserConsolePanel = new JPanel(new GridLayout(0, 1));
        JButton selectButton = new JButton("Confirm Choice");
        selectButton.setActionCommand("0select");
        selectButton.addActionListener(this);
        JButton newButton = new JButton("Make New Responder Instead");
        newButton.setActionCommand("0add");
        newButton.addActionListener(this);
        chooserConsolePanel.add(selectButton);
        chooserConsolePanel.add(newButton);
        return chooserConsolePanel;
    }

    // EFFECTS: returns JScrollPane where user can view and select a responder
    private JComponent getResponderList() {
        pollResponderListModel = new DefaultListModel();
        int count = 0;
        for (Responder responder : currentPoll.getPollResponders()) {
            count++;
            pollResponderListModel.addElement(count + ". " + responder.getFirstName() + " " + responder.getLastName());
        }
        responderList = new JList(pollResponderListModel);
        responderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        responderList.setSelectedIndex(pollResponderListModel.size());
        responderList.addListSelectionListener(this);
        responderList.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(responderList);
        listScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Select responder to take " + currentPoll.getPollName()),
                        BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        return listScrollPane;
    }

    // MODIFIES: this
    // EFFECTS: lets user edit current poll name
    private JComponent editPollNamePrompt() {
        JLabel actionLabel = new JLabel("Enter new poll name and hit enter");
        actionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JPanel textControlsPane = new JPanel((new GridLayout(0, 1)));
        JTextField textField = new JTextField(1);
        textField.setText(currentPoll.getPollName());
        textField.setActionCommand("0name");
        textField.addActionListener(this);
        textControlsPane.add(textField);
        textControlsPane.add(actionLabel);
        return textControlsPane;
    }

    // MODIFIES: this
    // EFFECTS: initializes the main menu tab
    private JComponent mainMenu() {
        mainMenu = new JPanel((new GridLayout(0, 1)));
        mainMenu.add(makeM1());
        mainMenu.add(makeM2());
        mainMenu.add(makeM3());
        mainMenu.add(makeM4());
        return mainMenu;
    }

    // Effects: creates change poll name button
    private JButton makeM4() {
        JButton m4 = new JButton("Change Poll Name");
        m4.setMnemonic(KeyEvent.VK_E);
        m4.setActionCommand("0changename");
        m4.addActionListener(this);
        m4.setToolTipText("Click this button to change the name of the poll.");
        return m4;
    }

    // Effects: creates load current poll button
    private JButton makeM3() {
        JButton m3 = new JButton("Load Current Poll");
        m3.setMnemonic(KeyEvent.VK_E);
        m3.setActionCommand("0load");
        m3.addActionListener(this);
        m3.setToolTipText("Click this button to load saved poll.");
        return m3;
    }

    // Effects: creates save current poll button
    private JButton makeM2() {
        JButton m2 = new JButton("Save Current Poll");
        m2.setVerticalTextPosition(AbstractButton.BOTTOM);
        m2.setHorizontalTextPosition(AbstractButton.CENTER);
        m2.setMnemonic(KeyEvent.VK_M);
        m2.setActionCommand("0save");
        m2.addActionListener(this);
        m2.setToolTipText("Click this button to save current poll.");
        return m2;
    }

    // Effects: creates run poll button
    private JButton makeM1() {
        String b1String = ("Run An Instance Of " + currentPoll.getPollName());
        JButton m1 = new JButton(b1String);
        m1.setVerticalTextPosition(AbstractButton.CENTER);
        m1.setHorizontalTextPosition(AbstractButton.CENTER); // aka LEFT, for left-to-right locales

        m1.setMnemonic(KeyEvent.VK_D);
        m1.setActionCommand("0run");
        boolean pollReady = currentPoll.isReadyPoll();
        m1.setEnabled(pollReady);
        m1.addActionListener(this);
        m1.setToolTipText("Click this to run the current poll");
        return m1;
    }

    // MODIFIES: this
    // EFFECTS: initializes the Poll manager gui
    public void startApp() {
        frame = new JFrame("Poll Manager");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(this);
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width / 2,
                ((int) (Toolkit.getDefaultToolkit().getScreenSize().height / 1.6))));
        centreOnScreen();
        frame.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: Centeres the frame to the centere of the screen
    private void centreOnScreen() {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        frame.setLocation((width - getWidth()) / 4, (height - getHeight()) / 4);
    }

    // MODIFIES: this
    // EFFECTS: sorts the action commands and reloads the gui if needed
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().startsWith("0")) {
            menuActionHandle(e);
        } else if (e.getActionCommand().startsWith("1")) {
            pollActionHandle(e);
        } else if (e.getActionCommand().startsWith("2")) {
            responderActionHandle(e);
        } else if (e.getActionCommand().startsWith("3")) {
            statActionHandle(e);
        }
        if (doReload) {
            reload();
        }
        doReload = true;
        tabbedPane.setSelectedIndex(Integer.valueOf(e.getActionCommand().substring(0, 1))); // makes the reload stay on
                                                                                            // the current tab
    }

    // MODIFIES: this
    // EFFECTS: sorts action commands of the stat tab
    private void statActionHandle(ActionEvent e) {
        if ("3next".equals(e.getActionCommand())) {
            currentStatQuestionIndex++;
        } else if ("3prev".equals(e.getActionCommand())) {
            currentStatQuestionIndex--;
        } else if ("3applyIncome".equals(e.getActionCommand())) {
            filteringIncome = true;
        } else if ("3applyAge".equals(e.getActionCommand())) {
            filteringAge = true;
        } else if ("3applyGender".equals(e.getActionCommand())) {
            filteringGender = true;
        } else if ("3applyOccupation".equals(e.getActionCommand())) {
            filteringOccupations = true;
        } else if ("3confirm".equals(e.getActionCommand())) {
            confirmFilter();
        } else if ("3clear".equals(e.getActionCommand())) {
            hasFiltered = false;
            currentPoll.clearFilteredResponders();
            occupationListModel.clear();
        } else if ("3input".equals(e.getActionCommand())) {
            occupationListModel.addElement(occupation.getText());
        }
    }

    // MODIFIES: this
    // EFFECTS: based on what is being filtered filters
    @SuppressWarnings("methodlength")
    private void confirmFilter() {
        List<Responder> beforeList = currentPoll.getFilteredResponders();
        if (filteringAge) {
            try {
                filterAge();
            } catch (Exception numberFormatException) {
                doReload = false;
                return;
            }
        } else if (filteringIncome) {
            try {
                filterIncome();
            } catch (Exception numberFormatException) {
                doReload = false;
                return;
            }
        } else if (filteringOccupations) {
            filterOccupations();
        } else if (filteringGender) {
            filterGender();
        }
        int after = currentPoll.getFilteredResponders().size();
        if (after == 0) {
            currentPoll.setFilteredPollResponders(beforeList);
            occupationListModel.clear();
        } else {
            hasFiltered = true;
        }
    }

    // MODIFIES: this
    // EFFECTS: filters the responders by gender
    private void filterGender() {
        int genderId = filterGenderBox.getSelectedIndex() + 1;
        currentPoll.respondersOfGenders(genderId);
        filteringGender = false;
    }

    // MODIFIES: this
    // EFFECTS: filters the responders by occupations
    private void filterOccupations() {
        List<String> inputList = toOccupationStringList();
        currentPoll.respondersOfOccupations(inputList);
        filteringOccupations = false;
    }

    // MODIFIES: this
    // EFFECTS: filters the responders by income
    private void filterIncome() {
        int lower = Integer.parseInt(((JTextField) interval.get(0)).getText());
        int upper = Integer.parseInt(((JTextField) interval.get(1)).getText());
        if (lower <= upper && lower >= 0) {
            currentPoll.respondersWithinIncomeInterval(lower, upper);
            filteringIncome = false;
        } else {
            doReload = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: filters the responders by age
    private void filterAge() {
        int lower = Integer.parseInt(((JTextField) interval.get(0)).getText());
        int upper = Integer.parseInt(((JTextField) interval.get(1)).getText());
        if (lower <= upper && lower > 0) {
            currentPoll.respondersWithinAgeInterval(lower, upper);
            filteringAge = false;
        } else {
            doReload = false;
        }
    }

    // EFFECTS: converts the contents of occupationListModel into a List<String>
    private List<String> toOccupationStringList() {
        int max = occupationListModel.getSize() - 1;
        List<String> output = new ArrayList<>();
        for (int count = 0; count <= max; count++) {
            output.add((String) occupationListModel.getElementAt(count));
        }
        return output;
    }

    // MODIFIES: this
    // EFFECTS: handles events that occur in manage responders tab
    private void responderActionHandle(ActionEvent e) {
        if ("2confirm".equals(e.getActionCommand())) {
            handleResponder();
            if (responderEntryMode) {
                responderEntryMode = false;
                currentResponderIndex = currentPoll.getPollResponders().size() - 1;
            }
            responderEditMode = false;
        } else if ("2remove".equals(e.getActionCommand())) {
            removeResponse();
        } else if ("2prev".equals(e.getActionCommand())) {
            currentResponderIndex--;
        } else if ("2next".equals(e.getActionCommand())) {
            currentResponderIndex++;
        } else if ("2del".equals(e.getActionCommand())) {
            currentPoll.removeResponder(currentResponderIndex);
            if (currentResponderIndex >= currentPoll.getPollResponders().size()) {
                currentResponderIndex--;
            }
        } else if ("2add".equals(e.getActionCommand())) {
            responderEntryMode = true;
        } else if ("2edit".equals(e.getActionCommand())) {
            responderEditMode = true;
        }
    }

    // MODIFIES: this
    // EFFECTS: removes current response from the list
    private void removeResponse() {
        try {
            Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
            int index = responseList.getSelectedIndex();
            currentResponder.removeResponse(index);
        } catch (Exception exception) {
            doReload = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: if validAttributes() is true responder is added/edited otherwise
    // doReload set to false
    private void handleResponder() {
        if (validAttributes()) {
            addEditResponder();
        } else {
            doReload = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: if responderEditMode is true edits the current responder based on
    // responderAttributes otherwise adds a new responder based on
    // responderAttributes
    private void addEditResponder() {
        String firstName = ((JTextField) responderAttributes.get(0)).getText();
        String lastName = ((JTextField) responderAttributes.get(1)).getText();
        long phoneNumber = Long.parseLong(((JTextField) responderAttributes.get(2)).getText());
        String email = ((JTextField) responderAttributes.get(3)).getText();
        int age = Integer.parseInt(((JTextField) responderAttributes.get(4)).getText());
        int genderId = ((JComboBox) responderAttributes.get(5)).getSelectedIndex();
        int income = Integer.parseInt(((JTextField) responderAttributes.get(6)).getText());
        String occupation = ((JTextField) responderAttributes.get(7)).getText();
        if (responderEditMode && !pollResponderEntryMode) {
            editCurrentResponder(firstName, lastName, phoneNumber, email, age, genderId, income, occupation);

        } else {
            Responder newResponder = new Responder(firstName, lastName, phoneNumber, email, age, genderId + 1, income,
                    occupation);
            currentPoll.addResponder(newResponder);
            currentResponderIndex = currentPoll.getPollResponders().size() - 1;
        }
    }

    // MODIFIES: this
    // EFFECTS: edits current responder
    private void editCurrentResponder(String firstName, String lastName, long phoneNumber, String email, int age,
            int genderId, int income, String occupation) {
        Responder currentResponder = currentPoll.getPollResponders().get(currentResponderIndex);
        currentResponder.setFirstName(firstName);
        currentResponder.setLastName(lastName);
        currentResponder.setPhoneNumber(phoneNumber);
        currentResponder.setEmail(email);
        currentResponder.setAge(age);
        currentResponder.setGenderId(genderId + 1);
        currentResponder.setIncome(income);
        currentResponder.setOccupation(occupation);

    }

    // EFFECTS: returns true if responder attributes are all valid
    private boolean validAttributes() {
        boolean valid = false;
        try {
            valid = (validStrings() && validNumbers());
        } catch (Exception e) {
            valid = false;

        }
        return valid;
    }

    // EFFECTS: returns true if each Integer attribute is valid.
    private boolean validNumbers() {
        long phoneNumber = Long.parseLong(((JTextField) responderAttributes.get(2)).getText());
        boolean goodPhoneNumber = String.valueOf(phoneNumber).length() == 10;
        int age = Integer.parseInt(((JTextField) responderAttributes.get(4)).getText());
        boolean goodAge = age > 0;
        int income = Integer.parseInt(((JTextField) responderAttributes.get(6)).getText());
        boolean goodIncome = income >= 0;
        return goodPhoneNumber && goodAge && goodIncome;
    }

    // EFFECTS: returns true if each String attribute is valid.
    private boolean validStrings() {
        String email = ((JTextField) responderAttributes.get(3)).getText();
        boolean goodFirstName = !((JTextField) responderAttributes.get(0)).getText().equals("");
        boolean goodLastName = !((JTextField) responderAttributes.get(1)).getText().equals("");
        boolean goodOccupation = !((JTextField) responderAttributes.get(7)).getText().equals("");
        int length = email.length();
        boolean goodEmail = (email.contains("@") && (email.substring(length - 4, length).equals(".com")
                || email.substring(length - 3, length).equals(".ca")));
        return (goodFirstName && goodLastName && goodOccupation && goodEmail);
    }

    // MODIFIES: this
    // EFFECTS: handles events that occur in manage poll tab
    private void pollActionHandle(ActionEvent e) {
        if ("1input".equals(e.getActionCommand())) {
            addAnswer();
        } else if ("1firstq".equals(e.getActionCommand())) {
            handleQuestion(e);
        } else if ("1remove".equals(e.getActionCommand())) {
            removeAnswer();
        } else if ("1prev".equals(e.getActionCommand())) {
            currentQuestionIndex--;
        } else if ("1next".equals(e.getActionCommand())) {
            currentQuestionIndex++;
        } else if ("1del".equals(e.getActionCommand())) {
            currentPoll.removeQuestion(currentQuestionIndex);
            if (currentQuestionIndex >= currentPoll.getPollQuestions().size()) {
                currentQuestionIndex--;
                if (currentStatQuestionIndex >= currentPoll.getPollQuestions().size()) {
                    currentStatQuestionIndex--;
                }
            }
        } else if ("1add".equals(e.getActionCommand())) {
            questionEntryMode = true;
        } else if ("1edit".equals(e.getActionCommand())) {
            questionEditMode = true;
        }
    }

    // MODIFIES: this
    // EFFECTS:
    private void addAnswer() {
        String answerString = answer.getText();
        Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);

        if (!answerString.equals("") && !currentQuestion.getPollAnswers().contains(answerString)) {
            Toolkit.getDefaultToolkit().beep();
            currentQuestion.addAnswer(answerString);
        }
    }

    // MODIFIES: this
    // EFFECTS: removes current answer from the list
    private void removeAnswer() {
        try {
            Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);
            int index = answerList.getSelectedIndex();
            currentQuestion.removeAnswer(index);
        } catch (Exception exception) {
            doReload = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: reloads the gui
    private void reload() {
        frame.remove(tabbedPane);
        generatePanes();
        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: handles events that occur in main menu tab
    public void menuActionHandle(ActionEvent e) {
        if ("0save".equals(e.getActionCommand())) {
            savePoll();
        } else if ("0load".equals(e.getActionCommand())) {
            loadPoll();
        } else if ("0name".equals(e.getActionCommand())) {
            editPoll(e);
        } else if ("0changename".equals(e.getActionCommand())) {
            editPollName = true;
        } else if ("0run".equals(e.getActionCommand())) {
            answeringResponder = null;
            runPoll = true;
        } else if ("0select".equals(e.getActionCommand())) {
            sellectResponder();
        } else if ("0add".equals(e.getActionCommand())) {
            pollResponderEntryMode = true;
        } else if ("0confirm".equals(e.getActionCommand())) {
            confirmNewPollResponder();
        } else if ("0confirmAnswer".equals(e.getActionCommand())) {
            responderAnswered();
        }
    }

    // MODIFIES: this
    // EFFECTS: creates new poll responder
    private void confirmNewPollResponder() {
        handleResponder();
        int max = currentPoll.getPollResponders().size() - 1;
        answeringResponder = currentPoll.getPollResponders().get(max);
        pollResponderEntryMode = false;
    }

    // MODIFIES: this
    // EFFECST: sets the chosen responder as the answering responder for the poll
    private void sellectResponder() {
        try {
            answeringResponder = currentPoll.getPollResponders().get(responderList.getSelectedIndex());
        } catch (Exception exception) {
            doReload = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: logs the responders response as a new response and advances the
    // current poll question by 1
    private void responderAnswered() {
        try {
            int responderIndex = currentPoll.getPollResponders().indexOf(answeringResponder);
            int index = pollAnswerList.getSelectedIndex();
            Question currentQuestion = currentPoll.getPollQuestions().get(currentPollQuestionIndex);
            String chosenAnswer = currentQuestion.getPollAnswers().get(index);
            currentPoll.responderAnswered(responderIndex, currentQuestion, chosenAnswer);
            currentPollQuestionIndex++;
            if (currentPollQuestionIndex >= currentPoll.getPollQuestions().size()) {
                currentPollQuestionIndex = 0;
                runPoll = false;
            }
        } catch (Exception exception) {
            doReload = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: changes the poll name based on input
    private void editPoll(ActionEvent e) {
        String newName = ((JTextField) e.getSource()).getText();
        currentPoll.setPollName(newName);
        editPollName = false;

    }

    // MODIFIES: this
    // EFFECTS: if question EditMode is true changes Question prompt, or if
    // (currentPoll.isNoDuplicate(source) && source.length() > 0) then makes new
    // question and adds it to Poll questions
    private void handleQuestion(ActionEvent e) {
        String questionPrompt = ((JTextField) e.getSource()).getText();
        if (questionEditMode) {
            Question currentQuestion = currentPoll.getPollQuestions().get(currentQuestionIndex);
            currentQuestion.setQuestion(questionPrompt);
            questionEditMode = false;
        } else if (currentPoll.isNoDuplicate(questionPrompt) && questionPrompt.length() > 0) {
            Question newQuestion = new Question(questionPrompt);
            currentPoll.addQuestion(newQuestion);
            currentQuestionIndex = currentPoll.getPollQuestions().size() - 1;
            questionEntryMode = false;
        } else {
            doReload = false;
        }
    }

    // MODIFIES: this
    // EFFECTS: Writes the currentPoll as a json file in SavedPoll.json
    private void savePoll() {
        try {
            jsonWriter.open();
            jsonWriter.write(this.currentPoll);
            jsonWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads Poll from SavedPoll.json and sets it as the currentPoll
    private void loadPoll() {
        try {
            currentPoll = jsonReader.readPoll();
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
        startUp();
        currentQuestionIndex = 0;
        currentResponderIndex = 0;
    }

    // required by extended classes
    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    @Override
    public void insertUpdate(DocumentEvent e) {

    }

    @Override
    public void removeUpdate(DocumentEvent e) {

    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {
        
    }

    //EFFECTS: prints log to terminal when poll manager window is closed
    @Override
    public void windowClosing(WindowEvent e) {
        LogPrinter lp = new LogPrinter();
        lp.printLog(EventLog.getInstance());
    }

    @Override
    public void windowClosed(WindowEvent e) {
        
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

}
