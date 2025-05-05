package model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PollTest {
    private Poll testPoll;
    private Question testQuestion1;
    private Question testQuestion2;
    private Responder testResponder1;
    private Responder testResponder2;

    @BeforeEach
    void runBefore() {
        testPoll = new Poll("Test Poll");
        testQuestion1 = new Question("Test Question 1");
        testQuestion2 = new Question("Test Question 2");
        testResponder1 = new Responder("testFirst1", "TestLast1", 2264562459L, "Test1@Test1.com", 6, 1, 2000,
                "Test Occupation 1");
        testResponder2 = new Responder("testFirst2", "TestLast2", 2265682461L, "Test2@Test2.com", 75, 2, 2500,
                "Test Occupation 2");
    }

    @Test
    void testConstructor() {
        assertEquals("Test Poll", testPoll.getPollName());
        assertEquals(0, testPoll.getPollQuestions().size());
        assertEquals(0, testPoll.getPollResponders().size());
    }

    @Test
    void testSetPollNameOnce() {
        assertEquals("Test Poll", testPoll.getPollName());
        testPoll.setPollName("Test Poll New");
        assertEquals("Test Poll New", testPoll.getPollName());
    }

    @Test
    void testAddQuestionOnce() {
        assertEquals(0, testPoll.getPollQuestions().size());
        testPoll.addQuestion(testQuestion1);
        assertEquals(1, testPoll.getPollQuestions().size());
        assertEquals(testQuestion1, testPoll.getPollQuestions().get(0));
    }

    @Test
    void testAddQuestionMultiple() {
        assertEquals(0, testPoll.getPollQuestions().size());
        testPoll.addQuestion(testQuestion1);
        assertEquals(1, testPoll.getPollQuestions().size());
        assertEquals(testQuestion1, testPoll.getPollQuestions().get(0));
        testPoll.addQuestion(testQuestion1);
        testPoll.addQuestion(testQuestion2);
        assertEquals(3, testPoll.getPollQuestions().size());
        assertEquals(testQuestion1, testPoll.getPollQuestions().get(0));
        assertEquals(testQuestion1, testPoll.getPollQuestions().get(1));
        assertEquals(testQuestion2, testPoll.getPollQuestions().get(2));
    }

    @Test
    void testRemoveQuestionOnce() {
        testPoll.addResponder(testResponder1);
        testQuestion1.addAnswer("test answer 1");
        assertEquals(0, testPoll.getPollQuestions().size());
        testPoll.addQuestion(testQuestion1);
        testPoll.responderAnswered(0, testQuestion1, "test answer 1");
        assertEquals(1, testPoll.getPollResponders().get(0).getResponses().size());
        assertEquals(1, testPoll.getPollQuestions().size());
        assertEquals(testQuestion1, testPoll.getPollQuestions().get(0));
        testPoll.removeQuestion(0);
        assertEquals(0, testPoll.getPollQuestions().size());
        assertEquals(0, testPoll.getPollResponders().get(0).getResponses().size());
    }

    @Test
    void testRemoveQuestionMultiple() {
        testPoll.addResponder(testResponder1);
        testPoll.addResponder(testResponder2);
        assertEquals(0, testPoll.getPollQuestions().size());
        testPoll.addQuestion(testQuestion1);
        testQuestion1.addAnswer("test answer 1");
        testQuestion1.addAnswer("test answer 2");
        testQuestion2.addAnswer("test answer 1");
        testQuestion2.addAnswer("test answer 2");
        testPoll.responderAnswered(0, testQuestion1, "test answer 1");
        testPoll.responderAnswered(1, testQuestion1, "test answer 2");
        assertEquals(1, testPoll.getPollResponders().get(0).getResponses().size());
        assertEquals(1, testPoll.getPollResponders().get(1).getResponses().size());
        assertEquals(1, testPoll.getPollQuestions().size());
        assertEquals(testQuestion1, testPoll.getPollQuestions().get(0));
        testPoll.removeQuestion(0);
        assertEquals(0, testPoll.getPollResponders().get(0).getResponses().size());
        assertEquals(0, testPoll.getPollResponders().get(1).getResponses().size());
        assertEquals(0, testPoll.getPollQuestions().size());
        testPoll.addQuestion(testQuestion1);
        testPoll.addQuestion(testQuestion2);
        testPoll.responderAnswered(0, testQuestion1, "test answer 1");
        testPoll.responderAnswered(1, testQuestion1, "test answer 2");
        testPoll.responderAnswered(0, testQuestion2, "test answer 1");
        testPoll.responderAnswered(1, testQuestion2, "test answer 2");
        assertEquals(2, testPoll.getPollQuestions().size());
        assertEquals(2, testPoll.getPollResponders().get(0).getResponses().size());
        assertEquals(2, testPoll.getPollResponders().get(1).getResponses().size());
        assertEquals(testQuestion1, testPoll.getPollQuestions().get(0));
        assertEquals(testQuestion2, testPoll.getPollQuestions().get(1));
        testPoll.removeQuestion(1);
        assertEquals(1, testPoll.getPollQuestions().size());
        assertEquals(1, testPoll.getPollResponders().get(0).getResponses().size());
        assertEquals(1, testPoll.getPollResponders().get(1).getResponses().size());
        testPoll.removeQuestion(0);
        assertEquals(0, testPoll.getPollQuestions().size());
        assertEquals(0, testPoll.getPollResponders().get(0).getResponses().size());
        assertEquals(0, testPoll.getPollResponders().get(1).getResponses().size());
    }

    @Test
    void testSetPresentedQuestion() {
        testPoll.addQuestion(testQuestion1);
        testPoll.addQuestion(testQuestion2);
        assertEquals("Test Question 1", testPoll.getPollQuestions().get(0).getPollQuestion());
        testPoll.setPresentedQuestion(0, "New Test Question 1");
        assertEquals("New Test Question 1", testPoll.getPollQuestions().get(0).getPollQuestion());
        assertEquals("Test Question 2", testPoll.getPollQuestions().get(1).getPollQuestion());
        testPoll.setPresentedQuestion(1, "New Test Question 2");
        assertEquals("New Test Question 2", testPoll.getPollQuestions().get(1).getPollQuestion());
    }

    @Test
    void testAddQuestionAnswer() {
        testPoll.addQuestion(testQuestion1);
        testPoll.addQuestion(testQuestion2);
        assertEquals(0, testPoll.getPollQuestions().get(0).getPollAnswers().size());
        testPoll.addQuestionAnswer(0, "New Test Answer1-1");
        testPoll.addQuestionAnswer(1, "New Test Answer2-1");
        testPoll.addQuestionAnswer(1, "New Test Answer2-2");
        assertEquals(1, testPoll.getPollQuestions().get(0).getPollAnswers().size());
        assertEquals(2, testPoll.getPollQuestions().get(1).getPollAnswers().size());
        assertEquals("New Test Answer1-1", testPoll.getPollQuestions().get(0).getPollAnswers().get(0));
        assertEquals("New Test Answer2-1", testPoll.getPollQuestions().get(1).getPollAnswers().get(0));
        assertEquals("New Test Answer2-2", testPoll.getPollQuestions().get(1).getPollAnswers().get(1));
    }

    @Test
    void testAddResponder() {
        testPoll.addResponder(testResponder1);
        assertEquals(1, testPoll.getPollResponders().size());
        assertEquals(testResponder1, testPoll.getPollResponders().get(0));
        testPoll.addResponder(testResponder2);
        testPoll.addResponder(testResponder1);
        assertEquals(3, testPoll.getPollResponders().size());
        assertEquals(testResponder1, testPoll.getPollResponders().get(0));
        assertEquals(testResponder2, testPoll.getPollResponders().get(1));
        assertEquals(testResponder1, testPoll.getPollResponders().get(2));
    }

    @Test
    void testRemoveResponder() {
        testPoll.addResponder(testResponder1);
        assertEquals(1, testPoll.getPollResponders().size());
        assertEquals(testResponder1, testPoll.getPollResponders().get(0));
        testPoll.removeResponder(0);
        assertEquals(0, testPoll.getPollResponders().size());
        testPoll.addResponder(testResponder2);
        testPoll.addResponder(testResponder1);
        assertEquals(2, testPoll.getPollResponders().size());
        testPoll.removeResponder(1);
        assertEquals(1, testPoll.getPollResponders().size());
        assertEquals(testResponder2, testPoll.getPollResponders().get(0));
        testPoll.removeResponder(0);
        assertEquals(0, testPoll.getPollResponders().size());
    }

    @Test
    void testSetResponderAttribute() {
        testPoll.addResponder(testResponder2);
        testPoll.addResponder(testResponder1);
        assertEquals("testFirst1", testPoll.getPollResponders().get(1).getFirstName());
        assertEquals("TestLast1", testPoll.getPollResponders().get(1).getLastName());
        assertEquals("226-456-2459", testPoll.getPollResponders().get(1).getPhoneNumber());
        assertEquals("Test1@Test1.com", testPoll.getPollResponders().get(1).getEmail());
        assertEquals(6, testPoll.getPollResponders().get(1).getAge());
        assertEquals("Man", testPoll.getPollResponders().get(1).getGender());
        assertEquals(2000, testPoll.getPollResponders().get(1).getIncome());
        assertEquals("Test Occupation 1", testPoll.getPollResponders().get(1).getOccupation());
        testPoll.setResponderAttribute(1, 1, "New testFirst1"); // set first name
        testPoll.setResponderAttribute(0, 2, "New testLast1"); // set last name
        testPoll.setResponderAttribute(1, 3, 2164455671L); // set phone number
        testPoll.setResponderAttribute(1, 4, "NewTest1@Test1.com"); // set email
        testPoll.setResponderAttribute(0, 5, 20); // set age
        testPoll.setResponderAttribute(1, 6, 3); // set gender Id
        testPoll.setResponderAttribute(1, 7, 50000); // set income
        testPoll.setResponderAttribute(1, 8, "New Test1 Occupation"); // set occupation
        assertEquals("New testFirst1", testPoll.getPollResponders().get(1).getFirstName());
        assertEquals("New testLast1", testPoll.getPollResponders().get(0).getLastName());
        assertEquals("216-445-5671", testPoll.getPollResponders().get(1).getPhoneNumber());
        assertEquals("NewTest1@Test1.com", testPoll.getPollResponders().get(1).getEmail());
        assertEquals(20, testPoll.getPollResponders().get(0).getAge());
        assertEquals("Other", testPoll.getPollResponders().get(1).getGender());
        assertEquals(50000, testPoll.getPollResponders().get(1).getIncome());
        assertEquals("New Test1 Occupation", testPoll.getPollResponders().get(1).getOccupation());
    }

    @Test
    void testResponderAnswered() {
        testQuestion1.addAnswer("Test answer1");
        testQuestion1.addAnswer("Test answer2");
        testQuestion2.addAnswer("Other Test answer");
        testPoll.addQuestion(testQuestion1);
        testPoll.addQuestion(testQuestion2);
        testPoll.addResponder(testResponder1);
        testPoll.addResponder(testResponder2);
        testPoll.responderAnswered(0, testQuestion1, "Test answer1");
        testPoll.responderAnswered(1, testQuestion1, "Test answer1");
        assertEquals("Test answer1", testPoll.getPollResponders().get(0).getResponses().get(0).getResponseAnswer());
        assertEquals("Test answer1", testPoll.getPollResponders().get(1).getResponses().get(0).getResponseAnswer());
        testPoll.responderAnswered(1, testQuestion1, "Test answer2");
        testPoll.responderAnswered(1, testQuestion2, "Other Test answer");
        assertEquals("Test answer2", testPoll.getPollResponders().get(1).getResponses().get(0).getResponseAnswer());
        assertEquals("Other Test answer",
                testPoll.getPollResponders().get(1).getResponses().get(1).getResponseAnswer());
    }

    @Test
    void testResponsdersWithinIncomeInterval() {
        List<Responder> emptyList = new ArrayList<>();
        List<Responder> case1List = new ArrayList<>();
        List<Responder> case2List = new ArrayList<>();
        case1List.add(testResponder1);
        case2List.add(testResponder2);
        testPoll.addResponder(testResponder1);
        testPoll.addResponder(testResponder2);
        testPoll.respondersWithinIncomeInterval(0, 1999);
        assertEquals(emptyList, testPoll.getFilteredResponders());
        testPoll.respondersWithinIncomeInterval(2501, 5000);
        assertEquals(emptyList, testPoll.getFilteredResponders());
        testPoll.respondersWithinIncomeInterval(2000, 2499);
        assertEquals(case1List, testPoll.getFilteredResponders());
        testPoll.respondersWithinIncomeInterval(2001, 2500);
        assertEquals(emptyList, testPoll.getFilteredResponders());
        testPoll.clearFilteredResponders();
        testPoll.respondersWithinIncomeInterval(2001, 2500);
        assertEquals(case2List, testPoll.getFilteredResponders());
        testPoll.clearFilteredResponders();
        case1List.add(testResponder2);
        testPoll.respondersWithinIncomeInterval(0, 50000);
        assertEquals(case1List, testPoll.getFilteredResponders());
        testPoll.respondersWithinIncomeInterval(500000, 100000);
        assertEquals(emptyList, testPoll.getFilteredResponders());
    }

    @Test
    void testResponsdersWithinAgeInterval() {
        List<Responder> emptyList = new ArrayList<>();
        List<Responder> case1List = new ArrayList<>();
        List<Responder> case2List = new ArrayList<>();
        case1List.add(testResponder1);
        case2List.add(testResponder2);
        testPoll.addResponder(testResponder1);
        testPoll.addResponder(testResponder2);
        testPoll.respondersWithinAgeInterval(0, 5);
        assertEquals(emptyList, testPoll.getFilteredResponders());
        testPoll.respondersWithinAgeInterval(76, 5000);
        assertEquals(emptyList, testPoll.getFilteredResponders());
        testPoll.respondersWithinAgeInterval(6, 74);
        assertEquals(case1List, testPoll.getFilteredResponders());
        testPoll.respondersWithinAgeInterval(7, 75);
        assertEquals(emptyList, testPoll.getFilteredResponders());
        testPoll.clearFilteredResponders();
        testPoll.respondersWithinAgeInterval(7, 75);
        assertEquals(case2List, testPoll.getFilteredResponders());
        testPoll.clearFilteredResponders();
        case1List.add(testResponder2);
        testPoll.respondersWithinAgeInterval(0, 50000);
        assertEquals(case1List, testPoll.getFilteredResponders());
        testPoll.respondersWithinAgeInterval(500, 50000);
        assertEquals(emptyList, testPoll.getFilteredResponders());
    }

    @Test
    void testRespondersOfGenders() {
        List<Responder> emptyList = new ArrayList<>();
        List<Responder> case1List = new ArrayList<>();
        List<Responder> case2List = new ArrayList<>();
        case1List.add(testResponder1);
        case2List.add(testResponder2);
        testPoll.addResponder(testResponder1);
        testPoll.addResponder(testResponder2);
        testPoll.respondersOfGenders(3);
        assertEquals(emptyList, testPoll.getFilteredResponders());
        testPoll.respondersOfGenders(1);
        assertEquals(case1List, testPoll.getFilteredResponders());
        testPoll.clearFilteredResponders();
        testPoll.respondersOfGenders(2);
        assertEquals(case2List, testPoll.getFilteredResponders());
        case1List.add(testResponder2);
        testPoll.setResponderAttribute(1, 6, 1);
        testPoll.clearFilteredResponders();
        testPoll.respondersOfGenders(1);
        assertEquals(case1List, testPoll.getFilteredResponders());
        testPoll.respondersOfGenders(3);
        assertEquals(emptyList, testPoll.getFilteredResponders());
    }

    @Test
    void testRespondersOfOccupations() {
        List<Responder> emptyList = new ArrayList<>();
        List<Responder> case1List = new ArrayList<>();
        List<Responder> case2List = new ArrayList<>();
        List<String> jobList1 = new ArrayList<>();
        jobList1.add("Secret Occupation");
        List<String> jobList2 = new ArrayList<>();
        jobList2.add("Test Occupation 1");
        List<String> jobList3 = new ArrayList<>();
        jobList3.add("Test Occupation 2");
        case1List.add(testResponder1);
        case2List.add(testResponder2);
        testPoll.addResponder(testResponder1);
        testPoll.addResponder(testResponder2);
        testPoll.respondersOfOccupations(jobList1);
        assertEquals(emptyList, testPoll.getFilteredResponders());
        testPoll.respondersOfOccupations(jobList1);
        assertEquals(emptyList, testPoll.getFilteredResponders());
        testPoll.clearFilteredResponders();
        testPoll.respondersOfOccupations(jobList3);
        assertEquals(case2List, testPoll.getFilteredResponders());
        testPoll.clearFilteredResponders();
        jobList3.add("Test Occupation 1");
        case1List.add(testResponder2);
        testPoll.respondersOfOccupations(jobList3);
        assertEquals(case1List, testPoll.getFilteredResponders());
        testPoll.respondersOfOccupations(jobList1);
        assertEquals(emptyList, testPoll.getFilteredResponders());
    }

    @Test
    void testExtractResponderAnswers() {
        List<String> responseList2 = new ArrayList<>();
        List<String> responseList1 = new ArrayList<>();
        responseList1.add("Test answer1");
        responseList1.add("Test answer1");
        testQuestion1.addAnswer("Test answer1");
        testQuestion1.addAnswer("Test answer2");
        testQuestion2.addAnswer("Other Test answer");
        testPoll.addQuestion(testQuestion1);
        testPoll.addQuestion(testQuestion2);
        testPoll.addResponder(testResponder1);
        testPoll.addResponder(testResponder2);
        testPoll.responderAnswered(0, testQuestion1, "Test answer1");
        testPoll.responderAnswered(1, testQuestion1, "Test answer1");
        assertEquals(responseList2, testPoll.extractResponderAnswers(1));
        assertEquals(responseList1, testPoll.extractResponderAnswers(0));
        testPoll.responderAnswered(0, testQuestion2, "Other Test answer");
        responseList2.add("Other Test answer");
        assertEquals(responseList2, testPoll.extractResponderAnswers(1));
        testQuestion2.removeAnswer(0);
        List<String> emptyList = new ArrayList();
        assertEquals(emptyList, testPoll.extractResponderAnswers(1));
        testPoll.respondersOfGenders(1);
        responseList1.remove(0);
        assertEquals(responseList1, testPoll.extractResponderAnswers(0));
    }

    @Test
    void testIsNoDuplicate() {
        testQuestion1.addAnswer("Test answer1");
        testQuestion1.addAnswer("Test answer2");
        testQuestion2.addAnswer("Other Test answer");
        testPoll.addQuestion(testQuestion1);
        testPoll.addQuestion(testQuestion2);
        assertEquals(false, testPoll.isNoDuplicate("Test Question 1"));
        assertEquals(false, testPoll.isNoDuplicate("Test Question 2"));
        assertEquals(true, testPoll.isNoDuplicate("Test Question 3"));
    }

    @Test
    void testIsReadyPoll() {
        assertEquals(false, testPoll.isReadyPoll());
        testPoll.addQuestion(testQuestion1);
        assertEquals(false, testPoll.isReadyPoll());
        testQuestion1.addAnswer("1");
        assertEquals(false, testPoll.isReadyPoll());
        testQuestion1.addAnswer("2");
        assertEquals(true, testPoll.isReadyPoll());
        testPoll.addQuestion(testQuestion2);
        assertEquals(false, testPoll.isReadyPoll());
        testQuestion2.addAnswer("1");
        testQuestion2.addAnswer("2");
        assertEquals(true, testPoll.isReadyPoll());
    }

    @Test
    void testSetFilteredPollResponders() {
        List<Responder> emptyList = new ArrayList<>();
        List<Responder> caseList = new ArrayList<>();
        caseList.add(testResponder1);
        caseList.add(testResponder2);
        assertEquals(emptyList, testPoll.getFilteredResponders());
        testPoll.setFilteredPollResponders(caseList);
        assertEquals(caseList, testPoll.getFilteredResponders());
    }

}