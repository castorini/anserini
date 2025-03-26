package io.anserini.fusion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.anserini.TestUtils;

public class TrecRunTest {
    @Test
    public void testInvalidNumRuns() {
        TrecRun run = new TrecRun();
        Path newPath = Paths.get("runs/new_run");
        IllegalStateException thrown1 = assertThrows(IllegalStateException.class, () -> {
            run.saveToTxt(newPath, "Anserini");
        });
        assertEquals("Nothing to save. TrecRun is empty".trim(), thrown1.getMessage().trim());

        List<TrecRun> runs = new ArrayList<>();
        runs.add(run);
        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> {
            TrecRun.merge(runs, 1000, 1000);
        });
        assertEquals("Merge requires at least 2 runs.".trim(), thrown2.getMessage().trim());
    }

    @Test
    public void testResort() {
        try {
            Path path = Paths.get("src/test/resources/sample_runs/run3");
            TrecRun run = new TrecRun(path, true);
            Path newPath = Paths.get("runs/sorted_run");
            run.saveToTxt(newPath, "Anserini");
            TestUtils.checkFile("runs/sorted_run", new String[]{
                "query1 Q0 doc1 1 7.000000 Anserini",
                "query1 Q0 doc2 2 6.000000 Anserini",
                "query1 Q0 doc3 3 5.000000 Anserini",
                "query2 Q0 doc1 1 14.000000 Anserini",
                "query2 Q0 doc2 2 13.000000 Anserini",
                "query2 Q0 doc3 3 12.000000 Anserini"
            });
            assertTrue(new File("runs/sorted_run").delete());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
