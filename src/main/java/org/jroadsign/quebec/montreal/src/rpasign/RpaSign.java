// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign;

/**
 * Represents an RPA (Regulation Par Arrondissement) of Sign record with ID, description, and code.
 */
public record RpaSign(long idRpaSign, String descriptionRpaSign, String codeRpaSign) {

    @Override
    public String toString() {
        return "RpaSign{" +
                "idRpaSign=" + idRpaSign +
                ", descriptionRpaSign='" + descriptionRpaSign + '\'' +
                ", codeRpaSign='" + codeRpaSign + '\'' +
                '}';
    }
}
