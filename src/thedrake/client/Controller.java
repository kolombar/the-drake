package thedrake.client;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import thedrake.GameState;
import thedrake.PlayingSide;
import thedrake.ui.BoardView;
import thedrake.ui.CaptureView;
import thedrake.ui.GameView;
import thedrake.ui.StackView;

import static thedrake.ui.TheDrakeApp.createNewGame;

public class Controller implements Initializable {

    @FXML
    private HBox box;

    @FXML
    private BorderPane pane;

    @FXML
    private Button endGame;

    @FXML
    private Button startGame;

    @FXML
    private void endGameAction() {
        Stage stage = (Stage) endGame.getScene().getWindow();
        stage.close();
    }

    public static InputStream backgroundFromJPG(String fileName) {
        return Controller.class.getResourceAsStream("/thedrake/client/" + fileName + ".jpg");
    }

    public static BackgroundSize setSize() {
        return new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true);
    }

    @FXML
    private void startGameAction() {
        Stage stage = (Stage) startGame.getScene().getWindow();
        startNewGame(stage);
    }

    public static void startNewGame(Stage stage) {
        GameView gameView = createGameScene(stage);
        stage.setScene(new Scene(gameView));
        stage.setTitle("The Drake");
        stage.show();
    }

    public static GameView createGameScene(Stage stage) {
        GameState newGameState = createNewGame();
        BoardView boardView = new BoardView(newGameState);

        StackView stackViewBlue = new StackView(newGameState, PlayingSide.BLUE, boardView);
        StackView stackViewOrange = new StackView(newGameState, PlayingSide.ORANGE, boardView);
        boardView.setPlaceFromStackContexts(stackViewBlue, stackViewOrange);

        CaptureView captureViewBlue = new CaptureView(newGameState);
        CaptureView captureViewOrange = new CaptureView(newGameState);
        boardView.setCaptureViewContexts(captureViewBlue, captureViewOrange);

        GameView gameView = new GameView(boardView, stackViewBlue, stackViewOrange, captureViewBlue, captureViewOrange);
        gameView.prefWidthProperty().bind(stage.getScene().getWindow().widthProperty());
        gameView.prefHeightProperty().bind(stage.getScene().getWindow().heightProperty());
        BackgroundImage bgImage = new BackgroundImage(new Image(backgroundFromJPG("black_wall")), null, null, null, setSize());
        gameView.setBackground(new Background(bgImage));
        gameView.setOpacity(0.86);

        return gameView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        manageTitle();
        manageButtons();
        manageKnight();
    }

    private void manageTitle() {
        DoubleProperty fontSize = new SimpleDoubleProperty(50);
        fontSize.bind(Bindings.divide(Bindings.add(box.prefWidthProperty(), box.prefHeightProperty()), 1.5));
        Text text = new Text();
        text.setFill(Color.YELLOW);
        text.setFont(Font.font(null, FontWeight.BOLD, Double.parseDouble(fontSize.getValue().toString())));
        text.setText("THE DRAKE");

        Blend blend = getTitleEffect();
        text.setEffect(blend);

        box.prefWidthProperty().bind(pane.prefWidthProperty());
        box.prefHeightProperty().bind(Bindings.divide(pane.prefHeightProperty(), 5));

        box.getChildren().add(text);
    }

    private void manageButtons() {
        VBox buttonsVbox = (VBox) pane.getLeft();
        buttonsVbox.prefWidthProperty().bind(Bindings.divide(Bindings.add(pane.widthProperty(), pane.heightProperty()), 4));
        buttonsVbox.prefHeightProperty().bind(Bindings.divide(Bindings.add(pane.widthProperty(), pane.heightProperty()), 2));

        for (Node node : buttonsVbox.getChildren()) {
            Button button = (Button) node;
            button.setMinHeight(0);

            DoubleProperty buttonFontSize = new SimpleDoubleProperty(20);
            button.setFont(Font.font(null, FontWeight.BOLD, Double.parseDouble(buttonFontSize.getValue().toString())));

            VBox.setVgrow(button, Priority.ALWAYS);
        }
    }

    private void manageKnight() {
        VBox knightVbox = (VBox) pane.getRight();
        knightVbox.prefWidthProperty().bind(Bindings.divide(Bindings.add(pane.widthProperty(), pane.heightProperty()), 4));
        knightVbox.prefHeightProperty().bind(Bindings.divide(Bindings.add(pane.widthProperty(), pane.heightProperty()), 1));
        knightVbox.setAlignment(Pos.CENTER);

        ImageView knight = (ImageView) knightVbox.getChildren().get(0);
        VBox.setVgrow(knight, Priority.ALWAYS);

        knight.setPreserveRatio(true);
    }

    public static Blend getTitleEffect() {
        Blend blend = new Blend();
        blend.setMode(BlendMode.MULTIPLY);
        DropShadow ds = new DropShadow();
        ds.setColor(Color.rgb(254, 235, 66, 0.3));
        ds.setOffsetX(5);
        ds.setOffsetY(5);
        ds.setRadius(5);
        ds.setSpread(0.2);

        blend.setBottomInput(ds);

        DropShadow ds1 = new DropShadow();
        ds1.setColor(Color.web("#f13a00"));
        ds1.setRadius(20);
        ds1.setSpread(0.2);

        Blend blend2 = new Blend();
        blend2.setMode(BlendMode.MULTIPLY);

        InnerShadow is = new InnerShadow();
        is.setColor(Color.web("#feeb42"));
        is.setRadius(9);
        is.setChoke(0.8);
        blend2.setBottomInput(is);

        InnerShadow is1 = new InnerShadow();
        is1.setColor(Color.web("#f13a00"));
        is1.setRadius(5);
        is1.setChoke(0.4);
        blend2.setTopInput(is1);

        Blend blend1 = new Blend();
        blend1.setMode(BlendMode.MULTIPLY);
        blend1.setBottomInput(ds1);
        blend1.setTopInput(blend2);

        blend.setTopInput(blend1);

        return blend;
    }
}


