package com.group12.controller;

import com.group12.model.GameData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import static com.group12.helper.HttpClientHelper.getSessionCookie;

@Component
public class OnlineGameController {
    @FXML
    private Polygon hexagon;

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

    @FXML private Button skipTurnButton;

    @FXML private ImageView firstDiceImage;
    @FXML private ImageView secondDiceImage;

    @FXML private Button roadBuildButton;
    @FXML private Button settlementBuildButton;
    @FXML private Button settlementUpgradeButton;

    @FXML private Pane p1;
    @FXML private Pane p2;
    @FXML private Pane p3;
    @FXML private Pane p4;

    @FXML private Text player1Name;
    @FXML private Text player2Name;
    @FXML private Text player3Name;
    @FXML private Text player4Name;

    @FXML private Text player1LongestRoad;
    @FXML private Text player2LongestRoad;
    @FXML private Text player3LongestRoad;
    @FXML private Text player4LongestRoad;

    @FXML private Text player1TotalResources;
    @FXML private Text player2TotalResources;
    @FXML private Text player3TotalResources;
    @FXML private Text player4TotalResources;

    @FXML private Text player1Score;
    @FXML private Text player2Score;
    @FXML private Text player3Score;
    @FXML private Text player4Score;

    @FXML private Text woolText;
    @FXML private Text oreText;
    @FXML private Text grainText;
    @FXML private Text brickText;
    @FXML private Text lumberText;

    @FXML private Label resultBanner;
    @FXML private Button backButton;

    public void initialize() {}

}
