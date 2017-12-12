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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UnitConversion {

    public static void main(String[] args) throws IOException, UnitExpressionParserException, Exception {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter statement or quit for exit:");
        while (true) {
            String inputLine = br.readLine().trim();
            if (inputLine.equals("quit")) {
                break;
            }
            try {
                Expression parsedExp = UnitExpressionParser.parseExpression(inputLine);
                Expression calculatedExp = UnitManager.processExpression(parsedExp);
                if (calculatedExp != null) {
                    System.out.println(calculatedExp.toString());
                }
            } catch (UnitExpressionParserException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}