package View.GUI;

import Model.Enums.Direction;
import Model.Ship.Components.SpaceshipComponent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads (and caches) the game pictures bundled in src/main/resources/pics.
 */
final class Images {

    private static final String TILE_BACK = "pics/tiles/0.jpg";
    private static final Map<String, Image> cache = new ConcurrentHashMap<>();

    private Images() {}

    /**
     * @param resource a classpath resource, e.g. "pics/tiles/12.jpg"
     * @return the image, or null if the resource does not exist
     */
    static Image load(String resource) {
        if (resource == null) {
            return null;
        }
        return cache.computeIfAbsent(resource, name -> {
            try (InputStream stream = Images.class.getClassLoader().getResourceAsStream(name)) {
                return stream == null ? null : new Image(stream);
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * Resolves an image path stored in the model (e.g. "src/main/resources/pics/tiles/12.png").
     * The model paths do not always match the file extension on disk, so both .jpg and .png are tried.
     */
    static Image fromModelPath(String modelPath) {
        if (modelPath == null || modelPath.isBlank()) {
            return null;
        }
        String resource = modelPath.replace('\\', '/');
        final int picsIndex = resource.indexOf("pics/");
        if (picsIndex >= 0) {
            resource = resource.substring(picsIndex);
        }
        final int dot = resource.lastIndexOf('.');
        final String base = dot >= 0 ? resource.substring(0, dot) : resource;

        Image image = load(base + ".jpg");
        if (image == null) {
            image = load(base + ".png");
        }
        return image;
    }

    /**
     * @return the face of the component, or its back if it has not been revealed yet
     */
    static Image tile(SpaceshipComponent component, boolean faceUp) {
        final Image face = faceUp ? fromModelPath(component.getImagePath()) : null;
        return face != null ? face : load(TILE_BACK);
    }

    /**
     * @return an ImageView of the component, sized and rotated as it is placed
     */
    static ImageView tileView(SpaceshipComponent component, boolean faceUp, double size) {
        final ImageView view = new ImageView(tile(component, faceUp));
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        if (faceUp) {
            view.setRotate(rotation(component.getOrientation()));
        }
        return view;
    }

    /**
     * Orientation of a component expressed as a clockwise rotation in degrees (UP = not rotated).
     */
    static double rotation(Direction orientation) {
        if (orientation == null) {
            return 0;
        }
        return switch (orientation) {
            case UP -> 0;
            case RIGHT -> 90;
            case DOWN -> 180;
            case LEFT -> 270;
        };
    }
}
