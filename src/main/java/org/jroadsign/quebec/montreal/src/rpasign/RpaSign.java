// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign;

import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDesc;

import java.util.List;

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

    public RpaSign(long id, String description, String code) {
        this.id = id;
        this.code = RpaSignCode.fromString(code);
        initDescription(description);
    }

    private void initDescription(String description) {
        switch (code) {
            case SLR_ST_75:
                this.description = new RpaSignDesc(List.of(description.split(",")), description);
                break;
            default:
                this.description = new RpaSignDesc(description);
        }
    }

    public long getId() {
        return id;
    }

    public RpaSignDesc getDescription() {
        return description;
    }

    public String getStringDescription() {
        return description.getStringDescription();
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

