package com.group12.util;

import java.util.*;

public class GameUtil {

  private static final ArrayList<String> tileList =
      new ArrayList<>(
          Arrays.asList(
              "hill",
              "hill",
              "hill",
              "mountain",
              "mountain",
              "mountain",
              "forest",
              "forest",
              "forest",
              "forest",
              "field",
              "field",
              "field",
              "field",
              "pastureField",
              "pastureField",
              "pastureField",
              "pastureField"));

  private static final ArrayList<String> tileResources =
      new ArrayList<>(
          Arrays.asList(
              "2", "12", "3", "3", "4", "4", "5", "5", "6", "6", "8", "8", "9", "9", "10", "10",
              "11", "11"));

  public static final ArrayList<String> playerColors =
      new ArrayList<>(Arrays.asList("red", "orange", "green", "pink"));

  public static String throwDice() {
    List<String> diceResult = new ArrayList<>();
    Random rnd = new Random();
    for (int i = 0; i < 2; i++) {
      diceResult.add(Integer.toString(rnd.nextInt(6) + 1));
    }
    return String.join("/", diceResult);
  }

  public static String shuffleBoardTiles() {
    StringBuilder sb = new StringBuilder();

    List<String> tileTypes = new ArrayList<>(tileList);
    List<String> tileNumbers = new ArrayList<>(tileResources);

    Collections.shuffle(tileTypes);
    Collections.shuffle(tileNumbers);

    for (int i = 0; i < 18; i++) {
      sb.append(tileTypes.get(i));
      sb.append("-");
      sb.append(tileNumbers.get(i));
      sb.append(i == 17 ? "" : "/");
    }

    return sb.toString();
  }
}
