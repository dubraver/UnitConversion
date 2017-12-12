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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nikolay Dyundik
 */
public class UnitManager {

    private static final HashMap<String, Unit> UNITS = new HashMap<>();

    private UnitManager() {
    }

    public static Expression processExpression(Expression exp) throws Exception {
        if (exp.type == null) {
            throw new Exception("Unknown expression type");
        } else {
            switch (exp.type) {
                case GENERATE_UNITS:
                    generateUnits(exp);
                    return null;
                case CALCULATE:
                    return caclulateExpression(exp);
                default:
                    throw new Exception("Unknown expression type");
            }
        }
    }

    private static void generateUnits(Expression exp) {
        Unit unit1 = UNITS.get(exp.u1);
        if (unit1 == null) {
            unit1 = new Unit(exp.u1, exp.u2, exp.v1, exp.v2);
            UNITS.put(exp.u1, unit1);
        } else {
            unit1.addConversionRule(exp.u2, exp.v1, exp.v2);
        }

        Unit unit2 = UNITS.get(exp.u2);
        if (unit2 == null) {
            unit2 = new Unit(exp.u2, exp.u1, exp.v2, exp.v1);
            UNITS.put(exp.u2, unit2);
        } else {
            unit2.addConversionRule(exp.u1, exp.v2, exp.v1);
        }
    }

    private static Expression caclulateExpression(Expression exp) {

        ArrayList<Double> factors = new ArrayList<>();
        getFactors(0, exp.u1, exp.u2, exp.u1, exp.u2, exp.v1, factors);
        if (factors.isEmpty()) {
            return new Expression(ExpressionType.CONVERSION_NOT_POSSIBLE, exp.v1, exp.u1, exp.v2, exp.u2);
        } else {
            double quantity = factors.stream().mapToDouble(Double::doubleValue).reduce(1d, (x, y) -> x * y) * exp.v1;
            return new Expression(ExpressionType.COMPUTED, exp.v1, exp.u1, quantity, exp.u2);
        }
    }

    private static void getFactors(int iteration, String dimensionFrom, String dimensionTo,
            String rootDf, String rootT, double quantityFrom, ArrayList<Double> factors) {

        if (dimensionFrom.equals(rootDf) && dimensionTo.equals(rootT)
                && iteration > 0) {
            return;
        }
        Unit unit = UNITS.get(dimensionFrom);
        if (unit == null) {
            return;
        }

        double factor = unit.getFactor(dimensionTo);
        if (Double.compare(factor, 0d) != 0) {
            factors.add(factor);
            return;
        }

        HashMap<String, Double> map = unit.getMap();
        for (Map.Entry<String, Double> entry : map.entrySet()) {

            String key = entry.getKey();
            double value = entry.getValue();

            if (rootDf.equals(key)) {
                break;
            }

            int tempSize = factors.size();

            if (!key.equals(dimensionFrom)) {
                getFactors(++iteration, key, dimensionTo, rootDf, rootT, quantityFrom, factors);
            }
            if (tempSize < factors.size()) {
                factors.add(value);
            }
        }
    }
}
