package org.example;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main
{
  public static void main(String[] args)
  {
    //read arguments
    if (args.length != 3) {
      System.out.println("Invalid arguments");
      return;
    }
    int maxNgramSize = Integer.parseInt(args[1]);

    String inputPath = args[0];
    String outputPath = args[2];
    List<String> fileNames = new ArrayList<>();
    Map<String, Integer> phrases = new HashMap<>();
    LinkedHashMap<String, Integer> sortedPhrases = new LinkedHashMap<>();

    try (Scanner scanner = new Scanner(new File(inputPath))) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        fileNames.add(line);
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      System.err.println("File not found: " + e.getMessage());
    }

    for (String filePath: fileNames) {
      List<String> words = new ArrayList<>();

      try {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
          String word = cleanWord(scanner.next());
          words.add(word);
        }
        scanner.close(); // Close the scanner to release resources
      } catch (FileNotFoundException e) {
        System.err.println("Error: File not found at " + filePath);
        e.printStackTrace();
      }

      for (int i = 2; i <= maxNgramSize; i++) {
        for (int j = 0; j <= words.size() - i; j++) {
          String phrase = "";
          for (int k = 0; k < i; k++) {
            if (k == 0) {
              phrase = words.get(j);
            } else {
              phrase = phrase + " " + words.get(j + k);
            }
          }
          addToMap(phrase, phrases);
        }

      }
    }

    sortedPhrases = sortPhrases(phrases);
    System.out.println(sortedPhrases);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
      for (Map.Entry<String, Integer> entry : sortedPhrases.entrySet()) {
        writer.write(entry.getKey() + " " + entry.getValue());
        writer.newLine(); // Writes a new line character
      }
      System.out.println("Map items successfully written to " + outputPath);
    } catch (IOException e) {
      System.err.println("Error writing to file: " + e.getMessage());
      e.printStackTrace();
    }


  }
  public static LinkedHashMap<String, Integer> sortPhrases(Map<String, Integer> phrases) {
    LinkedHashMap<String, Integer> sortedPhrases = phrases.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
            ));
    return sortedPhrases;
  }

  public static void addToMap(String word, Map<String, Integer> returnList) {
    int count = 0;
    if (returnList.containsKey(word)) {
      count = returnList.get(word);
    }
    returnList.put(word, count+1);
  }

  private static String cleanWord(String word) {
    String lowerCase = word.toLowerCase();
    return lowerCase.replaceAll("^\\p{Punct}+|\\p{Punct}+$", "");
  }
}


