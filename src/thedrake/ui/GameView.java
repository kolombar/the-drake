package thedrake.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.Blend;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import javafx.beans.binding.Bindings;

import static thedrake.client.Controller.getTitleEffect;

public class GameView extends BorderPane {

    private BoardView boardView;
    private StackView stackViewBlue;
    private StackView stackViewOrange;
    private CaptureView captureViewBlue;
    private CaptureView captureViewOrange;

    public GameView(BoardView boardView, StackView stackViewBlue, StackView stackViewOrange, CaptureView captureViewBlue, CaptureView captureViewOrange) {
        this.boardView = boardView;
        this.stackViewBlue = stackViewBlue;
        this.stackViewOrange = stackViewOrange;
        this.captureViewBlue = captureViewBlue;
        this.captureViewOrange = captureViewOrange;

        setPrefWidth(1600);
        setPrefHeight(1000);

        GridPane board = getBoard(boardView, getPrefWidth(), getPrefHeight());
        VBox blueStack = getStack(stackViewBlue, getPrefWidth(), getPrefHeight());
        VBox orangeStack = getStack(stackViewOrange, getPrefWidth(), getPrefHeight());
        VBox blueCapture = getCapture(captureViewBlue, getPrefWidth(), getPrefHeight());
        VBox orangeCapture = getCapture(captureViewOrange, getPrefWidth(), getPrefHeight());

        setCenter(board);
        setBottom(blueStack);
        setTop(orangeStack);
        setLeft(blueCapture);
        setRight(orangeCapture);
    }

    private Text getStackText(VBox parent) {
        Text stackText = new Text();
        stackText.setText("Stack");
        stackText.setFill(Color.YELLOW);
        stackText.setTextAlignment(TextAlignment.CENTER);
        DoubleProperty fontSize = new SimpleDoubleProperty(20);
        fontSize.bind(Bindings.divide(Bindings.add(parent.prefWidthProperty(), parent.prefHeightProperty()), 30));
        stackText.setFont(Font.font(null, FontWeight.BOLD, Double.parseDouble(fontSize.getValue().toString())));
        Blend blend = getTitleEffect();
        stackText.setEffect(blend);

        return stackText;
    }

    private VBox getStack(GridPane stackView, Double width, Double height) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefWidth(width);
        vbox.setPrefHeight(height / 4);
        vbox.getChildren().addAll(getStackText(vbox), stackView);

        return vbox;
    }

    private Text getCaptureText(VBox parent) {
        Text captureText = new Text();
        captureText.setText("Capture");
        captureText.setFill(Color.YELLOW);
        captureText.setTextAlignment(TextAlignment.CENTER);
        DoubleProperty fontSize = new SimpleDoubleProperty(20);
        fontSize.bind(Bindings.divide(Bindings.add(parent.prefWidthProperty(), parent.prefHeightProperty()), 15));
        captureText.setFont(Font.font(null, FontWeight.BOLD, Double.parseDouble(fontSize.getValue().toString())));
        Blend blend = getTitleEffect();
        captureText.setEffect(blend);

        return captureText;
    }

    private VBox getCapture(GridPane captureView, Double width, Double height) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefWidth(width / 5);
        vbox.setPrefHeight(height / 2);
        vbox.getChildren().addAll(getCaptureText(vbox), captureView);
        vbox.setPadding(new Insets(0, 10, 0, 10));

        return vbox;
    }

    private GridPane getBoard(GridPane boardView, Double width, Double height) {
        boardView.setPrefWidth(width / 2);
        boardView.setPrefHeight(height / 2);

        return boardView;
    }
}
