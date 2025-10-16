package org.example;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

  @Test
  void testAddToMap_NewWord() {
    Map<String, Integer> map = new HashMap<>();
    Main.addToMap("hello world", map);
    assertEquals(1, map.get("hello world"));
  }

  @Test
  void testAddToMap_ExistingWordIncrementsCount() {
    Map<String, Integer> map = new HashMap<>();
    map.put("hello", 2);
    Main.addToMap("hello", map);
    assertEquals(3, map.get("hello"));
  }

  @Test
  void testSortPhrases_SortsByDescendingValue() {
    Map<String, Integer> unsorted = new HashMap<>();
    unsorted.put("one", 1);
    unsorted.put("three", 3);
    unsorted.put("two", 2);

    LinkedHashMap<String, Integer> sorted = Main.sortPhrases(unsorted);
    List<String> keys = new ArrayList<>(sorted.keySet());

    assertEquals(Arrays.asList("three", "two", "one"), keys);
  }

  @Test
  void testCleanWord_RemovesPunctuationAndLowercases() throws Exception {
    var method = Main.class.getDeclaredMethod("cleanWord", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(null, "\"Hello!\"");
    assertEquals("hello", result);
  }

  @Test
  void testCleanWord_HandlesEmptyString() throws Exception {
    var method = Main.class.getDeclaredMethod("cleanWord", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(null, "");
    assertEquals("", result);
  }

  @Test
  void testMain_GeneratesExpectedOutputFile() throws Exception {
    // Setup temporary input and output files
    Path tempDir = Files.createTempDirectory("ngramtest");
    Path inputFileList = tempDir.resolve("input.txt");
    Path textFile1 = tempDir.resolve("file1.txt");
    Path outputFile = tempDir.resolve("output.txt");

    // Write content to file1.txt
    Files.writeString(textFile1, "Hello world hello");

    // input.txt contains path to file1.txt
    Files.writeString(inputFileList, textFile1.toString() + System.lineSeparator());

    // Run main with ngram size = 2
    String[] args = {inputFileList.toString(), "2", outputFile.toString()};
    Main.main(args);

    // Validate output file exists and contains expected content
    assertTrue(Files.exists(outputFile));

    String output = Files.readString(outputFile);
    assertTrue(output.contains("hello world"));
    assertTrue(output.contains("world hello"));

    // Cleanup
    Files.deleteIfExists(textFile1);
    Files.deleteIfExists(inputFileList);
    Files.deleteIfExists(outputFile);
    Files.deleteIfExists(tempDir);
  }

  @Test
  void testMain_InvalidArgs_ShowsMessage() {
    // Capture system output
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    String[] args = {"onlyOneArg"};
    Main.main(args);

    String output = out.toString().trim();
    assertTrue(output.contains("Invalid arguments"));
  }
}
