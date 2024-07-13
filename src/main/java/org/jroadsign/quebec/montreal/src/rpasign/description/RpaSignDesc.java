package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.RpaSignCode;

import java.util.ArrayList;
import java.util.List;

public class RpaSignDesc {
    private final String strDescription;
    private List<RpaSignDescRule> rpaSignDescRules;

    public RpaSignDesc(String strDescription, List<RpaSignDescRule> rpaSignDescRules) {
        this.strDescription = strDescription;
        this.rpaSignDescRules = rpaSignDescRules;
    }

    public RpaSignDesc(String strDescription, RpaSignCode code) { // FIXME : remove `code`
        this.strDescription = strDescription;
        rpaSignDescRules = new ArrayList<>();

        String cleanedStrDescription = RoadSignDescCleaner.cleanDescription(strDescription, code);
        List<String> rpaSignDescStrRules = RpaSignDescStrRule.divider(cleanedStrDescription, code);

        for (String strRule : rpaSignDescStrRules) {
            rpaSignDescRules.add(new RpaSignDescRule(strRule));
        }
    }

    public String getStrDescription() {
        return strDescription;
    }

    public List<RpaSignDescRule> getRpaSignDescRules() {
        return rpaSignDescRules;
    }

    @Override
    public String toString() {
        return "RpaSignDesc{" +
                "strDescription='" + strDescription + '\'' +
                ", rpaSignDescRules=" + rpaSignDescRules +
                '}';
    }
}
