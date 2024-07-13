package org.jroadsign.quebec.montreal.src.rpasign;

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
        this.description = new RpaSignDesc(sDescription, this.code);
    }

    public long getId() {
        return id;
    }

    public RpaSignDesc getDescription() {
        return description;
    }

    public String getStringDescription() {
        return description.getStrDescription();
    }

    public RpaSignCode getCode() {
        return code;
    }

    public String getStringCode() {
        return code.getCode();
    }

    @Override
    public String toString() {
        return "RpaSign{" +
                "id=" + id +
                ", description=" + description +
                ", code='" + code.getCode() + '\'' +
                '}';
    }
}

