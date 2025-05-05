package model;

import persistence.Writable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

// A represantation of question of a poll with a presented question and a list of possible choosable answers
public class Question implements Writable {
    private String pollQuestion;
    private List<String> pollAnswers;

    // EFFECTS: Construct a Question object with a chosen question and an empty
    // answer List
    public Question(String pollQuestion) {
        this.pollQuestion = pollQuestion;
        this.pollAnswers = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: sets newQuestion as the poll question
    public void setQuestion(String newQuestion) {
        String oldPrompt = this.pollQuestion;
        this.pollQuestion = newQuestion;
        EventLog.getInstance().logEvent(
                new Event("Changed question with prompt: " + oldPrompt + " to new prompt: " + newQuestion));
    }

    // MODIFIES: this
    // EFFECTS: adds the new answer to the pollAnswers list
    public void addAnswer(String newAnswer) {
        this.pollAnswers.add(newAnswer);
        EventLog.getInstance().logEvent(
                new Event("Added answer: " + newAnswer + " to question with prompt: "
                        + this.pollQuestion));
    }

    // REQUIRES: this.pollAnswers.length().size() > 0 &&
    // (this.pollAnswers.length().size() -1) >= chosenAnswerIndex
    // MODIFIES: this
    // EFFECTS: removes the chosen index answer from list of answers
    public void removeAnswer(int chosenAnswerIndex) {
        String removedAnswer = this.pollAnswers.remove(chosenAnswerIndex);
        EventLog.getInstance().logEvent(
                new Event("Removed answer: " + removedAnswer + " from question with prompt: "
                        + this.pollQuestion));
    }

    // REQUIRES: this.pollAnswers.length().size() > 0 &&
    // (this.pollAnswers.length().size() -1) >= chosenAnswerIndex
    // MODIFIES: this
    // EFFECTS: changes the chosen index answer into changed answer in list of
    // answers
    public void modifyAnswer(int chosenAnswerIndex, String changedAnswer) {
        this.pollAnswers.set(chosenAnswerIndex, changedAnswer);
    }

    @Override
    // EFFECTS: returns this as Json file
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("prompt", this.pollQuestion);
        json.put("answers", answersToJson());

        return json;
    }

    // EFFECTS: returns this questions answers as Json Array
    private JSONArray answersToJson() {
        JSONArray jsonArray = new JSONArray();

        for (String s : pollAnswers) {
            jsonArray.put(s);
        }

        return jsonArray;
    }

    // GETTERS

    public String getPollQuestion() {
        return this.pollQuestion;
    }

    public List<String> getPollAnswers() {
        return this.pollAnswers;
    }

}
