package thedrake.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.Blend;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import thedrake.GameResult;
import thedrake.GameState;

import static thedrake.client.Controller.*;

public class FinalView extends VBox {

    public FinalView (GameState gameState, Stage window) {

        BackgroundImage bgImage = new BackgroundImage(new Image(backgroundFromJPG("black_wall")), null, null, null, setSize());
        setBackground(new Background(bgImage));

        Text text = createTitle(gameState, window);
        getChildren().add(text);

        Button newGame = createNewGameButton(window);
        Button endGame = createExitGameButton(window);

        getChildren().add(newGame);
        getChildren().add(endGame);

        setSpacing(60);

        setOpacity(0.86);
        setAlignment(Pos.CENTER);
    }

    private String getIdleButtonStyle() {
        return "-fx-background-color:\n" +
                "            linear-gradient(#ffd65b, #e68400),\n" +
                "            linear-gradient(#ffef84, #f2ba44),\n" +
                "            linear-gradient(#ffea6a, #efaa22),\n" +
                "            linear-gradient(#ffe657 0%, #f8c202 50%, #eea10b 100%),\n" +
                "            linear-gradient(from 0% 0% to 15% 50%, rgba(255,255,255,0.9), rgba(255,255,255,0));\n" +
                "    -fx-background-insets: 0,1,2,3,0;\n" +
                "    -fx-text-fill: #654b00;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-font-size: 20px;\n" +
                "    -fx-padding: 20 40 20 40;\n" +
                "    -fx-pref-width: 450px;";
    }

    private String getHoverButtonStyle() {
        return "-fx-background-color:\n" +
                "            linear-gradient(#e68400, #ffd65b),\n" +
                "            linear-gradient(#f2ba44, #ffef84),\n" +
                "            linear-gradient(#efaa22, #ffea6a),\n" +
                "            linear-gradient(#ffe657 0%, #f8c202 50%, #eea10b 100%),\n" +
                "            linear-gradient(from 0% 0% to 15% 50%, rgba(255,255,255,0.9), rgba(255,255,255,0));\n" +
                "    -fx-background-insets: 0,1,2,3,0;\n" +
                "    -fx-text-fill: #654b00;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-font-size: 20px;\n" +
                "    -fx-padding: 20 40 20 40;\n" +
                "    -fx-pref-width: 450px;";
    }

    private Button createNewGameButton(Stage window) {
        Button newGame = new Button("New game");
        newGame.setStyle(getIdleButtonStyle());
        newGame.setOnMouseEntered(e -> newGame.setStyle(getHoverButtonStyle()));
        newGame.setOnMouseExited(e -> newGame.setStyle(getIdleButtonStyle()));
        newGame.prefWidthProperty().bind(Bindings.divide(window.widthProperty(), 3.5));
        newGame.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Stage primaryStage = (Stage) getScene().getWindow();
                startNewGame(primaryStage);
            }
        });

        return newGame;
    }

    private Button createExitGameButton(Stage window) {
        Button endGame = new Button("Exit");
        endGame.setStyle(getIdleButtonStyle());
        endGame.setOnMouseEntered(e -> endGame.setStyle(getHoverButtonStyle()));
        endGame.setOnMouseExited(e -> endGame.setStyle(getIdleButtonStyle()));
        endGame.prefWidthProperty().bind(Bindings.divide(window.widthProperty(), 3.5));
        endGame.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Stage primaryStage = (Stage) getScene().getWindow();
                primaryStage.close();
            }
        });

        return endGame;
    }

    private Text createTitle(GameState gameState, Stage window) {
        Blend blend = getTitleEffect();
        String textContent = gameState.result() == GameResult.VICTORY ? gameState.armyNotOnTurn().side().name().toUpperCase() + " has won!" : "It's a draw!";
        Text text = new Text(textContent);
        DoubleProperty fontSize = new SimpleDoubleProperty(20);
        fontSize.bind(Bindings.divide(Bindings.add(window.widthProperty(), window.heightProperty()), 20));
        text.setFont(Font.font(null, FontWeight.BOLD, Double.parseDouble(fontSize.getValue().toString())));
        text.setEffect(blend);
        text.setFill(Color.YELLOW);

        return text;
    }
}
