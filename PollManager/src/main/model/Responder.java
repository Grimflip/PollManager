package model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import persistence.Writable;

// A representation of a poll responder with: 
// first and last name, phone number, email, age, gender, income, 
// and occupation and responder answers to all poll questions.
public class Responder implements Writable {
    private String firstName;
    private String lastName;
    private long phoneNumber;
    private String responderEmail;
    private int age;
    private int genderId; // 1=man, 2=woman, 3=other
    private int income;
    private String occupation;
    private List<Response> responses;

    // EFFECTS: constructs a responder with first and last name phone number, email,
    // age, gender, income and occupation,
    // and occupation type and responder answers to all poll questions.
    public Responder(String firstName, String lastName, long number,
            String email, int age, int gender, int income, String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = number;
        this.responderEmail = email;
        this.age = age;
        this.genderId = gender;
        this.income = income;
        this.occupation = occupation;
        this.responses = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: sets newName as the first name of responder
    public void setFirstName(String newName) {
        if (!this.firstName.equals(newName)) {
            String prevName = this.firstName;
            this.firstName = newName;
            EventLog.getInstance().logEvent(
                    new Event("Responder first name updated from " + prevName + " to " + this.firstName));
        }
    }

    // MODIFIES: this
    // EFFECTS: sets newName as the last name of responder
    public void setLastName(String newName) {
        if (!this.lastName.equals(newName)) {
            String prevName = this.lastName;
            this.lastName = newName;
            EventLog.getInstance().logEvent(
                    new Event("Responder: " + this.firstName + " last name updated from " + prevName + " to "
                            + this.lastName));
        }
    }

    // REQUIRES: number of digits = 10
    // MODIFIES: this
    // EFFECTS: sets newNumber as phone number of responder
    public void setPhoneNumber(long newNumber) {
        if (this.phoneNumber != newNumber) {
            this.phoneNumber = newNumber;
            EventLog.getInstance().logEvent(
                    new Event("Responder: " + firstName + " " + lastName + " phone number updated"));
        }
    }

    // MODIFIES: this
    // EFFECTS: sets newEmail as the email of responder
    public void setEmail(String newEmail) {
        if (!this.responderEmail.equals(newEmail)) {
            this.responderEmail = newEmail;
            EventLog.getInstance().logEvent(
                    new Event("Responder: " + firstName + " " + lastName + " email updated"));
        }
    }

    // REQUIRES: newAge > 0
    // MODIFIES: this
    // EFFECTS: sets newAge as age of responder
    public void setAge(int newAge) {
        if (this.age != newAge) {
            this.age = newAge;
            EventLog.getInstance().logEvent(
                    new Event("Responder: " + firstName + " " + lastName + " age updated"));
        }
    }

    // REQUIRES: 1 <= newGend <= 3
    // MODIFIES: this
    // EFFECTS: sets newGend as the gender of this responder
    public void setGenderId(int newId) {
        if (this.genderId != newId) {
            this.genderId = newId;
            EventLog.getInstance().logEvent(
                    new Event("Responder: " + firstName + " " + lastName + " gender id updated"));
        }
    }

    // REQUIRES: newIncome >= 0
    // MODIFIES: this
    // EFFECTS: sets newIncome as the income of responder
    public void setIncome(int newIncome) {
        if (this.income != newIncome) {
            this.income = newIncome;
            EventLog.getInstance().logEvent(
                    new Event("Responder: " + firstName + " " + lastName + " income updated"));
        }
    }

    // MODIFIES: this
    // EFFECTS: sets newOccupation as the occupation of responder
    public void setOccupation(String newOccupation) {
        if (!this.occupation.equals(newOccupation)) {
            this.occupation = newOccupation;
            EventLog.getInstance().logEvent(
                    new Event("Responder: " + firstName + " " + lastName + " occupation updated"));
        }
    }

    // REQUIRES: a valid index number for responder responses
    // && new response must be an answer option for the question chosen by the index
    // MODIFIES: this
    // EFFECTS: updates chosen response with a new chosen answer
    public void updateAnswer(int chosenAnswerIndex, String newResponse) {
        this.responses.get(chosenAnswerIndex).setResponseAnswer(newResponse);
    }

    // REQUIRES: a valid index number for responder responses
    // MODIFIES: this
    // EFFECTS: removes chosen response that matches index and removes it
    public void removeResponse(int chosenAnswerIndex) {
        Response removedResponse = this.responses.remove(chosenAnswerIndex);
        EventLog.getInstance().logEvent(
            new Event("Removed response for question: " + removedResponse.getResponseQuestion().getPollQuestion()
                    + " for responder: " + firstName + " " + lastName));
    }   

    // MODIFIES: this
    // EFFECTS: adds the new Response to the responses list
    public void addResponse(Response newResponse) {
        this.responses.add(newResponse);
       
    }

    // MODIFIES: this
    // EFFECTS: clears the responses list
    public void clearResponses() {
        this.responses.clear();
    }

    @Override
    // EFFECTS: Returns this a json file
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("first name", this.firstName);
        json.put("last name", this.lastName);
        json.put("phone number", this.phoneNumber);
        json.put("email", this.responderEmail);
        json.put("age", this.age);
        json.put("gender id", this.genderId);
        json.put("income", this.income);
        json.put("occupation", this.occupation);
        json.put("responses", responsesToJson());
        return json;
    }

    // EFFECTS: returns this as a json array
    private JSONArray responsesToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Response r : this.responses) {
            jsonArray.put(r.toJson());
        }

        return jsonArray;
    }

    // getters

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public long getRawPhoneNumber() {
        return this.phoneNumber;
    }

    public String getPhoneNumber() {
        String stringNumber = Long.toString(this.phoneNumber);
        String partOne = stringNumber.substring(0, 3);
        String partTwo = stringNumber.substring(3, 6);
        String partTre = stringNumber.substring(6, 10);
        return (partOne + "-" + partTwo + "-" + partTre);
    }

    public String getEmail() {
        return this.responderEmail;
    }

    public int getAge() {
        return this.age;
    }

    public String getGender() {
        switch (this.genderId) {
            case 1:
                return "Man";
            case 2:
                return "Woman";
            default:
                return "Other";
        }
    }

    public int getGenderId() {
        return this.genderId;
    }

    public int getIncome() {
        return this.income;
    }

    public String getOccupation() {
        return this.occupation;
    }

    public List<Response> getResponses() {
        return this.responses;
    }

}
