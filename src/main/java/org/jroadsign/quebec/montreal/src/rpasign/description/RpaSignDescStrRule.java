package org.jroadsign.quebec.montreal.src.rpasign.description;

import org.jroadsign.quebec.montreal.src.rpasign.RpaSignCode;

import java.util.List;

public class RpaSignDescStrRule {

    public RpaSignDescStrRule() {
    }

    /**
     * Note: strDesc should be cleaned before calling this methode
     * cleaning exclude '-', ','
     */
    public static List<String> divider(String strDesc, RpaSignCode code) {

        switch (code) {
            case SLR_ST_75: // ex : '9H À 17H LUN MER VEN 15 NOV AU 15 MARS, 11H À 12H MERCREDI 15 MARS AU 15 NOV'
                return List.of(strDesc.split(","));
            case SLR_ST_98: // ex : '9H À 17H MAR JEU 15 NOV AU 15 MARS - 11H À 12H JEUDI 15 MARS AU 15 NOV'
                return List.of(strDesc.split("-"));

            case SLR_ST_82: // ex : 'LUN MER VEN 8H À 12H - MAR JEU 13H À 17H'
                return List.of(strDesc.split("-"));
            case SLR_ST_84: // ex : 'LUN MER VEN 8H À 12H - MAR JEU 14H À 17H'
                return List.of(strDesc.split("-"));

            case SS_JM: // ex : '\P 07h-16h LUN A VEN ET 07h-12h SAMEDI'
                return List.of(strDesc.split("ET"));

            case SD_OP: // ex : '\P 18h-24h LUN A VEN  +  08h-24h SAM ET DIM'
                return List.of(strDesc.split("\\+"));


            case SLR_ST_111: // FIXME ex : '17H MAR À 17H MER - 17H JEU À 17H VEN - 17H SAM À 17H LUN'
                return List.of(strDesc.split("-"));

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

            case SB_NX: // TODO ex: '\P 23h30-00h30  MAR A MER, VEN A SAM  1 MARS AU 1 DEC. '
                return List.of(strDesc);
            case SB_NX_A: // TODO ex: '\P 23h30-00h30  MAR A MER, VEN A SAM  1 AVRIL AU 1 DEC.'
                return List.of(strDesc);
            case SB_NY: // TODO ex: '\P 23h30-00h30 LUN A MAR, JEU A VEN  1 MARS AU 1 DEC.'
                return List.of(strDesc);
            case SB_NY_A: // TODO ex: '\P 23h30-00h30 LUN A MAR, JEU A VEN  1 AVRIL AU 1 DEC.'
                return List.of(strDesc);

            case SLR_ST_172: // TODO ex: '120 MIN - LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H'
                return List.of(strDesc);
            case SLR_ST_174: // TODO ex: '120 MIN - MAR 17H À MER 17H - JEU 17H À VEN 17H - SAM 17H À LUN 17H'
                return List.of(strDesc);
            case SLR_ST_175: // TODO ex: '120 MIN - MAR 18H À MER 18H - JEU 18H À VEN 18H - SAM 18H À LUN 18H'
                return List.of(strDesc);
            case SLR_ST_80: // TODO ex: 'LUN 17H À MAR 17H - MER 17H À JEU 17H - VEN 17H À SAM 17H'
                return List.of(strDesc);
            case SLR_ST_81: // TODO ex: 'LUN 18H À MAR 18H - MER 18H À JEU 18H - VEN 18H À SAM 18H'
                return List.of(strDesc);

            default:
                return List.of(strDesc);
        }
    }
}
