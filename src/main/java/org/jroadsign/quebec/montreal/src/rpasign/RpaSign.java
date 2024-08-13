package org.jroadsign.quebec.montreal.src.rpasign;

import org.jroadsign.quebec.montreal.src.rpasign.description.RoadSignDescCleaner;
import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDesc;

/**
 * Represents an RPA (Regulation Par Arrondissement) of Sign with ID, description, and code.
 */
public class RpaSign {

    private long id;
    private RpaSignDesc description;
    private RpaSignCode code;

    public RpaSign(long id, RpaSignDesc description, RpaSignCode code) {
        this.id = id;
        this.description = description;
        this.code = code;
    }

    public RpaSign(long id, String sDescription, String sCode) {
        this.id = id;
        this.code = RpaSignCode.fromString(sCode);

        String sDescriptionCleaned = RoadSignDescCleaner.cleanDescription(sDescription, code);
        this.description = new RpaSignDesc(sDescriptionCleaned, sDescription);
    }

    public long getId() {
        return id;
    }

    public RpaSignDesc getDescription() {
        return description;
    }

    public RpaSignCode getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "RpaSign{" +
                "id=" + id +
                ", description=" + description +
                ", code='" + code.getStr() + '\'' +
                '}';
    }
}

