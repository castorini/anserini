package io.anserini.server;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ControllerTest {

    @Test
    public void testSearch() throws Exception {
        Controller controller = new Controller();

        List<QueryResult> results = controller.search(null, "Albert Einstein");

        assertEquals(results.size(), 10);
        assertEquals(results.get(0).getDocid(), "3075155");
    }

    @Test
    public void testIndexNotFound() throws Exception {
        Controller controller = new Controller();

        assertThrows(RuntimeException.class, () -> {
            List<QueryResult> results = controller.search("nonexistent-index", "Albert Einstein");
        });
    }

}
