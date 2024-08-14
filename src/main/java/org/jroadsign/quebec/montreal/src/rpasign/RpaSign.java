package org.jroadsign.quebec.montreal.src.rpasign;

import org.jroadsign.quebec.montreal.src.rpasign.description.RoadSignDescCleaner;
import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDesc;

/**
 * Represents an RPA (Regulation Par Arrondissement) of Sign with ID, description, and code.
 */
public class RpaSign {

    private long id;
    private RpaSignCode code;
    private RpaSignDesc description;

    public RpaSign(long id, RpaSignCode code, RpaSignDesc description) {
        this.id = id;
        this.description = description;
        this.code = code;
    }

    public RpaSign(long id, String sCode, String sDescription) {
        this.id = id;
        this.code = RpaSignCode.fromString(sCode);

        String sDescriptionCleaned = RoadSignDescCleaner.cleanDescription(sDescription, code);
        this.description = new RpaSignDesc(sDescriptionCleaned, sDescription);
    }

    public long getId() {
        return id;
    }

    public RpaSignCode getCode() {
        return code;
    }

    public RpaSignDesc getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "RpaSign{" +
                "id=" + id +
                ", code='" + code.getStr() + '\'' +
                ", description=" + description +
                '}';
    }

    public String toJson() {
        return "{" +
                "\"id\": " + id +
                ",\"code\": \"" + code.toString() + "\"" +
                ",\"description\": " + description.toJson() +
                "}";
    }
}

