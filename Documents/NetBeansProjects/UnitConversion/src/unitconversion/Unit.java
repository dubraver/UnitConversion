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

import java.util.HashMap;

/**
 *
 * @author Nikolay Dyundik
 */
public class Unit {

    private final String dimension;
    private final HashMap<String, Double> map = new HashMap<>();

    public String getDimension() {
        return this.dimension;
    }

    public HashMap<String, Double> getMap() {
        return map;
    }

    public double getFactor(String dimensionTo) {
        return map.get(dimensionTo) == null ? 0d : map.get(dimensionTo);
    }

    public Unit(String dimension, String dimensionTo, double quantityFrom, double quantityTo) {
        this.dimension = dimension;
        addConversionRule(dimensionTo, quantityFrom, quantityTo);
    }

    public void addConversionRule(String dimensionTo, double quantityFrom, double quantityTo) {
        if (Double.compare(quantityFrom, 0d) != 0) {
            double factor = this.dimension.equals(dimensionTo) ? 1 : quantityTo / quantityFrom;
            map.put(dimensionTo, factor);

        } else {
            throw new IllegalArgumentException("quantityFrom == 0");
        }
    }

    @Override
    public String toString() {
        return this.dimension;
    }
}
