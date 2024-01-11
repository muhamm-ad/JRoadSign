// License: GPL-3.0. For details, see README.md file.

package org.jroadsign.quebec.montreal.src.rpasign;

import org.jroadsign.quebec.montreal.src.rpasign.description.RpaSignDesc;

/**
 * Represents an RPA (Regulation Par Arrondissement) of Sign record with ID, description, and code.
 */
public record RpaSign(long id, RpaSignDesc description, String code) {

    @Override
    public String toString() {
        return "RpaSign{" +
                "id=" + id +
                ", description=" + description +
                ", code='" + code + '\'' +
                '}';
    }
}
