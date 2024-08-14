package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalFunctions;

import java.util.ArrayList;
import java.util.List;

import static org.jroadsign.quebec.montreal.src.rpasign.description.RoadSignDescCleaner.RULE_SEPARATOR;

public class RpaSignDesc {
    private final String strDescription;
    private final String strDescriptionCleaned;
    private List<RpaSignDescRule> rpaSignDescRules;

    public RpaSignDesc(String strDescriptionCleaned) {
        this.strDescriptionCleaned = strDescriptionCleaned;
        this.strDescription = strDescriptionCleaned;

        this.rpaSignDescRules = new ArrayList<>();
        for (String strRule : strDescriptionCleaned.split(RULE_SEPARATOR)) {
            this.rpaSignDescRules.add(new RpaSignDescRule(strRule));
        }
    }

    public RpaSignDesc(String strDescriptionCleaned, String strDescription) {
        this.strDescriptionCleaned = strDescriptionCleaned;
        this.strDescription = strDescription;

        this.rpaSignDescRules = new ArrayList<>();
        for (String strRule : strDescriptionCleaned.split(RULE_SEPARATOR)) {
            this.rpaSignDescRules.add(new RpaSignDescRule(strRule));
        }
    }

    public String getStrDescription() {
        return strDescription;
    }

    public String getStrDescriptionCleaned() {
        return strDescriptionCleaned;
    }

    public List<RpaSignDescRule> getRpaSignDescRules() {
        return rpaSignDescRules;
    }

    @Override
    public String toString() {
        return "RpaSignDesc{" +
                "strDescription='" + strDescription + '\'' +
                ", strDescriptionCleaned='" + strDescriptionCleaned + '\'' +
                ", rpaSignDescRules=" + rpaSignDescRules +
                '}';
    }

    public String toJson() {
        return "{" +
                "\"strDescription\": \"" + strDescription.replace("\\P", "\\\\P").replace("\\p", "\\\\p") + "\"" +
                ",\"strDescriptionCleaned\": \"" + strDescriptionCleaned.replace("\\P", "\\\\P") + "\"" +
                ",\"rpaSignDescRules\": " + GlobalFunctions.getJsonList(rpaSignDescRules, RpaSignDescRule::toJson) +
                "}";

    }
}
