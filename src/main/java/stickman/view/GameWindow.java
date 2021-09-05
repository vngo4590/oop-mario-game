package stickman.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import observer.Observer;
import observer.Subject;
import stickman.entity.Entity;
import stickman.level.Level;
import stickman.model.GameEngine;
import stickman.model.GameRun;
import stickman.model.TimeElapse;

import java.security.Key;
import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * The window the Game exists within.
 * <h1>
 *     UPDATE:
 * </h1>
 * <ul>
 *      <li> Applied the Observer Pattern - Because the game window is managing the GUI aspect of
 *      the game so change in level should result in changes in this class.
 *      </li>
 * </ul>
 */
public class GameWindow implements Observer {

    /**
     * The distance from the top/bottom the player can be before the camera follows.
     */
    private static final double VIEWPORT_MARGIN_VERTICAL = 130.0;

    /**
     * The distance from the left/right side the player can be before the camera follows.
     */
    private static final double VIEWPORT_MARGIN = 280.0;

    /**
     * The width of the screen.
     */
    private final int width;

    /**
     * The height of the screen.
     */
    private final int height;

    /**
     * The current running scene.
     */
    private Scene scene;

    /**
     * The pane of the window on which sprites are projected.
     */
    private Pane pane;

    /**
     * The GameEngine of the game.
     */
    private GameEngine model;

    /**
     * A list of all the entities' views in the Game.
     */
    private List<EntityView> entityViews;

    /**
     * The background of the scene.
     */
    private BackgroundDrawer backgroundDrawer;

    /**
     * The x-offset of the camera.
     */
    private double xViewportOffset = 0.0;

    /**
     * The y-offset of the camera.
     */
    private double yViewportOffset = 0.0;

    /**
    * Timeline for the level
    * */
    private Timeline timeline;
    /**
    * Text object to represent the timer
    * */
    private Text timerText;
    /**
     * Text object to represent the current game point
     * */
    private Text currentPointText;
    /**
     * Text object to represent the total game point of previous levels
     * */
    private Text totalPointText;
    /**
     * Text object to represent the lives of the hero
     * */
    private Text heroLivesText;




    /**
     * Creates a new GameWindow object.
     * @param model The GameEngine of the game
     * @param width The width of the screen
     * @param height The height of the screen
     */
    public GameWindow(GameEngine model, int width, int height) {
        this.model = model;
        this.pane = new Pane();
        this.width = width;
        this.height = height;
        this.scene = new Scene(pane, width, height);
        // Now we attempt to attach the new level with the GameWindow
        Level tempLevel = this.model.getCurrentLevel();
        if (tempLevel instanceof Subject) {
            ((Subject) tempLevel).attach(this);
        }
        this.entityViews = new ArrayList<>();

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(model);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

        this.backgroundDrawer = new BlockedBackground();

        backgroundDrawer.draw(model, pane);
        drawTimer();

    }

    /**
     * Returns the scene.
     * @return The current scene
     */
    public Scene getScene() {
        return this.scene;
    }

    /**
     * Starts the game.
     */
    public void run() {
        timeline = new Timeline(new KeyFrame(Duration.millis(17),
                t -> this.draw()));

        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();
    }

    /**
    * Method that draws the Count of time on the screen
    * */
    private void drawTimer() {
        this.pane.getChildren().remove(timerText);
        Formatter fmt = new Formatter();
        fmt.format("Timer Count : %2d m %2d s",
                this.model.getTimer().convertTimeCountToDuration().toMinutes()%60,
                this.model.getTimer().convertTimeCountToDuration().toSeconds()%60);
        timerText = new Text(fmt.toString());
        timerText.setX(10);
        timerText.setY(20);
        timerText.setFont(Font.font("verdana", FontWeight.BLACK, 15));
        this.pane.getChildren().add(timerText);

    }
    /**
     * Method that draws the Count of the level point
     * */
    private void drawCurrentLevelPoint() {
        this.pane.getChildren().remove(currentPointText);
        Formatter fmt = new Formatter();
        fmt.format("Current Level Point : %2d",
                this.model.getCurrentLevelPoint());
        currentPointText = new Text(fmt.toString());
        currentPointText.setX(10);
        currentPointText.setY(40);
        currentPointText.setFont(Font.font("verdana", FontWeight.BLACK, 12));
        this.pane.getChildren().add(currentPointText);
    }
    /**
     * Method that draws the total of the level points
     * */
    private void drawTotalLevelPoint() {
        this.pane.getChildren().remove(totalPointText);
        Formatter fmt = new Formatter();
        fmt.format("Total Level Point : %2d",
                this.model.getCurrentTotalPoint());
        totalPointText = new Text(fmt.toString());
        totalPointText.setX(10);
        totalPointText.setY(60);
        totalPointText.setFont(Font.font("verdana", FontWeight.BLACK, 12));
        this.pane.getChildren().add(totalPointText);
    }
    /**
     * Method that draws the lives of the hero
     * */
    private void drawLifePointHero() {
        this.pane.getChildren().remove(heroLivesText);
        Formatter fmt = new Formatter();
        fmt.format("Lives : %d",
                this.model.getStickmanLives());
        heroLivesText = new Text(fmt.toString());
        heroLivesText.setX(this.width-100);
        heroLivesText.setY(20);
        heroLivesText.setFont(Font.font("verdana", FontWeight.BLACK, 15));
        this.pane.getChildren().add(heroLivesText);
    }


    /**
    * This method uses the 3 methods above and draw the timeline status on the screen
    * */
    private void drawPointStatus() {
        // Check if the game is still running
        if (this.model.getGameState() instanceof GameRun) {
            drawTimer();
            drawTotalLevelPoint();
            drawCurrentLevelPoint();
        }
        drawLifePointHero();
    }

    /**
     * Draws the game (and updates it).
     */
    private void draw() {
        model.tick();
        // Draw the game status
        drawPointStatus();

        List<Entity> entities = model.getCurrentLevel().getEntities();
        for (EntityView entityView: entityViews) {
            entityView.markForDelete();
        }
        double heroXPos = model.getCurrentLevel().getHeroX();
        heroXPos -= xViewportOffset;

        if (heroXPos < VIEWPORT_MARGIN) {
            if (xViewportOffset >= 0) { // Don't go further left than the start of the level
                xViewportOffset -= VIEWPORT_MARGIN - heroXPos;
                if (xViewportOffset < 0) {
                    xViewportOffset = 0;
                }
            }
        } else if (heroXPos > width - VIEWPORT_MARGIN) {
            xViewportOffset += heroXPos - (width - VIEWPORT_MARGIN);
        }

        double heroYPos = model.getCurrentLevel().getHeroY();
        heroYPos -= yViewportOffset;

        if (heroYPos < VIEWPORT_MARGIN_VERTICAL) {
            if (yViewportOffset >= 0) { // Don't go further up than the top of the level
                yViewportOffset -= VIEWPORT_MARGIN_VERTICAL - heroYPos;
            }
        } else if (heroYPos > height - VIEWPORT_MARGIN_VERTICAL) {
            yViewportOffset += heroYPos - (height - VIEWPORT_MARGIN_VERTICAL);
        }

        backgroundDrawer.update(xViewportOffset, yViewportOffset);

        for (Entity entity: entities) {
            boolean notFound = true;
            for (EntityView view: entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (EntityView entityView: entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }
        entityViews.removeIf(EntityView::isMarkedForDelete);
    }

    @Override
    public void update() {
        // Once the level updated it's status
        // We are going to redraw the floor
        this.backgroundDrawer.draw(this.model, this.pane);
    }
}
