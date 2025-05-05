package persistence;

import model.Poll;
import model.Question;
import model.Responder;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class JsonWriterTest extends JsonHelpersTest {

    @Test
    void testWriterInvalidFile() {
        try {
            Poll newPoll = new Poll("My Illegal Poll");
            JsonWriter writer = new JsonWriter("PollManager/data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterNewPoll() {
        try {
            Poll poll = new Poll("My Empty Poll");
            JsonWriter writer = new JsonWriter("PollManager/data/testWriterNewPoll.json");
            writer.open();
            writer.write(poll);
            writer.close();

            JsonReader reader = new JsonReader("PollManager/data/testWriterNewPoll.json");
            poll = reader.readPoll();
            // check equals here
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriteSimplePoll() {
        try {
            Poll poll = new Poll("My Simple Poll");
            makeSimpleQuestion(poll);
            makeSimpleResponder(poll);
            poll.responderAnswered(0, poll.getPollQuestions().get(0), "Simple Answer 2");
            JsonWriter writer = new JsonWriter("PollManager/data/testWriterNewPoll.json");
            writer.open();
            writer.write(poll);
            writer.close();
            JsonReader reader = new JsonReader("PollManager/data/testWriterNewPoll.json");
            poll = reader.readPoll();
            checkSimpleQuestion(poll.getPollQuestions().get(0));
            checkSimpleResponder(poll.getPollResponders().get(0));
            poll.removeQuestion(0);
            assertEquals(0 ,poll.getPollResponders().get(0).getResponses().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriteComplexPoll() {
        try {
            Poll poll = new Poll("My Complex Poll");
            makeSimpleQuestion(poll);
            makeComplexQuestion(poll);
            makeUnfinishedQuestion(poll);
            makeSimpleResponder(poll);
            makeLazyResponder(poll);
            makeComplexResponder(poll);
            poll.responderAnswered(0, poll.getPollQuestions().get(0), "Simple Answer 2");
            poll.responderAnswered(2, poll.getPollQuestions().get(0), "Simple Answer 1");
            poll.responderAnswered(2, poll.getPollQuestions().get(1), "Complex Answer 3");
            JsonWriter writer = new JsonWriter("PollManager/data/testWriterNewPoll.json");
            writer.open();
            writer.write(poll);
            writer.close();
            JsonReader reader = new JsonReader("PollManager/data/testWriterNewPoll.json");
            poll = reader.readPoll();
            checkSimpleQuestion(poll.getPollQuestions().get(0));
            checkComplexQuestion(poll.getPollQuestions().get(1));
            checkUnfinishedQuestion(poll.getPollQuestions().get(2));
            checkSimpleResponder(poll.getPollResponders().get(0));
            checkLazyResponder(poll.getPollResponders().get(1));
            checkComplexResponder(poll.getPollResponders().get(2)); 
            poll.removeQuestion(0);
            assertEquals(0 ,poll.getPollResponders().get(0).getResponses().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    private void makeComplexQuestion(Poll poll) {
        Question complexQuestion = new Question("Complex Question");
        complexQuestion.addAnswer("Complex Answer 1");
        complexQuestion.addAnswer("Complex Answer 2");
        complexQuestion.addAnswer("Complex Answer 3");
        poll.addQuestion(complexQuestion);
    }

    private void makeUnfinishedQuestion(Poll poll) {
        Question unfinishedQuestion = new Question("Unfinished Question");
        poll.addQuestion(unfinishedQuestion);
    }

    private void makeLazyResponder(Poll poll) {
        Responder newResponder = new Responder("Lazy First Name", "Lazy Last Name", 9996758989L,
                "Lazy@Lazy.com", 1, 1, 0, "Unemployed");
                poll.addResponder(newResponder);
    }

    private void makeComplexResponder(Poll poll) {
        Responder newResponder = new Responder("Complex First Name", "Complex Last Name", 7654569867L,
                "Complex@Complex.com", 65, 3, 10000, "Complex Test Dummy");
                poll.addResponder(newResponder);
    }

    // EFFECTS: makes simple responder and adds it to poll
    private void makeSimpleResponder(Poll poll) {
        Responder newResponder = new Responder("Simple First Name", "Simple Last Name", 2214567878L,
                "Simple@Simple.com", 20, 2, 2000, "Test Dummy");
                poll.addResponder(newResponder);
    }

    // EFFECTS: makes simple Question and adds it to poll
    private void makeSimpleQuestion(Poll poll) {
        Question simpleQuestion = new Question("Simple Question");
        simpleQuestion.addAnswer("Simple Answer 1");
        simpleQuestion.addAnswer("Simple Answer 2");
        poll.addQuestion(simpleQuestion);
    }

}
