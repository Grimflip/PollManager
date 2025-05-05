package model;

import org.json.JSONObject;

import persistence.Writable;

// A represantation of a responders response with the question answered and the chosesn response
public class Response implements Writable {
    private Question respondedQuestion;
    private String respondedAnswer;

    // REQUIRES: response must be a valid answer of the question
    // EFFECTS: constructs a Response object with a question answered and the
    // chosesn response
    public Response(Question question, String response) {
        this.respondedQuestion = question;
        this.respondedAnswer = response;
    }

    // MODIFIES: this
    // EFFECTS: changes the responded question to the new question
    public void setResponseQuestion(Question newQuestion) {
        this.respondedQuestion = newQuestion;
    }

    // MODIFIES: this
    // EFFECTS: changes the question response to the new response
    public void setResponseAnswer(String newAnswer) {
        this.respondedAnswer = newAnswer;
    }

    @Override
    //EFFECTS: returns this as json file
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("responded question", this.respondedQuestion.toJson());
        json.put("answer", this.respondedAnswer);
        return json;
    }

    // getters

    public Question getResponseQuestion() {
        return this.respondedQuestion;
    }

    public String getResponseAnswer() {
        return this.respondedAnswer;
    }

}
