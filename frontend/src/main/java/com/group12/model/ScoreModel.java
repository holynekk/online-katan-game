package com.group12.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ScoreModel {
    private final SimpleStringProperty displayName;

    private final SimpleIntegerProperty score;

    public ScoreModel(String displayName, int score) {
        this.displayName = new SimpleStringProperty(displayName);
        this.score = new SimpleIntegerProperty(score);
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public void setDisplayName(String dName) {
        displayName.set(dName);
    }

    public int getScore() {
        return score.get();
    }

    public void setScore(int scr) {
        score.set(scr);
    }
}
