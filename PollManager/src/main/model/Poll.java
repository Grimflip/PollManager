package model;

import persistence.Writable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

// A representation of a poll with a name, a list of questions and a list of responders
public class Poll implements Writable {
    private String pollName;
    private List<Question> pollQuestions;
    private List<Responder> pollResponders;
    private List<Responder> filteredPollResponders;

    // EFFECTS: constructs a poll with an empty list of poll question, an empty list
    // of poll responses, an empty filtered list of poll respondants and a name
    public Poll(String name) {
        this.pollName = name;
        this.pollQuestions = new ArrayList<>();
        this.pollResponders = new ArrayList<>();
        this.filteredPollResponders = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: sets newname as name of the poll
    public void setPollName(String newName) {
        EventLog.getInstance().logEvent(
                new Event("Poll name: " + this.pollName + " changed to: " + newName));
        this.pollName = newName;
    }

    // MODIFIES: this
    // EFFECTS: adds newQuestion to pollQuestions
    public void addQuestion(Question newQuestion) {
        this.pollQuestions.add(newQuestion);
        EventLog.getInstance().logEvent(
                new Event("Added new question with prompt: " + newQuestion.getPollQuestion()));
    }

    // REQUIRES: chosenQuestionIndex must be valid index for pollQuestions
    // MODIFIES: this
    // EFFECTS: removes the chosenQuestionIndex associated question from
    // pollQuestions
    public void removeQuestion(int chosenQuestionIndex) {
        Question removedQuestion = this.pollQuestions.remove(chosenQuestionIndex);
        EventLog.getInstance().logEvent(
                new Event("Removed question with prompt: " + removedQuestion.getPollQuestion()));
        this.removeResponsesContaining(removedQuestion);
    }

    // REQUIRES: chosenQuestionIndex must be valid index for pollQuestions
    // MODIFIES: this
    // EFFECTS: sets the chosenQuestionIndex associated question presented question
    // to newPresQuestion
    public void setPresentedQuestion(int chosenQuestionIndex, String newPresQuestion) {
        this.pollQuestions.get(chosenQuestionIndex).setQuestion(newPresQuestion);
    }

    // REQUIRES: chosenQuestionIndex must be valid index for pollQuestions
    // MODIFIES: this
    // EFFECTS: adds newQuestionAnswer to the the chosenQuestionIndex associated
    // question's list of answers
    public void addQuestionAnswer(int chosenQuestionIndex, String newQuestionAnswer) {
        Question chosenQuestion = this.pollQuestions.get(chosenQuestionIndex);
        chosenQuestion.addAnswer(newQuestionAnswer);
    }

    // MODIFIES: this
    // EFFECTS: adds the newResponder to pollResponders
    public void addResponder(Responder newResponder) {
        this.pollResponders.add(newResponder);
        EventLog.getInstance().logEvent(
                new Event("Added responder: " + newResponder.getFirstName() + " " + newResponder.getLastName()));
    }

    // REQUIRES: chosenResponderIndex must be valid index for pollResponders
    // MODIFIES: this
    // EFFECTS: removes the chosenResponderIndex from pollResponders
    public void removeResponder(int chosenResponderIndex) {
        Responder removedResponder = this.pollResponders.remove(chosenResponderIndex);
        EventLog.getInstance().logEvent(
                new Event("Removed responder: " + removedResponder.getFirstName() + " "
                        + removedResponder.getLastName()));
    }

    // REQUIRES: 8 >= attributeId >= 1
    // && newAttribute must be valid parameter type for the Responder attribute set
    // method
    // && chosenResponderIndex must be valid index for pollResponders
    // MODIFIES: this
    // EFFECTS:
    // attributeId=1 -> set newAttribute as chosenResponderIndex associated
    // Responder first name
    // attributeId=2 -> set newAttribute as chosenResponderIndex associated
    // Responder last name
    // attributeId=3 -> set newAttribute as chosenResponderIndex associated
    // Responder phone number
    // attributeId=4 -> set newAttribute as chosenResponderIndex associated
    // Responder email
    // attributeId=5 -> set newAttribute as chosenResponderIndex associated
    // Responder age
    // attributeId=6 -> set newAttribute as chosenResponderIndex associated
    // Responder genderId
    // attributeId=7 -> set newAttribute as chosenResponderIndex associated
    // Responder income
    // attributeId=8 -> set newAttribute as chosenResponderIndex associated
    // Responder occupation
    @SuppressWarnings("methodlength")
    public void setResponderAttribute(int chosenResponderIndex, int attributeId, Object newAttribute) {
        switch (attributeId) {
            case 1:
                this.pollResponders.get(chosenResponderIndex).setFirstName((String) newAttribute);
                break;
            case 2:
                this.pollResponders.get(chosenResponderIndex).setLastName((String) newAttribute);
                break;
            case 3:
                this.pollResponders.get(chosenResponderIndex).setPhoneNumber((long) newAttribute);
                break;
            case 4:
                this.pollResponders.get(chosenResponderIndex).setEmail((String) newAttribute);
                break;
            case 5:
                this.pollResponders.get(chosenResponderIndex).setAge((int) newAttribute);
                break;
            case 6:
                this.pollResponders.get(chosenResponderIndex).setGenderId((int) newAttribute);
                break;
            case 7:
                this.pollResponders.get(chosenResponderIndex).setIncome((int) newAttribute);
                break;
            default:
                this.pollResponders.get(chosenResponderIndex).setOccupation((String) newAttribute);
                break;
        }
    }

    // REQUIRES: chosenAnswer must be contained in answeredQuestion.getPollAnswers()
    // && chosenResponderIndex must be valid index for pollResponders
    // MODIFIES: this
    // EFFECTS: Updates chosenResponder's list of responses with a new response,
    // if the answered question already has a response containing it the
    // chosenAnswer will overwrite the current answer
    public void responderAnswered(int chosenResponderIndex, Question answeredQuestion, String chosenAnswer) {
        boolean isDone = false;
        Responder currentResponder = this.pollResponders.get(chosenResponderIndex);
        for (Response response : currentResponder.getResponses()) {
            if (answeredQuestion.equals(response.getResponseQuestion())) {
                response.setResponseAnswer(chosenAnswer);
                isDone = true;
                EventLog.getInstance().logEvent(
                        new Event("Responder: " + currentResponder.getFirstName() + " " + currentResponder.getLastName()
                            + " updated their answer to: \"" + chosenAnswer + "\" for the question: "
                            + answeredQuestion.getPollQuestion()));
                break;
            }
        }
        if (!isDone) {
            Response newResponse = new Response(answeredQuestion, chosenAnswer);
            currentResponder.addResponse(newResponse);
            EventLog.getInstance().logEvent(
                    new Event("Responder: " + currentResponder.getFirstName() + " " + currentResponder.getLastName()
                            + " chose answer: \"" + chosenAnswer + "\" to the question: "
                            + answeredQuestion.getPollQuestion()));
        }
    }

    // REQUIRES: higher >= lower >= 0
    // EFFECTS: returns responders whose income are within the interval of lower and
    // higher
    public void respondersWithinIncomeInterval(int lower, int higher) {
        List<Responder> outputList = new ArrayList<>();
        List<Responder> respondersToFilter = new ArrayList<>();
        if (this.getFilteredResponders().equals(outputList)) {
            respondersToFilter = this.getPollResponders();
        } else {
            respondersToFilter = this.getFilteredResponders();
        }

        for (Responder responder : respondersToFilter) {
            if (lower <= responder.getIncome() && responder.getIncome() <= higher) {
                outputList.add(responder);
            }
        }
        this.filteredPollResponders = outputList;
        EventLog.getInstance().logEvent(
                        new Event("Income interval filter applied"));
    }

    // REQUIRES: higher >= lower >= 0
    // EFFECTS: if filteredPollResponders isn't null, filters the filtered list to
    // keep Responders within the age interval.
    // if null it filters the PollResponders list to keep Responders within the age
    // interval and sets it as filtered list
    public void respondersWithinAgeInterval(int lower, int higher) {
        List<Responder> outputList = new ArrayList<>();
        List<Responder> respondersToFilter = new ArrayList<>();
        if (this.getFilteredResponders().equals(outputList)) {
            respondersToFilter = this.getPollResponders();
        } else {
            respondersToFilter = this.getFilteredResponders();
        }
        for (Responder responder : respondersToFilter) {
            if (lower <= responder.getAge() && responder.getAge() <= higher) {
                outputList.add(responder);
            }
        }
        this.filteredPollResponders = outputList;
        EventLog.getInstance().logEvent(
                        new Event("Age interval filter applied"));
    }

    // REQUIRES: chosenGender <= 3
    // EFFECTS: if filteredPollResponders isn't null, filters it to keep Responders
    // with chosenGenderId.
    // if null it filters the PollResponders list to keep Responders with
    // chosenGenderIdand sets it as filtered list
    public void respondersOfGenders(int chosenGenderId) {
        List<Responder> outputList = new ArrayList<>();
        List<Responder> respondersToFilter = new ArrayList<>();
        if (this.getFilteredResponders().equals(outputList)) {
            respondersToFilter = this.getPollResponders();
        } else {
            respondersToFilter = this.getFilteredResponders();
        }
        String gender = "Other";
        switch (chosenGenderId) {
            case 1:
                gender = "Man";
                break;
            case 2:
                gender = "Woman";
            default:
                break;
        }
        for (Responder responder : respondersToFilter) {
            if (responder.getGender().equals(gender)) {
                outputList.add(responder);
            }
        }
        this.filteredPollResponders = outputList;
        EventLog.getInstance().logEvent(
                        new Event("Gender Id filter applied"));
        
    }

    // REQUIRES: listOccupations.size() >= 1
    // EFFECTS: returns responders whose occupation is contained in listOccupations
    public void respondersOfOccupations(List<String> listOccupations) {
        List<Responder> outputList = new ArrayList<>();
        List<Responder> respondersToFilter = new ArrayList<>();
        if (this.getFilteredResponders().equals(outputList)) {
            respondersToFilter = this.getPollResponders();
        } else {
            respondersToFilter = this.getFilteredResponders();
        }
        for (Responder responder : respondersToFilter) {
            if (listOccupations.contains(responder.getOccupation())) {
                outputList.add(responder);
            }
        }
        this.filteredPollResponders = outputList;
        EventLog.getInstance().logEvent(
                        new Event("Occupations filter applied"));
    }

    // EFFECTS: return response answers of chosen index question whos answer is one
    // of the options in chosen question index from poll
    // responders
    public List<String> extractResponderAnswers(int chosenQuestionIndex) {
        List<String> outputList = new ArrayList<>();
        List<Responder> emptyList = new ArrayList<>();
        List<Responder> respondersToExtract = new ArrayList<>();
        Question currentQuestion = this.getPollQuestions().get(chosenQuestionIndex);
        if (this.getFilteredResponders().equals(emptyList)) {
            respondersToExtract = this.getPollResponders();
        } else {
            respondersToExtract = this.getFilteredResponders();
        }
        for (Responder responder : respondersToExtract) {
            for (Response response : responder.getResponses()) {
                if (response.getResponseQuestion().equals(currentQuestion)
                        && currentQuestion.getPollAnswers().contains(response.getResponseAnswer())) {
                    outputList.add(response.getResponseAnswer());
                }
            }
        }
        return outputList;
    }

    // MODIFIES: this
    // EFFECTS: clears filtered response list
    public void clearFilteredResponders() {
        this.filteredPollResponders.clear();
        EventLog.getInstance().logEvent(
                        new Event("Filters cleared"));
    }

    // EFFECTS: returns true if String is not a question prompt in poll Questions
    public boolean isNoDuplicate(String possiblePrompt) {
        for (Question currentQuestion : this.getPollQuestions()) {
            if (currentQuestion.getPollQuestion().equals(possiblePrompt)) {
                return false;
            }
        }
        return true;
    }

    // MODIFIES: this
    // EFFECTS: removes all responses from poll responders that have removedQuestion
    // as answered question
    private void removeResponsesContaining(Question removedQuestion) {
        for (Responder responder : this.getPollResponders()) {
            int currentIndex = 0;
            List<Response> initialList = responder.getResponses();
            List<Integer> indexList = new ArrayList<>();
            for (Response response : initialList) {
                if (response.getResponseQuestion().equals(removedQuestion)) {
                    indexList.add(currentIndex);
                }
                currentIndex++;
            }
            for (int index : indexList) {
                responder.removeResponse(index);
            }
        }
    }

    // EFFECTS: Returns true if Poll Questions contains at least 1 question
    // AND each question contains at least 2 answers
    public boolean isReadyPoll() {
        return ((this.getPollQuestions().size() >= 1) && isMinTwo());
    }

    // Helper function for isReadyPoll
    // EFFECTS: returns true if every Poll Question has at least 2 answers, false
    // otherwise
    private boolean isMinTwo() {
        for (Question question : this.getPollQuestions()) {
            if (question.getPollAnswers().size() < 2) {
                return false;
            }
        }
        return true;
    }

    // MODIFIES: this
    // EFFECTS: sets newList as current list of filtered responders
    public void setFilteredPollResponders(List<Responder> newList) {
        this.filteredPollResponders = newList;
    }

    @Override
    // EFFECTS: converts this into Json File
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", this.pollName);
        json.put("questions", questionsToJson());
        json.put("responders", respondersToJson());
        return json;
    }

    // EFFECTS: returns the questions in this poll as a JSON array
    private JSONArray questionsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Question q : pollQuestions) {
            jsonArray.put(q.toJson());
        }

        return jsonArray;
    }

    // EFFECTS: returns the responders in this poll as a JSON array
    private JSONArray respondersToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Responder r : pollResponders) {
            jsonArray.put(r.toJson());
        }

        return jsonArray;
    }

    // getters

    public String getPollName() {
        return this.pollName;
    }

    public List<Question> getPollQuestions() {
        return this.pollQuestions;
    }

    public List<Responder> getPollResponders() {
        return this.pollResponders;
    }

    public List<Responder> getFilteredResponders() {
        return this.filteredPollResponders;
    }

}
