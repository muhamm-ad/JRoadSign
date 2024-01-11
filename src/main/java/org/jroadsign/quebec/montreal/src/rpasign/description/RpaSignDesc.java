// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RpaSignDesc {
    private final String stringDescription;
    private List<RpaSignDescRule> rpaSignDescRules;
    private List<String> additionalMetaDataList;


    private static final String DESC_RULE_PATTERN =
            String.format(GlobalConfigs.DAY_TIME_RANGE_PATTERN, "\\s*", "\\s*(AU?|-)\\s*") + "\\s*"
                    + GlobalConfigs.WEEKLY_DAYS_RANGE_EXPRESSION_PATTERN + "\\s*"
                    + GlobalConfigs.ANNUAL_MONTH_RANGE_PATTERN + "\\s*";
    private static final String COMBINED_DESC_RULE_PATTERN = DESC_RULE_PATTERN + "(," + DESC_RULE_PATTERN + ")*";
    private static final Pattern COMPILED_COMBINED_DESC_RULE_PATTERN = Pattern.compile(
            "\\b" + COMBINED_DESC_RULE_PATTERN + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);


    public RpaSignDesc(String sDescription) {
        if (sDescription == null) throw new IllegalArgumentException("sDescriptions cannot be null");

        stringDescription = sDescription;
        rpaSignDescRules = new ArrayList<>();
        additionalMetaDataList = new ArrayList<>();

        String descRule = RoadSignDescCleaner.cleanDescription(sDescription);
        rpaSignDescRules.add(new RpaSignDescRule(descRule));


        String addInfo = rpaSignDescRules.get(0).getRuleAdditionalMetaData();
        if (addInfo != null) {
            additionalMetaDataList.add(addInfo);
        }
    }

    public RpaSignDesc(List<String> sDescriptions) {
        if (sDescriptions == null) throw new IllegalArgumentException("sDescriptions cannot be null");

        StringBuilder descBuilder = new StringBuilder();
        rpaSignDescRules = new ArrayList<>();
        additionalMetaDataList = new ArrayList<>();

        for (String desc : sDescriptions) {
            if (desc != null) {
                descBuilder.append(desc);
                String descRule = RoadSignDescCleaner.cleanDescription(desc);
                rpaSignDescRules.add(new RpaSignDescRule(descRule));
            }
        }

        stringDescription = descBuilder.toString();

        for (RpaSignDescRule rule : rpaSignDescRules) {
            if (rule.getRuleAdditionalMetaData() != null) {
                additionalMetaDataList.add(rule.getRuleAdditionalMetaData());
            }
        }
    }


    public String getStringDescription() {
        return stringDescription;
    }

    public List<RpaSignDescRule> getRpaSignDescRules() {
        return rpaSignDescRules;
    }

    public List<String> getAdditionalMetaDataList() {
        return additionalMetaDataList;
    }

    @Override
    public String toString() {
        return "RpaSignDesc{" +
                "stringDescription='" + stringDescription + '\'' +
                ", rpaSignDescRules=" + rpaSignDescRules +
                ", additionalMetaData='" + additionalMetaDataList + '\'' +
                '}';
    }
}
