// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.util.ArrayList;
import java.util.List;

public class RpaSignDesc {
    private final String strDescription;
    private List<RpaSignDescRule> rpaSignDescRules;

    public RpaSignDesc(String strDescription, List<RpaSignDescRule> rpaSignDescRules) {
        this.strDescription = strDescription;
        this.rpaSignDescRules = rpaSignDescRules;
    }

    public RpaSignDesc(List<String> rpaSignDescStrRules, String strDescription) {
        if (rpaSignDescStrRules == null) throw new IllegalArgumentException("sDescriptions cannot be null");
        this.strDescription = strDescription;
        rpaSignDescRules = new ArrayList<>();

        for (String strRule : rpaSignDescStrRules) {
            String cleanedStrRule = RoadSignDescCleaner.cleanDescription(strRule.trim());
            rpaSignDescRules.add(new RpaSignDescRule(cleanedStrRule));
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
