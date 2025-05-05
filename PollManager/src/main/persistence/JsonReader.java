package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Poll;
import model.Question;
import model.Responder;
import model.Response;

// a reader that reads and creates a poll like the one specified in the json file
// code based on CPSC 210 Json Serialization Starter
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads Poll from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Poll readPoll() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return extractPoll(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses Poll from JSON object and returns it
    private Poll extractPoll(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        Poll newPoll = new Poll(name);
        addQuestions(newPoll, jsonObject);
        addResponders(newPoll, jsonObject);
        return newPoll;
    }

    // MODIFIES: newPoll
    // EFFECTS: parses Questions from JSON object and adds them to newPoll
    private void addQuestions(Poll newPoll, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("questions");
        for (Object json : jsonArray) {
            JSONObject nextQuestion = (JSONObject) json;
            addQuestionToPoll(newPoll, nextQuestion);
        }
    }

    // MODIFIES: newPoll
    // EFFECTS: parses Question from JSON object and adds it to newPoll
    private void addQuestionToPoll(Poll newPoll, JSONObject jsonObject) {
        String prompt = jsonObject.getString("prompt");
        Question newQuestion = new Question(prompt);
        addAnswers(newQuestion, jsonObject);
        newPoll.addQuestion(newQuestion);
    }

    // MODIFIES: newQuestion
    // EFFECTS: parses an answer(String) from JSON object and adds it to newQuestion
    private void addAnswers(Question newQuestion, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("answers");
        for (Object json : jsonArray) {
            String nextAnswer = (String) json;
            newQuestion.addAnswer(nextAnswer);
        }
    }

    // MODIFIES: newPoll
    // EFFECTS: parses Responders from JSON object and adds them to newPoll
    private void addResponders(Poll newPoll, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("responders");
        for (Object json : jsonArray) {
            JSONObject nextResponder = (JSONObject) json;
            addResponder(newPoll, nextResponder);
        }
    }

    // MODIFIES: newPoll
    // EFFECTS: parses Responder from JSON object and adds it to newPoll
    private void addResponder(Poll newPoll, JSONObject jsonObject) {
        String firstName = jsonObject.getString("first name");
        String lastName = jsonObject.getString("last name");
        long phoneNumber = jsonObject.getLong("phone number");
        String email = jsonObject.getString("email");
        int age = jsonObject.getInt("age");
        int genderId = jsonObject.getInt("gender id");
        int income = jsonObject.getInt("income");
        String occupatoin = jsonObject.getString("occupation");
        Responder newResponder = new Responder(firstName, lastName, phoneNumber, email, age, genderId, income,
                occupatoin);
        addResponses(newPoll, newResponder, jsonObject);
        newPoll.addResponder(newResponder);
    }

    // MODIFIES: newResponder
    // EFFECTS: parses Responses from JSON object and adds THEM to newResponder
    private void addResponses(Poll newPoll, Responder newResponder, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("responses");
        for (Object json : jsonArray) {
            JSONObject nextResponse = (JSONObject) json;
            addResponse(newPoll, newResponder, nextResponse);
        }

    }

    // MODIFIES: newResponder
    // EFFECTS: parses response from JSON object and adds it to newResponder
    private void addResponse(Poll newPoll, Responder newResponder, JSONObject jsonObject) {
        String answer = jsonObject.getString("answer");
        JSONObject question = jsonObject.getJSONObject("responded question");
        Question respondedQuestion = getQuestion(newPoll, question);
        Response newResponse = new Response(respondedQuestion, answer);
        newResponder.addResponse(newResponse);
    }

    // EFFECTS returns the question from newPoll's pollQuestions that matches the
    // responses' responded question
    private Question getQuestion(Poll newPoll, JSONObject question) {
        String prompt = question.getString("prompt"); // each prompt is unique so there will be no mismatch
        Question output = null;
        for (Question currentQuestion : newPoll.getPollQuestions()) {
            if (currentQuestion.getPollQuestion().equals(prompt)) {
                output = currentQuestion;
            }
        }
        return output;
    }
}
