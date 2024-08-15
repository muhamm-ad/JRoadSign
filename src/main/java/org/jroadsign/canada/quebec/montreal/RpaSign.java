package org.jroadsign.canada.quebec.montreal;

import org.jroadsign.canada.quebec.montreal.rpasign.RpaSignCode;
import org.jroadsign.canada.quebec.montreal.rpasign.RpaSignDesc;
import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.RoadSignDescCleaner;
import org.json.JSONObject;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 * @description Represents an RPA (Regulation Par Arrondissement) of Sign with ID, description, and code.
 */
public class RpaSign {

    private final long id;
    private final RpaSignCode code;
    private final RpaSignDesc description;

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
                ", code=" + code +
                ", description=" + description +
                '}';
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("id", id);
        json.put("code", code != null ? code.getStr() : JSONObject.NULL);
        json.put("description", description != null ? description.toJson() : JSONObject.NULL);

        return json;
    }
}

