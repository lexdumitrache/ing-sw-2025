package View.GUI;

import Controller.Enums.MatchLevel;
import Model.Board.FlightBoard;
import Model.Player;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

/**
 * Draws the flight board artwork with a rocket marker on each player's space.
 * Space i of the model is the i-th triangle clockwise from the start space labelled "4".
 */
final class FlightBoardPane extends Pane {

    /** Centers of the 18 spaces on pics/cardboard/cardboard-3.png (985 x 546). */
    private static final int[][] TRIAL_SPACES = {
            {262, 124}, {354, 94}, {447, 81}, {540, 80}, {632, 93}, {720, 121}, {799, 179}, {847, 285}, {796, 369},
            {711, 422}, {619, 451}, {526, 465}, {433, 466}, {341, 453}, {253, 425}, {174, 368}, {126, 261}, {176, 177}};

    /** Centers of the 24 spaces on pics/cardboard/cardboard-5.png (1055 x 639). */
    private static final int[][] LEVEL2_SPACES = {
            {257, 153}, {333, 123}, {413, 104}, {490, 96}, {570, 96}, {648, 104}, {724, 122}, {797, 152},
            {863, 202}, {912, 283}, {912, 372}, {857, 436}, {784, 482}, {708, 512}, {629, 530}, {549, 539},
            {469, 540}, {392, 531}, {316, 513}, {242, 483}, {177, 432}, {127, 350}, {129, 262}, {185, 198}};

    /** Rocket colors, assigned in the order players joined the game. */
    static final List<Color> ROCKET_COLORS = List.of(
            Color.web("#e8453c"), Color.web("#f2c230"), Color.web("#3fbf5f"), Color.web("#3b8cf0"));

    private final double width;

    FlightBoardPane(double width) {
        this.width = width;
    }

    /**
     * @param board    the flight board to draw
     * @param level    the match level, which decides the board picture
     * @param players  all players, in join order (decides the rocket colors)
     * @param inTurn   name of the player currently acting, or null
     */
    void show(FlightBoard board, MatchLevel level, List<Player> players, String inTurn) {
        getChildren().clear();

        final boolean trial = level == MatchLevel.TRIAL;
        final Image image = Images.load(trial ? "pics/cardboard/cardboard-3.png" : "pics/cardboard/cardboard-5.png");
        final int[][] spaces = trial ? TRIAL_SPACES : LEVEL2_SPACES;
        if (image == null) {
            return;
        }

        final double scale = width / image.getWidth();
        final ImageView view = new ImageView(image);
        view.setFitWidth(width);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        getChildren().add(view);
        setPrefSize(width, image.getHeight() * scale);
        setMinSize(width, image.getHeight() * scale);

        final double radius = 15 * Math.max(scale, 0.6);
        for (Player player : board.getTurnOrder()) {
            final int position = Math.floorMod(board.getPosition(player), spaces.length);
            final Color color = colorOf(players, player.getName());

            final Circle rocket = new Circle(radius, color);
            rocket.setStroke(player.getName().equals(inTurn) ? Color.WHITE : Color.BLACK);
            rocket.setStrokeWidth(player.getName().equals(inTurn) ? 3 : 1.5);

            final Label initial = new Label(player.getName().substring(0, 1).toUpperCase());
            initial.getStyleClass().add("rocket-label");

            final StackPane marker = new StackPane(rocket, initial);
            marker.relocate(spaces[position][0] * scale - radius, spaces[position][1] * scale - radius);
            Tooltip.install(marker, new Tooltip(player.getName() + ": space " + position
                    + ", " + board.getTotalDistance(player) + " travelled"));
            getChildren().add(marker);
        }
    }

    static Color colorOf(List<Player> players, String name) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(name)) {
                return ROCKET_COLORS.get(i % ROCKET_COLORS.size());
            }
        }
        return Color.GRAY;
    }
}
