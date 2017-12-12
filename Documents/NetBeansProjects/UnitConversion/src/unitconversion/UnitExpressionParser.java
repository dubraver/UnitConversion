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

import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 *
 * @author Nikolay Dyundik
 */
public class UnitExpressionParser {

    private static final String PATTERN = "(?<v1>[\\d\\.]+) (?<u1>[\\w]+) = (?<v2>[\\d\\.]+|[?]{1}) (?<u2>[\\w]+)";
    private static final int GROUP_COUNT = 4;
    private static final String QUESTION_MARK = "?";

    private UnitExpressionParser() {
    }

    public static Expression parseExpression(String inputString) throws UnitExpressionParserException {
        Scanner s = new Scanner(inputString);
        s.findInLine(PATTERN);
        MatchResult r;
        try {
            r = s.match();
        } catch (Exception e) {
            s.close();
            throw new UnitExpressionParserException();
        }

        if (r.groupCount() != GROUP_COUNT) {
            throw new UnitExpressionParserException();
        }

        double v1 = Double.parseDouble(r.group(1));
        String u1 = r.group(2);
        double v2 = Double.parseDouble(r.group(3).equals(QUESTION_MARK) ? "0" : r.group(3));
        ExpressionType type = Double.compare(v2, 0d) != 0 ? ExpressionType.GENERATE_UNITS : ExpressionType.CALCULATE;
        String u2 = r.group(4);

        return new Expression(type, v1, u1, v2, u2);
    }
}
