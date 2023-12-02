package com.group12.controller;

import com.group12.helper.NotificationHelper;
import com.group12.model.CPUPlayer;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

import static com.group12.helper.GameBoardSetupHelper.circleOptionsAtSetup;
import static com.group12.helper.GameBoardSetupHelper.setupBoardTiles;

@Component
public class GameController {

  @FXML private Polygon hexagon;

  @FXML private AnchorPane anchPane;

  @FXML private Text h1;
  @FXML private Text h2;
  @FXML private Text h3;
  @FXML private Text h4;
  @FXML private Text h5;
  @FXML private Text h6;
  @FXML private Text h7;
  @FXML private Text h8;
  @FXML private Text h9;
  @FXML private Text h10;
  @FXML private Text h11;
  @FXML private Text h12;
  @FXML private Text h13;
  @FXML private Text h14;
  @FXML private Text h15;
  @FXML private Text h16;
  @FXML private Text h17;
  @FXML private Text h18;
  @FXML private Text h19;

  @FXML private Text firstDiceResult;

  @FXML private Text secondDiceResult;

  private int turn;

  private int hillResource;
  private int mountainResource;
  private int forestResource;
  private int fieldResource;
  private int pastureFieldResource;

  private ArrayList<Text> tileTextList = new ArrayList<>();

  private ArrayList<String> hexagonList =
      new ArrayList<>(
          Arrays.asList(
              "h1", "h2", "h3", "h4", "h5", "h6", "h7", "h8", "h9", "h10", "h11", "h12", "h13",
              "h14", "h15", "h16", "h17", "h18", "h19"));

  ArrayList<String> rectangleList =
      new ArrayList<>(
          Arrays.asList(
              "h1", "h2", "h3", "h4", "h5", "h6", "h7", "h8", "h9", "h10", "h11", "h12", "h13",
              "h14", "h15", "h16", "h17", "h18", "h19"));

  ArrayList<String> circleList =
      new ArrayList<>(
          Arrays.asList(
              "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10", "c11", "c12", "c13",
              "c14", "c15", "c16", "c17", "c18", "c19", "c20", "c21", "c22", "c23", "c24", "c25",
              "c26", "c27", "c28", "c29", "c30", "c31", "c32", "c33", "c34", "c35", "c36", "c37",
              "c38", "c39", "c40", "c41", "c42", "c43", "c44", "c45", "c46", "c47", "c48", "c49",
              "c50", "c51", "c52", "c53", "c54"));

  private ArrayList<String> occupiedCircles = new ArrayList<>();
  private ArrayList<String> occupiedEdges = new ArrayList<>();

  public void initialize() {
    turn = 0;
    hillResource = 0;
    mountainResource = 0;
    forestResource = 0;
    fieldResource = 0;
    pastureFieldResource = 0;
    tileTextList =
        new ArrayList<>(
            Arrays.asList(
                h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12, h13, h14, h15, h16, h17, h18,
                h19));
    setupBoardTiles(anchPane, tileTextList);
  }

  @FXML
  public void rectanglePush(MouseEvent event) {
    Rectangle eventRectangle = (Rectangle) event.getSource();
    String rectangleId = eventRectangle.getId();

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        node.setVisible(false);
        if (node.getId().equals(rectangleId)) {
          node.setVisible(true);
          node.setStyle("-fx-fill: red;");
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
        }
      }
    }
    occupiedEdges.add(rectangleId);
    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have built a new road!");
    CPUPlayer cpuOrange = new CPUPlayer("orange", new ArrayList<>(), new ArrayList<>());
    CPUPlayer cpuGreen = new CPUPlayer("green", new ArrayList<>(), new ArrayList<>());
    CPUPlayer cpuPink = new CPUPlayer("pink", new ArrayList<>(), new ArrayList<>());
    // CPU plays
    String cpuCircleId;
    String cpuRectangleId;
    // Orange --------------------------------------------------------------------
    cpuCircleId =
        cpuOrange.buildSettlementAtSetup(
            anchPane, circleOptionsAtSetup(anchPane, circleList, occupiedCircles));
    occupiedCircles.add(cpuCircleId);

    cpuRectangleId = cpuOrange.buildRoadAtSetup(anchPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    // Green --------------------------------------------------------------------
    cpuCircleId =
        cpuGreen.buildSettlementAtSetup(
            anchPane, circleOptionsAtSetup(anchPane, circleList, occupiedCircles));
    occupiedCircles.add(cpuCircleId);

    cpuRectangleId = cpuGreen.buildRoadAtSetup(anchPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    // Pink --------------------------------------------------------------------
    cpuCircleId =
        cpuPink.buildSettlementAtSetup(
            anchPane, circleOptionsAtSetup(anchPane, circleList, occupiedCircles));
    occupiedCircles.add(cpuCircleId);

    cpuRectangleId = cpuPink.buildRoadAtSetup(anchPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    // Pink AGAIN --------------------------------------------------------------------
    cpuCircleId =
        cpuPink.buildSettlementAtSetup(
            anchPane, circleOptionsAtSetup(anchPane, circleList, occupiedCircles));
    occupiedCircles.add(cpuCircleId);

    cpuRectangleId = cpuPink.buildRoadAtSetup(anchPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    // Green AGAIN --------------------------------------------------------------------
    cpuCircleId =
        cpuGreen.buildSettlementAtSetup(
            anchPane, circleOptionsAtSetup(anchPane, circleList, occupiedCircles));
    occupiedCircles.add(cpuCircleId);

    cpuRectangleId = cpuGreen.buildRoadAtSetup(anchPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    // Orange AGAIN --------------------------------------------------------------------
    cpuCircleId =
        cpuOrange.buildSettlementAtSetup(
            anchPane, circleOptionsAtSetup(anchPane, circleList, occupiedCircles));
    occupiedCircles.add(cpuCircleId);

    cpuRectangleId = cpuOrange.buildRoadAtSetup(anchPane, cpuCircleId);
    occupiedEdges.add(cpuRectangleId);

    ArrayList<String> blabla = circleOptionsAtSetup(anchPane, circleList, occupiedCircles);
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {
        if (blabla.contains(node.getId())) {
          node.setVisible(true);
          node.setStyle("-fx-fill: #D3D3D3;");
          node.setOnMouseClicked(this::lastCirclePush);
        }
      }
    }
  }

  @FXML
  public void circlePush(MouseEvent event) {
    Circle eventCircle = (Circle) event.getSource();
    String circleId = eventCircle.getId();
    // Set the settlement

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {
        node.setVisible(false);
        if (node.getId().equals(circleId)) {
          node.setVisible(true);
          node.setStyle("-fx-fill: red;");
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
          node.setScaleX(1.3);
          node.setScaleY(1.3);
        }
      }
    }
    occupiedCircles.add(circleId);
    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have built a new settlement!");
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle") && node.getId().contains(circleId)) {
        node.setVisible(true);
      }
    }
  }

  @FXML
  public void lastCirclePush(MouseEvent event) {
    Circle eventCircle = (Circle) event.getSource();
    String circleId = eventCircle.getId();
    // Set the settlement

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Circle")) {

        if (node.getId().equals(circleId)) {
          node.setVisible(true);
          node.setStyle("-fx-fill: red;");
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
          node.setScaleX(1.3);
          node.setScaleY(1.3);
        }
      }
    }
    occupiedCircles.add(circleId);
    for (Node node1 : anchPane.getChildren()) {
      if (node1.getClass().getName().contains("Circle")) {
        if (!occupiedCircles.contains(node1.getId())) {
          node1.setVisible(false);
        }
      }
    }
    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have built a new settlement!");

    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (node.getId().contains(circleId)) {
          node.setVisible(true);
          node.setOnMouseClicked(this::lastRectanglePush);
        }
      }
    }
  }

  @FXML
  public void lastRectanglePush(MouseEvent event) {
    Rectangle eventRectangle = (Rectangle) event.getSource();
    String rectangleId = eventRectangle.getId();
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (node.getId().equals(rectangleId)) {
          node.setVisible(true);
          node.setStyle("-fx-fill: red;");
          node.setOnMouseClicked(null);
          node.setCursor(Cursor.DEFAULT);
        }
      }
    }
    occupiedEdges.add(rectangleId);
    for (Node node : anchPane.getChildren()) {
      if (node.getClass().getName().contains("Rectangle")) {
        if (!occupiedEdges.contains(node.getId())) {
          node.setVisible(false);
        }
      }
    }

    NotificationHelper.showAlert(
        Alert.AlertType.INFORMATION, "Information", "You have built a new road!");
  }

  @FXML
  public void hexagonPush(MouseEvent event) {
    System.out.println("hex push");
  }
}
