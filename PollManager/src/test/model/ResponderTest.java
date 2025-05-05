package model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class ResponderTest {
    private Responder testResponder;
    private Response testResponse1;
    private Response testResponse2;

    @BeforeEach
    void runBefore() {
        testResponder = new Responder("First test name", "Last test name",
                2266756278L, "TestEmail@test.com", 18, 1, 80000, "Tester");

    }

    @Test
    void testConstructor() {
        List<Response> emptyList = new ArrayList();
        assertEquals("First test name", testResponder.getFirstName());
        assertEquals("Last test name", testResponder.getLastName());
        assertEquals("226-675-6278", testResponder.getPhoneNumber());
        assertEquals("TestEmail@test.com", testResponder.getEmail());
        assertEquals(18, testResponder.getAge());
        assertEquals("Man", testResponder.getGender());
        assertEquals(80000, testResponder.getIncome());
        assertEquals("Tester", testResponder.getOccupation());
        assertEquals(emptyList, testResponder.getResponses());
    }

    @Test
    void testSetFirstNameOnce() {
        assertEquals("First test name", testResponder.getFirstName());
        testResponder.setFirstName("New First test name");
        assertEquals("New First test name", testResponder.getFirstName());
    }

    @Test
    void testSetFirstNameMultiple() {
        assertEquals("First test name", testResponder.getFirstName());
        testResponder.setFirstName("New First test name");
        assertEquals("New First test name", testResponder.getFirstName());
        testResponder.setFirstName("Middle First test name");
        testResponder.setFirstName("Final First test name");
        assertEquals("Final First test name", testResponder.getFirstName());
        testResponder.setFirstName("Final First test name");
        assertEquals("Final First test name", testResponder.getFirstName());
    }

    @Test
    void testSetLastNameOnce() {
        assertEquals("Last test name", testResponder.getLastName());
        testResponder.setFirstName("New Last test name");
        assertEquals("New Last test name", testResponder.getFirstName());
    }

    @Test
    void testSetLastNameMultiple() {
        assertEquals("Last test name", testResponder.getLastName());
        testResponder.setLastName("New Last test name");
        assertEquals("New Last test name", testResponder.getLastName());
        testResponder.setLastName("Middle Last test name");
        testResponder.setLastName("Final Last test name");
        assertEquals("Final Last test name", testResponder.getLastName());
        testResponder.setLastName("Final Last test name");
        assertEquals("Final Last test name", testResponder.getLastName());
    }

    @Test
    void testSetPhoneNumberOnce() {
        assertEquals("226-675-6278", testResponder.getPhoneNumber());
        assertEquals(2266756278L, testResponder.getRawPhoneNumber());
        testResponder.setPhoneNumber(2266756279L);
        assertEquals("226-675-6279", testResponder.getPhoneNumber());
        assertEquals(2266756279L, testResponder.getRawPhoneNumber());
    }

    @Test
    void testSetPhoneMultiple() {
        assertEquals("226-675-6278", testResponder.getPhoneNumber());
        testResponder.setPhoneNumber(2264535615L);
        assertEquals(2264535615L, testResponder.getRawPhoneNumber());
        assertEquals("226-453-5615", testResponder.getPhoneNumber());
        testResponder.setPhoneNumber(2266756277L);
        testResponder.setPhoneNumber(2266756276L);
        assertEquals("226-675-6276", testResponder.getPhoneNumber());
        assertEquals(2266756276L, testResponder.getRawPhoneNumber());
        testResponder.setPhoneNumber(2266756276L);
        assertEquals("226-675-6276", testResponder.getPhoneNumber());
        assertEquals(2266756276L, testResponder.getRawPhoneNumber());
    }

    @Test
    void testSetEmailOnce() {
        assertEquals("TestEmail@test.com", testResponder.getEmail());
        testResponder.setEmail("NewTestEmail@test.com");
        assertEquals("NewTestEmail@test.com", testResponder.getEmail());
    }

    @Test
    void testSetEmailMultiple() {
        assertEquals("TestEmail@test.com", testResponder.getEmail());
        testResponder.setEmail("NewTestEmail@test.com");
        assertEquals("NewTestEmail@test.com", testResponder.getEmail());
        testResponder.setEmail("MiddleTestEmail@test.com");
        testResponder.setEmail("FinalTestEmail@test.com");
        assertEquals("FinalTestEmail@test.com", testResponder.getEmail());
        testResponder.setEmail("FinalTestEmail@test.com");
        assertEquals("FinalTestEmail@test.com", testResponder.getEmail());
    }

    @Test
    void testSetAgeOnce() {
        assertEquals(18, testResponder.getAge());
        testResponder.setAge(1);
        assertEquals(1, testResponder.getAge());
    }

    @Test
    void testSetAgeMultiple() {
        assertEquals(18, testResponder.getAge());
        testResponder.setAge(1);
        assertEquals(1, testResponder.getAge());
        testResponder.setAge(6);
        testResponder.setAge(25);
        assertEquals(25, testResponder.getAge());
        testResponder.setAge(25);
        assertEquals(25, testResponder.getAge());
    }

    @Test
    void testSetGenderOnce() {
        assertEquals("Man", testResponder.getGender());
        testResponder.setGenderId(2);
        assertEquals(2, testResponder.getGenderId());
        assertEquals("Woman", testResponder.getGender());
    }

    @Test
    void testSetGenderMultiple() {
        assertEquals("Man", testResponder.getGender());
        testResponder.setGenderId(2);
        assertEquals(2, testResponder.getGenderId());
        assertEquals("Woman", testResponder.getGender());
        testResponder.setGenderId(1);
        testResponder.setGenderId(3);
        assertEquals(3, testResponder.getGenderId());
        assertEquals("Other", testResponder.getGender());
        testResponder.setGenderId(3);
        assertEquals(3, testResponder.getGenderId());
        assertEquals("Other", testResponder.getGender());
    }

    @Test
    void testSetIncomeOnce() {
        assertEquals(80000, testResponder.getIncome());
        testResponder.setIncome(7000);
        assertEquals(7000, testResponder.getIncome());
    }

    @SuppressWarnings("deprecation")
    @Test
    void testSetIncomeMultiple() {
        assertEquals(80000, testResponder.getIncome());
        testResponder.setIncome(7000);
        assertEquals(7000, testResponder.getIncome());
        testResponder.setIncome(8000);
        testResponder.setIncome(9500);
        assertEquals(9500, testResponder.getIncome());
        testResponder.setIncome(9500);
        assertEquals(9500, testResponder.getIncome());
    }

    @Test
    void testSetOccupation(){
        assertEquals("Tester", testResponder.getOccupation());
        testResponder.setOccupation("Senior Tester");
        assertEquals("Senior Tester", testResponder.getOccupation());
        testResponder.setOccupation("Junior Tester");
        testResponder.setOccupation("Lead Tester");
        assertEquals("Lead Tester", testResponder.getOccupation());
        testResponder.setOccupation("Lead Tester");
        assertEquals("Lead Tester", testResponder.getOccupation());
    }

    @Test
    void testUpdateAnswerOnce() {
        List<Response> emptyList = new ArrayList();
        Question testQuestion1 = new Question("Test Question 1");
        testQuestion1.addAnswer("Test Answer 1");
        Question testQuestion2 = new Question("Test Question 2");
        testQuestion2.addAnswer("Test Answer 2");
        testResponse1 = new Response(testQuestion1, "Test Answer 1");
        testResponse2 = new Response(testQuestion2, "Test Answer 2");
        assertEquals(emptyList, testResponder.getResponses());
        testResponder.addResponse(testResponse1);
        assertEquals(testResponse1, testResponder.getResponses().get(0));
        testQuestion1.addAnswer("Test Answer 2");
        testResponder.updateAnswer(0, "Test Answer 2");
        assertEquals(testResponse1, testResponder.getResponses().get(0));
        assertEquals("Test Answer 2", testResponder.getResponses().get(0).getResponseAnswer());
    }

    @Test
    void testUpdateAnswerMultiple() {
        List<Response> emptyList = new ArrayList();
        Question testQuestion1 = new Question("Test Question 1");
        testQuestion1.addAnswer("Test Answer 1");
        Question testQuestion2 = new Question("Test Question 2");
        testQuestion2.addAnswer("Test Answer 2");
        testResponse1 = new Response(testQuestion1, "Test Answer 1");
        testResponse2 = new Response(testQuestion2, "Test Answer 2");
        assertEquals(emptyList, testResponder.getResponses());
        testResponder.addResponse(testResponse1);
        assertEquals(testResponse1, testResponder.getResponses().get(0));
        testQuestion1.addAnswer("Test Answer 2");
        testQuestion1.addAnswer("Test Answer 1");
        testResponder.updateAnswer(0, "Test Answer 2");
        testResponder.updateAnswer(0, "Test Answer 1");
        assertEquals(testResponse1, testResponder.getResponses().get(0));
        assertEquals("Test Answer 1", testResponder.getResponses().get(0).getResponseAnswer());

    }

    @Test
    void testRemoveResponseOnce() {
        List<Response> emptyList = new ArrayList();
        Question testQuestion1 = new Question("Test Question 1");
        testQuestion1.addAnswer("Test Answer 1");
        testResponse1 = new Response(testQuestion1, "Test Answer 1");
        assertEquals(emptyList, testResponder.getResponses());
        testResponder.addResponse(testResponse1);
        assertEquals(1, testResponder.getResponses().size());
        assertEquals(testResponse1, testResponder.getResponses().get(0));
        testResponder.removeResponse(0);
        assertEquals(0, testResponder.getResponses().size());
    }

    @Test
    void testRemoveResponseMultiple() {
        List<Response> emptyList = new ArrayList();
        Question testQuestion1 = new Question("Test Question 1");
        testQuestion1.addAnswer("Test Answer 1");
        Question testQuestion2 = new Question("Test Question 2");
        testQuestion2.addAnswer("Test Answer 2");
        testResponse1 = new Response(testQuestion1, "Test Answer 1");
        testResponse2 = new Response(testQuestion2, "Test Answer 2");
        assertEquals(emptyList, testResponder.getResponses());
        testResponder.addResponse(testResponse1);
        assertEquals(1, testResponder.getResponses().size());
        assertEquals(testResponse1, testResponder.getResponses().get(0));
        testResponder.removeResponse(0);
        assertEquals(0, testResponder.getResponses().size());
        testResponder.addResponse(testResponse1);
        testResponder.addResponse(testResponse2);
        assertEquals(2, testResponder.getResponses().size());
        assertEquals(testResponse1, testResponder.getResponses().get(0));
        assertEquals(testResponse2, testResponder.getResponses().get(1));
        testResponder.removeResponse(1);
        assertEquals(testResponse1, testResponder.getResponses().get(0));
        testResponder.removeResponse(0);
        assertEquals(0, testResponder.getResponses().size());
    }

    @Test
    void testAddResponseOnce() {
        Question testQuestion1 = new Question("Test Question 1");
        testResponse1 = new Response(testQuestion1, "Test Answer 1");
        testResponder.addResponse(testResponse1);
        assertEquals(1, testResponder.getResponses().size());
        assertEquals(testResponse1, testResponder.getResponses().get(0));
    }

    @Test
    void testAddResponseMultiple() {
        Question testQuestion1 = new Question("Test Question 1");
        testQuestion1.addAnswer("Test Answer 1");
        Question testQuestion2 = new Question("Test Question 2");
        testQuestion2.addAnswer("Test Answer 2");
        testResponse1 = new Response(testQuestion1, "Test Answer 1");
        testResponse2 = new Response(testQuestion2, "Test Answer 2");
        testResponder.addResponse(testResponse1);
        testResponder.addResponse(testResponse2);
        assertEquals(2, testResponder.getResponses().size());
        assertEquals(testResponse1, testResponder.getResponses().get(0));
        assertEquals(testResponse2, testResponder.getResponses().get(1));
    }

    @Test
    void testClearResponse() {
        Question testQuestion1 = new Question("Test Question 1");
        testQuestion1.addAnswer("Test Answer 1");
        Question testQuestion2 = new Question("Test Question 2");
        testQuestion2.addAnswer("Test Answer 2");
        testResponse1 = new Response(testQuestion1, "Test Answer 1");
        testResponse2 = new Response(testQuestion2, "Test Answer 2");
        testResponder.addResponse(testResponse1);
        testResponder.addResponse(testResponse2);
        assertEquals(2, testResponder.getResponses().size());
        testResponder.clearResponses();
        assertEquals(0, testResponder.getResponses().size());
    }

}
