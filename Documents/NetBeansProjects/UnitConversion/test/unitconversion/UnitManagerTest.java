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

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nikolay Dyundik
 */
public class UnitManagerTest {

    public UnitManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of processExpression method, of class UnitManager.
     * @throws java.lang.Exception
     */
    @Test
    public void testProcessExpression() throws Exception {
        System.out.println("Test of processExpression method, of class UnitManager.");

        LinkedHashMap<String, Expression> map = new LinkedHashMap<>();
        map.put("1024 byte = 1 kilobyte", new Expression(ExpressionType.GENERATE_UNITS,1024,"byte",1,"kilobyte"));
        map.put("2 bar = 12 ring", new Expression(ExpressionType.GENERATE_UNITS,2,"bar",12,"ring"));
        map.put("16.8 ring = 2 pyramid", new Expression(ExpressionType.GENERATE_UNITS,16.8,"ring",2,"pyramid"));
        map.put("4 hare = 1 cat", new Expression(ExpressionType.GENERATE_UNITS,4,"hare",1,"cat"));
        map.put("5 cat = 0.5 giraffe", new Expression(ExpressionType.GENERATE_UNITS,5,"cat",0.5,"giraffe"));
        map.put("1 byte = 8 bit", new Expression(ExpressionType.GENERATE_UNITS,1,"byte",8,"bit"));
        map.put("15 ring = 2.5 bar", new Expression(ExpressionType.GENERATE_UNITS,15,"ring",2.5,"bar"));

        map.put("1 pyramid = ? bar", new Expression(ExpressionType.COMPUTED, 1, "pyramid", 1.4, "bar"));
        map.put("1 giraffe = ? hare", new Expression(ExpressionType.COMPUTED, 1, "giraffe", 40, "hare"));
        map.put("0.5 byte = ? cat", new Expression(ExpressionType.CONVERSION_NOT_POSSIBLE, 0.5, "byte", 0, "cat"));
        map.put("2 kilobyte = ? bit", new Expression(ExpressionType.COMPUTED, 2, "kilobyte", 16384, "bit"));
        for (Map.Entry<String, Expression> entry : map.entrySet()) {
            String inputLine = entry.getKey();
            Expression equalExp = entry.getValue();
            
            Expression parsedExp = UnitExpressionParser.parseExpression(inputLine);
            Expression calculatedExp = UnitManager.processExpression(parsedExp);
            if (calculatedExp != null) {
                System.out.println(calculatedExp.toString());
                assertEquals(equalExp, calculatedExp);
            } 
        }
    }
}
