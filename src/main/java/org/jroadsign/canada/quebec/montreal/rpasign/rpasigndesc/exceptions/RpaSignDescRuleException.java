package org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.exceptions;

import org.jroadsign.canada.quebec.montreal.rpasign.rpasigndesc.RpaSignDescRule;

public class RpaSignDescRuleException extends Exception {
    private final RpaSignDescRule rpaSignDescRule;

    public RpaSignDescRuleException(RpaSignDescRule rpaSignDescRule) {
        this.rpaSignDescRule = rpaSignDescRule;
    }

    public RpaSignDescRule getRpaSignDescRule() {
        return rpaSignDescRule;
    }
}