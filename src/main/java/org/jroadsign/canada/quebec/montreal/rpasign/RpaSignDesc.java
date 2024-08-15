package org.jroadsign.canada.quebec.montreal.rpasign;

import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.RpaSignDescRule;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.RoadSignDescCleaner.RULE_SEPARATOR;

/**
 * @author - <a href="https://github.com/muhamm-ad">muhamm-ad</a>
 * @project JRoadSign
 */
public class RpaSignDesc {
    private final String strDescription;
    private final String strDescriptionCleaned;
    private final List<RpaSignDescRule> rpaSignDescRules;

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

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("Description", strDescription);
        json.put("DescriptionCleaned", strDescriptionCleaned);

        JSONArray rulesArray = new JSONArray();
        for (RpaSignDescRule rule : rpaSignDescRules) {
            rulesArray.put(rule.toJson());
        }
        json.put("rpaSignDescRules", rulesArray);

        return json;
    }
}
