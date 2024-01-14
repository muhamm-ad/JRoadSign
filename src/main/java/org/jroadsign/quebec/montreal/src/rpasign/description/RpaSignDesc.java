// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign.description;

import java.util.ArrayList;
import java.util.List;

public class RpaSignDesc {
    private final String stringDescription;
    private List<RpaSignDescRule> rpaSignDescRules;
    private List<String> additionalMetaDataList;

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

    public RpaSignDesc(List<String> signDescriptions) {
        if (signDescriptions == null) throw new IllegalArgumentException("sDescriptions cannot be null");
        StringBuilder descBuilder = new StringBuilder();
        rpaSignDescRules = new ArrayList<>();
        additionalMetaDataList = new ArrayList<>();
        processSignDescriptions(signDescriptions, descBuilder);

        stringDescription = descBuilder.toString();
        for (RpaSignDescRule rule : rpaSignDescRules) {
            addAdditionalMetaData(rule);
        }
    }

    public RpaSignDesc(List<String> signDescriptions, String stringDescription) {
        if (signDescriptions == null) throw new IllegalArgumentException("sDescriptions cannot be null");
        this.stringDescription = stringDescription;
        rpaSignDescRules = new ArrayList<>();
        additionalMetaDataList = new ArrayList<>();
        processSignDescriptions(signDescriptions, null);
    }

    private void processSignDescriptions(List<String> signDescriptions, StringBuilder descBuilder) {
        for (String desc : signDescriptions) {
            if (desc != null) {
                if (descBuilder != null) descBuilder.append(desc);
                String descRule = RoadSignDescCleaner.cleanDescription(desc);
                rpaSignDescRules.add(new RpaSignDescRule(descRule));
            }
        }
    }

    private void addAdditionalMetaData(RpaSignDescRule rule) {
        if (rule.getRuleAdditionalMetaData() != null) {
            additionalMetaDataList.add(rule.getRuleAdditionalMetaData());
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
