package model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QuestionTest {
    private Question testQuestion;
    
    @BeforeEach
    void runBefore() {
        testQuestion = new Question("Test Question");
    }

    @Test
    void testConstructor(){
        List<String> emptyTestList = new ArrayList<>();
        assertEquals("Test Question", testQuestion.getPollQuestion());
        assertEquals(emptyTestList, testQuestion.getPollAnswers());
    }

    @Test
    void testSetQuestionOnce(){
        testQuestion.setQuestion("Other Test Question");
        assertEquals("Other Test Question", testQuestion.getPollQuestion());
    }

    @Test
    void testSetQuestionMultiple(){
        testQuestion.setQuestion("Other Test Question");
        assertEquals("Other Test Question", testQuestion.getPollQuestion());
        testQuestion.setQuestion("Another Test Question");
        assertEquals("Another Test Question", testQuestion.getPollQuestion());
    }

    @Test
    void testAddAnswerOnce(){
        assertEquals(0, testQuestion.getPollAnswers().size());
        testQuestion.addAnswer("Answer 1");
        assertEquals(1, testQuestion.getPollAnswers().size());
        assertEquals("Answer 1",testQuestion.getPollAnswers().get(0));
    }

    @Test
    void testAddAnswerMultiple(){
        assertEquals(0, testQuestion.getPollAnswers().size());
        testQuestion.addAnswer("Answer 1");
        assertEquals(1, testQuestion.getPollAnswers().size());
        assertEquals("Answer 1",testQuestion.getPollAnswers().get(0));
        testQuestion.addAnswer("Answer 2");
        testQuestion.addAnswer("Answer 2");
        assertEquals(3, testQuestion.getPollAnswers().size());
        assertEquals("Answer 2",testQuestion.getPollAnswers().get(1));
        assertEquals("Answer 2",testQuestion.getPollAnswers().get(2));
        assertEquals("Answer 1",testQuestion.getPollAnswers().get(0));
    }

    @Test
    void testRemoveAnswerOnce(){
        assertEquals(0, testQuestion.getPollAnswers().size());
        testQuestion.addAnswer("Answer 1");
        assertEquals(1, testQuestion.getPollAnswers().size());
        assertEquals("Answer 1",testQuestion.getPollAnswers().get(0));
        testQuestion.removeAnswer(0);
        assertEquals(0, testQuestion.getPollAnswers().size());
    }

    @Test
    void testRemoveAnswerMultiple(){
        assertEquals(0, testQuestion.getPollAnswers().size());
        testQuestion.addAnswer("Answer 1");
        testQuestion.addAnswer("Answer 2");
        assertEquals(2, testQuestion.getPollAnswers().size());
        assertEquals("Answer 1",testQuestion.getPollAnswers().get(0));
        assertEquals("Answer 2",testQuestion.getPollAnswers().get(1));
        testQuestion.removeAnswer(0);
        assertEquals(1, testQuestion.getPollAnswers().size());
        assertEquals("Answer 2",testQuestion.getPollAnswers().get(0));
        testQuestion.addAnswer("Answer 3");
        testQuestion.removeAnswer(1);
        assertEquals(1, testQuestion.getPollAnswers().size());
        assertEquals("Answer 2",testQuestion.getPollAnswers().get(0));
        testQuestion.removeAnswer(0);
        assertEquals(0, testQuestion.getPollAnswers().size());
    }

    @Test
    void testModifyAnswer(){
        testQuestion.addAnswer("Answer 1");
        testQuestion.modifyAnswer(0,"Answer 2");
        assertEquals(1, testQuestion.getPollAnswers().size());
        assertEquals("Answer 2",testQuestion.getPollAnswers().get(0));
        testQuestion.addAnswer("Answer 3");
        assertEquals("Answer 3",testQuestion.getPollAnswers().get(1));
        testQuestion.modifyAnswer(1,"Answer 4");
        testQuestion.modifyAnswer(1,"Answer 5");
        assertEquals(2, testQuestion.getPollAnswers().size());
        assertEquals("Answer 2",testQuestion.getPollAnswers().get(0));
        assertEquals("Answer 5",testQuestion.getPollAnswers().get(1));

    }
}
