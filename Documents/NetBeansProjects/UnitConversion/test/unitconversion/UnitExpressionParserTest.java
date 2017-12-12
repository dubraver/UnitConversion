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
public class UnitExpressionParserTest {

    public UnitExpressionParserTest() {
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
     * Test of parseExpression method, of class UnitExpressionParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testParseExpression() throws Exception {
        System.out.println("Test of parseExpression method, of class UnitExpressionParser");

        LinkedHashMap<String, Expression> map = new LinkedHashMap<>();
        map.put("1024 byte = 1 kilobyte", new Expression(ExpressionType.GENERATE_UNITS,1024,"byte",1,"kilobyte"));
        map.put("2 bar = 12 ring", new Expression(ExpressionType.GENERATE_UNITS,2,"bar",12,"ring"));
        map.put("16.8 ring = 2 pyramid", new Expression(ExpressionType.GENERATE_UNITS,16.8,"ring",2,"pyramid"));
        map.put("4 hare = 1 cat", new Expression(ExpressionType.GENERATE_UNITS,4,"hare",1,"cat"));
        map.put("5 cat = 0.5 giraffe", new Expression(ExpressionType.GENERATE_UNITS,5,"cat",0.5,"giraffe"));
        map.put("1 byte = 8 bit", new Expression(ExpressionType.GENERATE_UNITS,1,"byte",8,"bit"));
        map.put("15 ring = 2.5 bar", new Expression(ExpressionType.GENERATE_UNITS,15,"ring",2.5,"bar"));
        for (Map.Entry<String, Expression> entry : map.entrySet()) {
            String inputString = entry.getKey();
            Expression result = entry.getValue();
            Expression expResult = UnitExpressionParser.parseExpression(inputString);
            assertEquals(expResult, result);
        }
    }
     /**
     * Test UnitExpressionParserException
     * @throws unitconversion.UnitExpressionParserException
     */
    @Test(expected = UnitExpressionParserException.class)
    public void testUnitExpressionParserException() throws UnitExpressionParserException {
        System.out.println("Test UnitExpressionParserException");
        ArrayList<String> list = new ArrayList<>();
        list.add("1024 byte = ??? kilobyte");
        list.add("? byte = ? kilobyte");
        list.add("? byte = 1 kilobyte");
        list.add("2 bar = test ring");
        list.add("xxxxyyyyy");
        list.add("");
        for (String inputString : list) {
            UnitExpressionParser.parseExpression(inputString);
        } 
    }
}
