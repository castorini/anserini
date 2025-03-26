package io.anserini.fusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class FusionOptionsTest {
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();
    private PrintStream save;

    private void redirectStderr() {
        save = System.err;
        err.reset();
        System.setErr(new PrintStream(err));
    }

    private void restoreStderr() {
        System.setErr(save);
    }

    @Test
    public void testInvalidDepth() throws Exception {
        redirectStderr();

        String[] fuseArgs = new String[] {
            "-runs", "/fake/path",
            "-output", "runs/fused_run.test",
            "-method", "normalize",
            "-k", "1000",
            "-depth", "0",
            "-rrf_k", "60",
            "-alpha", "0.5"};
        FuseTrecRuns.main(fuseArgs);

        assertEquals("Error: Option depth must be greater than 0. Please check the provided arguments. Use the \"-options\" flag to print out detailed information about available options and their usage.\n".trim(), err.toString().trim());
        restoreStderr();
    }

    @Test
    public void testInvalidK() throws Exception {
        redirectStderr();

        String[] fuseArgs = new String[] {
            "-runs", "/fake/path",
            "-output", "runs/fused_run.test",
            "-method", "normalize",
            "-k", "0",
            "-depth", "1000",
            "-rrf_k", "60",
            "-alpha", "0.5"};
        FuseTrecRuns.main(fuseArgs);

        assertEquals("Error: Option k must be greater than 0. Please check the provided arguments. Use the \"-options\" flag to print out detailed information about available options and their usage.\n".trim(), err.toString().trim());
        restoreStderr();
    }

    @Test
    public void testInvalidRuns() throws Exception {
        redirectStderr();

        String[] fuseArgs = new String[] {
            "-runs", "/fake/path /fake/path2",
            "-output", "runs/fused_run.test",
            "-method", "normalize",
            "-k", "1000",
            "-depth", "1000",
            "-rrf_k", "60",
            "-alpha", "0.5"};
        FuseTrecRuns.main(fuseArgs);

        assertEquals("Error: /fake/path (No such file or directory). Please check the provided arguments. Use the \"-options\" flag to print out detailed information about available options and their usage.\n".trim(), err.toString().trim());
        restoreStderr();
    }

    @Test
    public void testInvalidMethod() throws Exception {
        redirectStderr();

        String[] fuseArgs = new String[] {
            "-runs", "src/test/resources/sample_runs/run1 src/test/resources/sample_runs/run2",
            "-output", "runs/fused_run.test",
            "-method", "add",
            "-k", "1000",
            "-depth", "1000",
            "-rrf_k", "60",
            "-alpha", "0.5"};
        FuseTrecRuns.main(fuseArgs);

        assertEquals("Unknown fusion method: add. Supported methods are: average, rrf, interpolation.".trim(), err.toString().trim());
        restoreStderr();
    }

    @Test
    public void testOptions() throws Exception {
        redirectStderr();

        String[] fuseArgs = new String[] {
            "-runs", "/fake/path /fake/path2",
            "-options"
        };
        FuseTrecRuns.main(fuseArgs);

        assertTrue(err.toString().contains("Options for FuseTrecRuns:"));
        restoreStderr();
    }
}
