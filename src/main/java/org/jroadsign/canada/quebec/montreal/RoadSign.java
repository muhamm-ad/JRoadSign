package org.jroadsign.canada.quebec.montreal;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 * @description Represents a road sign with various attributes such as position, ID, RPA (Regulation Par
 *         Arrondissement) sign, arrow code, toponymic, category description, description of the REP (Regulation
 *         Explanation Panel), and image.
 */
public class RoadSign implements Comparable<RoadSign> {

    public static final int IMAGE_WIDTH = 50;
    public static final int IMAGE_HEIGHT = 60;
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
    private final String toponymic; // Toponym code of the RoadSign
    private final String categoryDescription; // Description of the category of the RoadSign // TO_UPDATE
    private final BufferedImage image = null;
    private RepDescription repDescription; // Description of the REP (Regulation Explanation Panel) of the RoadSign

    public enum RepDescription {
        REAL, REMOVED, ARCHIVED, IN_DESIGN
    }

    public enum Direction {
        NONE, RIGHT, LEFT, LEFT_AND_RIGHT
        // TO_UPDATE : add all possible direction
    }

    public RoadSign(int position, long id, RpaSign rpaSign, int arrowCode, String toponymic, String categoryDescription, String repDescription) {
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
            default -> Direction.NONE;
        };
    }

    public String getArrowStrDirrection() {
        return switch (arrowCode) {
            case 2 -> "LEFT";
            case 3 -> "RIGHT";
            case 8 -> "LEFT_AND_RIGHT";
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

    private void setRepDescription(@NotNull String repDes) {
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
    public int compareTo(@NotNull RoadSign o) {
        return Long.compare(this.id, o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoadSign roadSign = (RoadSign) o;
        return position == roadSign.position &&
                id == roadSign.id &&
                arrowCode == roadSign.arrowCode &&
                Objects.equals(rpaSign, roadSign.rpaSign) &&
                Objects.equals(toponymic, roadSign.toponymic) &&
                Objects.equals(categoryDescription, roadSign.categoryDescription) &&
                // Objects.equals(image, roadSign.image) &&
                repDescription == roadSign.repDescription;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, id, rpaSign, arrowCode, toponymic, categoryDescription, repDescription);
    }


    @Override
    public String toString() {
        return "RoadSign{" +
                "position=" + position +
                ", id=" + id +
                ", rpaSign=" + rpaSign +
                ", arrowCode=" + arrowCode +
                ", toponymic='" + toponymic + '\'' +
                ", categoryDescription='" + categoryDescription + '\'' +
                ", image=" + image +
                ", repDescription=" + repDescription +
                '}';
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("position", position);
        json.put("id", id);
        json.put("rpaSign", rpaSign != null ? rpaSign.toJson() : JSONObject.NULL);
        json.put("arrowCode", arrowCode);
        json.put("arrowDirection", getArrowStrDirrection());
        json.put("toponymic", toponymic != null ? toponymic : JSONObject.NULL);
        json.put("categoryDescription", categoryDescription != null ? categoryDescription : JSONObject.NULL);
        json.put("repDescription", repDescription != null ? repDescription.toString() : JSONObject.NULL); // FIXME
        // json.put("image", image != null ? encodeImageToBase64(image) : JSONObject.NULL);

        return json;
    }

    /*
    private String encodeImageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }
    */
}
