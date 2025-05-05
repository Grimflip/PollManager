package persistence;

import model.Poll;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderTest extends JsonHelpersTest{

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("PollManager/data/noSuchFile.json");
        try {
            Poll poll = reader.readPoll();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderNewPoll() {
        JsonReader reader = new JsonReader("PollManager/data/testReaderNewPoll.json");
        try {
            Poll poll = reader.readPoll();
            assertEquals("My New Poll", poll.getPollName());
            assertTrue(poll.getPollQuestions().isEmpty());
            assertTrue(poll.getPollResponders().isEmpty());
            assertTrue(poll.getFilteredResponders().isEmpty());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderSimplePoll() {
        JsonReader reader = new JsonReader("PollManager/data/testReaderSimplePoll.json");
        try {
            Poll poll = reader.readPoll();
            assertEquals("My Simple Poll", poll.getPollName());
            checkSimpleQuestion(poll.getPollQuestions().get(0));
            checkSimpleResponder(poll.getPollResponders().get(0));
            poll.removeQuestion(0);
            assertEquals(0 ,poll.getPollResponders().get(0).getResponses().size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    

    @Test
    void testReaderComplexPoll() {
        JsonReader reader = new JsonReader("PollManager/data/testReaderComplexPoll.json");
        try {
            Poll poll = reader.readPoll();
            assertEquals("My Complex Poll", poll.getPollName());
            checkSimpleQuestion(poll.getPollQuestions().get(0));
            checkComplexQuestion(poll.getPollQuestions().get(1));
            checkUnfinishedQuestion(poll.getPollQuestions().get(2));
            checkSimpleResponder(poll.getPollResponders().get(0));
            checkLazyResponder(poll.getPollResponders().get(1));
            checkComplexResponder(poll.getPollResponders().get(2)); 
            poll.removeQuestion(0);
            assertEquals(0 ,poll.getPollResponders().get(0).getResponses().size());
            assertEquals(1 ,poll.getPollResponders().get(2).getResponses().size());

        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

}
