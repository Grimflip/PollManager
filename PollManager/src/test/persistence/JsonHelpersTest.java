package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import model.Question;
import model.Responder;


//Helper methods for other test Json classes
public class JsonHelpersTest {
    public void checkSimpleQuestion(Question question) {
        assertEquals("Simple Question", question.getPollQuestion());
        assertEquals(2, question.getPollAnswers().size());
        assertEquals("Simple Answer 1", question.getPollAnswers().get(0));
        assertEquals("Simple Answer 2", question.getPollAnswers().get(1));
    }

    public void checkSimpleResponder(Responder responder) {
        assertEquals("Simple First Name", responder.getFirstName());
        assertEquals("Simple Last Name", responder.getLastName());
        assertEquals("221-456-7878", responder.getPhoneNumber());
        assertEquals("Simple@Simple.com", responder.getEmail());
        assertEquals(20, responder.getAge());
        assertEquals("Woman", responder.getGender());
        assertEquals(2000, responder.getIncome());
        assertEquals("Test Dummy", responder.getOccupation());
        assertEquals(1, responder.getResponses().size());
        assertEquals("Simple Answer 2", responder.getResponses().get(0).getResponseAnswer());
        checkSimpleQuestion(responder.getResponses().get(0).getResponseQuestion());

    }

    public void checkComplexResponder(Responder responder) {
        assertEquals("Complex First Name", responder.getFirstName());
        assertEquals("Complex Last Name", responder.getLastName());
        assertEquals("765-456-9867", responder.getPhoneNumber());
        assertEquals("Complex@Complex.com", responder.getEmail());
        assertEquals(65, responder.getAge());
        assertEquals("Other", responder.getGender());
        assertEquals(10000, responder.getIncome());
        assertEquals("Complex Test Dummy", responder.getOccupation());
        assertEquals(2, responder.getResponses().size());
        assertEquals("Simple Answer 1", responder.getResponses().get(0).getResponseAnswer());
        checkSimpleQuestion(responder.getResponses().get(0).getResponseQuestion());
        assertEquals("Complex Answer 3", responder.getResponses().get(1).getResponseAnswer());
        checkComplexQuestion(responder.getResponses().get(1).getResponseQuestion());
    }

    public void checkLazyResponder(Responder responder) {
        assertEquals("Lazy First Name", responder.getFirstName());
        assertEquals("Lazy Last Name", responder.getLastName());
        assertEquals("999-675-8989", responder.getPhoneNumber());
        assertEquals("Lazy@Lazy.com", responder.getEmail());
        assertEquals(1, responder.getAge());
        assertEquals("Man", responder.getGender());
        assertEquals(0, responder.getIncome());
        assertEquals("Unemployed", responder.getOccupation());
        assertTrue(responder.getResponses().isEmpty());
    }

    public void checkUnfinishedQuestion(Question question) {
        assertEquals("Unfinished Question", question.getPollQuestion());
        assertTrue(question.getPollAnswers().isEmpty());
    }

    public void checkComplexQuestion(Question question) {
        assertEquals("Complex Question", question.getPollQuestion());
        assertEquals(3, question.getPollAnswers().size());
        assertEquals("Complex Answer 1", question.getPollAnswers().get(0));
        assertEquals("Complex Answer 2", question.getPollAnswers().get(1));
        assertEquals("Complex Answer 3", question.getPollAnswers().get(2));
    }

}
