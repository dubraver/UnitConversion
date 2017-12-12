/*
 * Copyright (C) 2017 Nikolay Dyundik
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package unitconversion;

import java.util.Objects;

/**
 *
 * @author Nikolay Dyundik
 */
public class Expression {

    public final ExpressionType type;
    public final double v1;
    public final String u1;
    public final double v2;
    public final String u2;

    public Expression(ExpressionType type, double v1, String u1, double v2, String u2) {
        this.type = type;
        this.v1 = v1;
        this.u1 = u1;
        this.v2 = v2;
        this.u2 = u2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Expression)) {
            return false;
        }
        Expression exp = (Expression) obj;

        return exp.u1.equals(this.u1)
                && exp.u2.equals(this.u2)
                && Double.compare(exp.v1, this.v1) == 0
                && Double.compare(exp.v2, this.v2) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.v1) ^ (Double.doubleToLongBits(this.v1) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.u1);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.v2) ^ (Double.doubleToLongBits(this.v2) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.u2);
        return hash;
    }

    @Override
    public String toString() {
        String result = String.format("%f %s = %f %s", this.v1, this.u1, this.v2, this.u2);
        return this.type != ExpressionType.CONVERSION_NOT_POSSIBLE ? result : "Conversion not possible.";
    }
}

enum ExpressionType {
    CALCULATE, GENERATE_UNITS, COMPUTED, CONVERSION_NOT_POSSIBLE
}
