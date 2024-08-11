package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.RpaSignCode;

import java.util.List;

public class RpaSignDescStrRule {

    public RpaSignDescStrRule() {
    }

    /**
     * Note: strDesc should be cleaned before calling this methode
     * cleaning should exclude `-`, `,`, `+`, `;` and `ET`
     */
    public static List<String> divider(String strDesc, RpaSignCode code) {

        switch (code) {
            case SLR_ST_75: // ex : '9H À 17H LUN MER VEN 15 NOV AU 15 MARS, 11H À 12H MERCREDI 15 MARS AU 15 NOV'
                return List.of(strDesc.split(","));

            case SS_JM: // ex : '\P 07h-16h LUN A VEN ET 07h-12h SAMEDI'
                return List.of(strDesc.split("ET"));
            case SD_OP: // ex : '\P 18h-24h LUN A VEN  +  08h-24h SAM ET DIM'
                return List.of(strDesc.split("\\+"));

            case SLR_ST_98, SLR_ST_82, SLR_ST_84:
                // ex : '9H À 17H MAR JEU 15 NOV AU 15 MARS - 11H À 12H JEUDI 15 MARS AU 15 NOV'
                // ex : 'LUN MER VEN 8H À 12H - MAR JEU 13H À 17H'
                return List.of(strDesc.split("-"));

            case SLR_ST_111, SLR_ST_172, SLR_ST_174, SLR_ST_175, SLR_ST_80, SLR_ST_81:
                // ex : '17H MAR À 17H MER; 17H JEU À 17H VEN; 17H SAM À 17H LUN'
                // or '\P 17H MAR À 17H MER; \P 17H JEU À 17H VEN; \P 17H SAM À 17H LUN'
                // or '120 MIN - LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H'
                return List.of(strDesc.split(";"));


            case SB_NX: // TODO ex: '\P 23h30-00h30  MAR A MER, VEN A SAM  1 MARS AU 1 DEC. '
                return List.of(strDesc);
            case SB_NX_A: // TODO ex: '\P 23h30-00h30  MAR A MER, VEN A SAM  1 AVRIL AU 1 DEC.'
                return List.of(strDesc);
            case SB_NY: // TODO ex: '\P 23h30-00h30 LUN A MAR, JEU A VEN  1 MARS AU 1 DEC.'
                return List.of(strDesc);
            case SB_NY_A: // TODO ex: '\P 23h30-00h30 LUN A MAR, JEU A VEN  1 AVRIL AU 1 DEC.'
                return List.of(strDesc);


            case SLR_ST_79: // TODO ex : '8H À 12H LUN MER VEN 13H À 18H MAR JEU'
                return List.of(strDesc);
            case SLR_ST_105: // TODO ex : '8H À 12H MAR JEU 13H À 17H LUN MER VEN'
                return List.of(strDesc);
            case SLR_ST_106: // TODO ex : '8H À 12H MAR JEU 13H À 18H LUN MER VEN'
                return List.of(strDesc);

            case SLR_ST_107: // TODO ex : 'MAR JEU 8H À 12H LUN MER VEN 14H À 17H'
                return List.of(strDesc);
            case SLR_ST_135: // TODO ex : '30 MIN - MAR MER VEN - 9H À 16H30 - LUN JEU - 12H À 16H30'
                return List.of(strDesc);


            default:
                return List.of(strDesc);
        }
    }
}
