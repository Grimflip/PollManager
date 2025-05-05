package model;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class ResponseTest {
    private Response testResponse;
    private Question testQuestion;

    @BeforeEach
    void runBefore(){
        testQuestion = new Question("Test Question");
        testQuestion.addAnswer("Test Answer");
        testResponse = new Response(testQuestion, "Test Answer");
    }

    @Test
    void testConstructor(){
        assertEquals(testQuestion, testResponse.getResponseQuestion());
        assertEquals("Test Answer", testResponse.getResponseAnswer());
    }

    @Test
    void testSetResponseQuestionOnce(){
        Question testQuestion2 = new Question("Test Question 2");
        testQuestion2.addAnswer("Test Answer 2");
        testResponse.setResponseQuestion(testQuestion2);
        assertEquals(testQuestion2, testResponse.getResponseQuestion());
        assertEquals("Test Answer", testResponse.getResponseAnswer());
    }

    @Test
    void testSetQuestionMultiple(){
        Question testQuestion2 = new Question("Test Question 2");
        testQuestion2.addAnswer("Test Answer 2");
        Question testQuestion3 = new Question("Test Question 3");
        testQuestion2.addAnswer("Test Answer 3");
        testResponse.setResponseQuestion(testQuestion2);
        assertEquals(testQuestion2, testResponse.getResponseQuestion());
        assertEquals("Test Answer", testResponse.getResponseAnswer());
        testResponse.setResponseQuestion(testQuestion);
        testResponse.setResponseQuestion(testQuestion3);
        assertEquals(testQuestion3, testResponse.getResponseQuestion());
        assertEquals("Test Answer", testResponse.getResponseAnswer());

    }

    @Test
    void testSetResponseAnswerOnce(){
        testResponse.setResponseAnswer("New Test Answer");
        assertEquals(testQuestion, testResponse.getResponseQuestion());
        assertEquals("New Test Answer", testResponse.getResponseAnswer());
    }

    @Test
    void testSetResponseAnswerMultiple(){
        testResponse.setResponseAnswer("New Test Answer");
        assertEquals(testQuestion, testResponse.getResponseQuestion());
        assertEquals("New Test Answer", testResponse.getResponseAnswer());
        testResponse.setResponseAnswer("Intermediate Test Answer");
        testResponse.setResponseAnswer("Final Test Answer");
        assertEquals(testQuestion, testResponse.getResponseQuestion());
        assertEquals("Final Test Answer", testResponse.getResponseAnswer());
    }

}
