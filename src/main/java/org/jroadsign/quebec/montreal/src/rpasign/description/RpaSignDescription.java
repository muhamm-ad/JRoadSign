// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.description.common.GlobalConfigs;
import org.jroadsign.quebec.montreal.src.rpasign.description.common.ParseFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RpaSignDescription {
    private String stringDescription;
    private List<RpaSignDescRule> rpaSignDescRules;
    private List<String> additionalMetaDataList;


    private static final String DESC_RULE_PATTERN =
            String.format(GlobalConfigs.DAY_TIME_RANGE_PATTERN, "\\\\s*", "\\\\s*(AU?|-)\\\\s*") + "\\s*"
                    + GlobalConfigs.WEEKLY_DAYS_RANGE_EXPRESSION_PATTERN + "*\\s*"
                    + GlobalConfigs.ANNUAL_MONTH_RANGE_PATTERN + "\\s*";
    private static final String COMBINED_DESC_RULE_PATTERN = DESC_RULE_PATTERN + "(," + DESC_RULE_PATTERN + ")*";
    private static final Pattern COMPILED_COMBINED_DESC_RULE_PATTERN = Pattern.compile(
            "\\b" + COMBINED_DESC_RULE_PATTERN + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);


    public RpaSignDescription(String sDescription) {
        stringDescription = sDescription;
        rpaSignDescRules = new ArrayList<>();

        String sDesc = ParseFunctions.cleanDescription(sDescription);
        Matcher matcherCombinedRules = COMPILED_COMBINED_DESC_RULE_PATTERN.matcher(sDesc);

        if (matcherCombinedRules.find()) {
            for (String descRule : sDesc.split(",")) {
                rpaSignDescRules.add(new RpaSignDescRule(descRule.trim()));
            }
        } else {
            rpaSignDescRules.add(new RpaSignDescRule(sDesc.trim()));
        }

        additionalMetaDataList = new ArrayList<>();
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
        return "RpaSignDescription{" +
                "stringDescription='" + stringDescription + '\'' +
                ", rpaSignDescRules=" + rpaSignDescRules +
                ", additionalMetaData='" + additionalMetaDataList + '\'' +
                '}';
    }
}
