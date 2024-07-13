package org.jroadsign.quebec.montreal.src;

import org.jroadsign.quebec.montreal.src.rpasign.RpaSign;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;

/**
 * Represents a RoadSign record with details like position, panel ID, RPA, arrow code,
 * toponymic code, and category and REP descriptions.
 */
public class RoadSign implements Comparable<RoadSign> {

    public static final int IMAGE_WIDTH = 50;
    public static final int IMAGE_HEIGHT = 60;
    private static final Logger LOGGER = Logger.getLogger(RoadSign.class.getName());
    public static BufferedImage errorImage;

    static {
        try {
            File path = new File("src/main/resources/common/images/error.png");
            BufferedImage originalImage = ImageIO.read(path);

            Image resizedImage = originalImage.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);

            // Create a new BufferedImage with the desired size and type
            BufferedImage fixedSizeImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            // Draw the resized image onto the fixedSizeImage
            Graphics2D g2d = fixedSizeImage.createGraphics();
            g2d.drawImage(resizedImage, 0, 0, null);
            g2d.dispose();

            // Assign the fixedSizeImage to the errorImage variable
            errorImage = fixedSizeImage;

        } catch (Exception e1) {
            errorImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        }
    }

    private final int position; // Identifier for the position of the RoadSign
    private final long id; // Identifier number of the RoadSign
    private final RpaSign rpaSign; // RPA of the RoadSign
    private final int arrowCode; // Code of the RoadSign's arrow
    private final String toponymic; // Toponymic code of the RoadSign
    private final String categoryDescription; // Description of the category of the RoadSign // UPDATE
    private final BufferedImage image = null;
    private RepDescription repDescription; // Description of the REP (Regulation Explanation Panel) of the RoadSign

    public RoadSign(int position, long id, RpaSign rpaSign, int arrowCode, String toponymic,
                    String categoryDescription, String repDescription) {
        this.position = position;
        this.id = id;
        this.rpaSign = rpaSign;
        this.arrowCode = arrowCode;
        this.toponymic = toponymic;
        this.categoryDescription = categoryDescription;
        setRepDescription(repDescription);
        // this.image = this.repDescription != RepDescription.REEL ? emptyImage : setImage(rpa);
    }

    private BufferedImage setImage(RpaSign rpaSign) {
        BufferedImage img = null;

        // TODO Generate image

        return img;
    }

    public int getPosition() {
        return position;
    }

    public long getId() {
        return id;
    }

    public RpaSign getRpaSign() {
        return rpaSign;
    }

    public int getArrowCode() {
        return arrowCode;
    }

    public Direction getArrowDirrection() {
        return switch (arrowCode) {
            case 2 -> Direction.LEFT;
            case 3 -> Direction.RIGHT;
            case 8 -> Direction.LEFT_AND_RIGHT;
            // UPDATE : add all possible dirrection
            default -> Direction.NONE;
        };
    }

    public String getArrowStrDirrection() {
        return switch (arrowCode) {
            case 2 -> "LEFT";
            case 3 -> "RIGHT";
            case 8 -> "LEFT_AND_RIGHT";
            // UPDATE : add all possible dirrection
            default -> "";
        };
    }

    public String getToponymic() {
        return toponymic;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public RepDescription getRepDescription() {
        return repDescription;
    }

    private void setRepDescription(String repDes) {
        switch (repDes) {
            case "Réel" -> repDescription = RepDescription.REAL;
            case "Enlevé" -> repDescription = RepDescription.REMOVED;
            case "En conception" -> repDescription = RepDescription.IN_DESIGN;
            default -> repDescription = RepDescription.ARCHIVED;
        }
    }

    public boolean isReal() {
        return (repDescription == RepDescription.REAL);
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public int compareTo(RoadSign otherRoadSign) {
        return Long.compare(this.id, otherRoadSign.id);
    }

    // TODO add is*** the rest of the attribut of RepDescription

    @Override
    public String toString() {
        return "RoadSign{" +
                "id=" + id +
                ", position=" + position +
                ", " + rpaSign +
                ", arrowCode=" + arrowCode + "(" + getArrowStrDirrection() + ")" +
                ", toponymic='" + toponymic + '\'' +
                ", categoryDescription='" + categoryDescription + '\'' +
                ", repDescription='" + repDescription + '\'' +
                '}';
    }

    public enum RepDescription {
        REAL,
        REMOVED,
        ARCHIVED,
        IN_DESIGN
    }

    public enum Direction {
        NONE,
        RIGHT,
        LEFT,
        LEFT_AND_RIGHT
        // UPDATE : add all possible dirrection
    }
}
