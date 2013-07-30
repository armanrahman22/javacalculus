package javacalculus.core;

import java.math.MathContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javacalculus.evaluator.*;
import javacalculus.evaluator.extend.CalcConstantEvaluator;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.struct.*;

/**
 * This class contains a lot of global static constant or mutable values,
 * functions, and instances.
 *
 * @author Duyun Chen <A
 * HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>, Seth Shannin
 * <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *
 *
 */
public final class CALC {

    /**
     * The math context for the engine. Controls rounding and truncations to 32
     * bit to IEEE 754R Decimal32 standards (7 digits). The precision can be
     * changed by calling <b>CALC.setMathContext(int precision)</b>
     */
    public static MathContext mathcontext = MathContext.DECIMAL32;
    public static boolean operator_notation = false;
    public static int max_recursion_depth = 3;
    /**
     * Standard unaryChar operator symbols
     */
    public static final CalcSymbol ADD = new CalcSymbol("ADD", new CalcADD(),
            CalcSymbol.OPERATOR | CalcSymbol.COMMUTATIVE | CalcSymbol.ASSOCIATIVE | CalcSymbol.UNIPARAM_IDENTITY);
    public static final CalcSymbol SUBTRACT = new CalcSymbol("SUBTRACT",
            CalcSymbol.OPERATOR | CalcSymbol.UNIPARAM_IDENTITY);
    public static final CalcSymbol MULTIPLY = new CalcSymbol("MULTIPLY", new CalcMULTIPLY(),
            CalcSymbol.OPERATOR | CalcSymbol.COMMUTATIVE | CalcSymbol.ASSOCIATIVE | CalcSymbol.UNIPARAM_IDENTITY);
    public static final CalcSymbol DIVIDE = new CalcSymbol("DIVIDE",
            CalcSymbol.OPERATOR | CalcSymbol.UNIPARAM_IDENTITY);
    public static final CalcSymbol POWER = new CalcSymbol("POWER", new CalcPOWER(),
            CalcSymbol.OPERATOR | CalcSymbol.UNIPARAM_IDENTITY);
    public static final CalcSymbol FACTORIAL = new CalcSymbol("FACTORIAL", new CalcFACTORIAL(),
            CalcSymbol.OPERATOR);
    public static final CalcSymbol ABS = new CalcSymbol("ABS", new CalcABS(),
            CalcSymbol.OPERATOR);
    public static final CalcSymbol EXPAND = new CalcSymbol("EXPAND", new CalcEXPAND(),
            CalcSymbol.OPERATOR);
    public static final CalcSymbol DEPTH = new CalcSymbol("DEPTH", new CalcDEPTH(),
            CalcSymbol.OPERATOR);
    public static final CalcSymbol SIMPLIFY = new CalcSymbol("SIMPLIFY", new CalcSIMPLIFY(),
            CalcSymbol.OPERATOR);
    /**
     * Special function symbols
     */
    public static final CalcSymbol SIN = new CalcSymbol("SIN", new CalcSIN(),
            CalcSymbol.NO_PROPERTY);
    public static final CalcSymbol COS = new CalcSymbol("COS", new CalcCOS(),
            CalcSymbol.NO_PROPERTY);
    public static final CalcSymbol TAN = new CalcSymbol("TAN", new CalcTAN(),
            CalcSymbol.NO_PROPERTY);
    //public static final CalcSymbol COT = new CalcSymbol("COT", new CalcCOT(),
    //CalcSymbol.NO_PROPERTY);
    public static final CalcSymbol LN = new CalcSymbol("LN", new CalcLN(),
            CalcSymbol.NO_PROPERTY);
    public static final CalcSymbol DIFF = new CalcSymbol("DIFF", new CalcDIFF(),
            CalcSymbol.NO_PROPERTY);
    public static final CalcSymbol INT = new CalcSymbol("INT", new CalcINT(),
            CalcSymbol.NO_PROPERTY);
    public static final CalcSymbol INTBYPARTS = new CalcSymbol("INTBYPARTS", new CalcINTBYPARTS(),
            CalcSymbol.NO_PROPERTY);
    //TODO implement INT (integration). This is gonna a hell of a lot harder.
    public static final CalcSymbol DEFINE = new CalcSymbol("DEFINE", new CalcDEFINE(),
            CalcSymbol.OPERATOR | CalcSymbol.FAST_EVAL);
    public static final CalcSymbol SUB = new CalcSymbol("SUB", new CalcSUB(),
            CalcSymbol.FAST_EVAL);
    public static final CalcSymbol TAYLOR = new CalcSymbol("TAYLOR", new CalcTAYLOR(),
            CalcSymbol.NO_PROPERTY);
    public static final CalcSymbol GAMMA = new CalcSymbol("GAMMA", new CalcGAMMA(),
            CalcSymbol.NO_PROPERTY);
    public static final CalcSymbol DET = new CalcSymbol("DET", new CalcDET(),
            CalcSymbol.NO_PROPERTY);
    public static final CalcSymbol PLOT = new CalcSymbol("PLOT", new CalcPLOT(),
            CalcSymbol.FAST_EVAL);
    public static final CalcSymbol PLOT3D = new CalcSymbol("PLOT3D", new CalcPLOT3D(),
            CalcSymbol.FAST_EVAL);
    /**
     * Useful numerical constants
     */
    private static final byte[] IntegerZero = {0};
    public static final CalcInteger ZERO = new CalcInteger(IntegerZero);
    public static final CalcDouble D_ZERO = new CalcDouble(ZERO);
    private static final byte[] IntegerOne = {1};
    public static final CalcInteger ONE = new CalcInteger(IntegerOne);
    public static final CalcDouble D_ONE = new CalcDouble(ONE);
    private static final byte[] IntegerNegOne = {-1};
    public static final CalcInteger NEG_ONE = new CalcInteger(IntegerNegOne);
    public static final CalcDouble D_NEG_ONE = new CalcDouble(NEG_ONE);
    private static final byte[] IntegerTwo = {2};
    public static final CalcInteger TWO = new CalcInteger(IntegerTwo);
    private static final byte[] IntegerNegTwo = {-2};
    public static final CalcInteger NEG_TWO = new CalcInteger(IntegerNegTwo);
    public static final CalcDouble D_TWO = new CalcDouble(TWO);
    private static final byte[] IntegerFour = {4};
    public static final CalcInteger FOUR = new CalcInteger(IntegerFour);
    public static final CalcDouble D_FOUR = new CalcDouble(FOUR);
    public static final CalcFraction HALF = new CalcFraction(ONE, TWO);
    public static final CalcFraction NEG_HALF = new CalcFraction(NEG_ONE, TWO);
    public static final CalcDouble D_NEG_QUARTER = new CalcDouble("-0.25");
    public static final CalcDouble D_QUARTER = new CalcDouble("0.25");
    public static final CalcDouble D_HALF = new CalcDouble("0.5");
    public static final CalcDouble D_THREE_HALF = new CalcDouble("1.5");
    public static final CalcDouble INFINITY = new CalcDouble(Double.POSITIVE_INFINITY);
    public static final CalcDouble NEG_INFINITY = new CalcDouble(Double.NEGATIVE_INFINITY);
    /**
     * Header definitions for certain structs
     */
    public static final CalcSymbol INTEGER = new CalcSymbol("Integer");
    public static final CalcSymbol DOUBLE = new CalcSymbol("Double");
    public static final CalcSymbol FRACTION = new CalcSymbol("Fraction");
    public static final CalcSymbol SYMBOL = new CalcSymbol("Symbol");
    public static final CalcSymbol MATRIX = new CalcSymbol("Matrix");
    public static final CalcSymbol VECTOR = new CalcSymbol("Vector");
    public static final CalcSymbol ERROR = new CalcSymbol("Error");
    /**
     * Symbols for built-in constants
     */
    public static final CalcSymbol PI = new CalcSymbol("PI",
            new CalcConstantEvaluator(new CalcDouble(Math.PI)), CalcSymbol.CONSTANT);
    public static final CalcSymbol E = new CalcSymbol("E",
            new CalcConstantEvaluator(new CalcDouble(Math.E)), CalcSymbol.CONSTANT);
    /**
     * HashMap that stores user defined local variables using the header symbol
     * as key
     */
    public static HashMap<CalcSymbol, CalcObject> defined = new HashMap<CalcSymbol, CalcObject>();

    /**
     *
     * @param variable
     * @return true if the HashMap <b>defined</b> contains key <b>variable</b>.
     * False otherwise.
     */
    public static boolean hasDefinedVariable(CalcSymbol variable) {
        //TODO optimize this process
        if (defined.isEmpty()) {
            return false;
        }

        Set<CalcSymbol> keySet = defined.keySet();
        Iterator<CalcSymbol> iter = keySet.iterator();

        while (iter.hasNext()) {
            if (variable.equals(iter.next())) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param variable
     * @return The value in HashMap <b>defined</b> corresponding to the key
     * <b>variable</b>
     */
    public static CalcObject getDefinedVariable(CalcSymbol variable) {
        if (!hasDefinedVariable(variable)) {
            return null;
        }

        Set<CalcSymbol> keySet = defined.keySet();
        Iterator<CalcSymbol> iter = keySet.iterator();

        while (iter.hasNext()) {
            CalcSymbol next = iter.next();
            if (variable.equals(next)) {
                return defined.get(next);
            }
        }

        return null;
    }

    public static CalcSymbol getDefinedVariableKey(CalcObject value) {
        if (!defined.containsValue(value)) {
            return null;
        }

        Set<CalcSymbol> keySet = defined.keySet();
        Iterator<CalcSymbol> iter = keySet.iterator();

        while (iter.hasNext()) {
            CalcSymbol next = iter.next();
            if (value.equals(defined.get(next))) {
                return next;
            }
        }

        return null;
    }

    /**
     * Puts entry (<b>key</b>, <b>value</b>) in HashMap <b>defined</b>
     *
     * @param key
     * @param value
     */
    public static void setDefinedVariable(CalcSymbol key, CalcObject value) {
        if (hasDefinedVariable(key)) {
            if (!getDefinedVariable(key).equals(value)) {
                defined.put(key, value);
            }
        } else {
            defined.put(key, value);
        }
    }

    /**
     *
     * @param input
     * @return true if every char in input is upper case. False otherwise.
     */
    public static final boolean isUpperCase(String input) {
        for (int ii = 0; ii < input.length(); ii++) {
            if (Character.isLowerCase(input.charAt(ii))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Symbolically and recursively evaluate a CalcObject using its own evaluate
     * method
     */
    public static CalcObject SYM_EVAL(CalcObject input) {
        CalcObject returnVal = null;
        try {
            returnVal = input.evaluate();
        } catch (Exception e1) {
            //e1.printStackTrace();
        }

        if (returnVal == null) {
            return null;
        }

        //If the evaluation object is PLOT or PLOT3D, no need to continue.
        //most likely the graph is already rendered at this point (after
        //first evaluation). Failure to include this line results in multiple
        //graphs rendered from multiple evaluations... waste of CPU cycles.
        if (returnVal.getHeader().equals(CALC.PLOT) || returnVal.getHeader().equals(CALC.PLOT3D)) {
            return returnVal;
        }

        CalcObject temp = null;

        try {
            temp = returnVal.evaluate();
        } catch (Exception e1) {
            //e1.printStackTrace();
        }

        //if 2nd evaluation still produced different result, do it again
        //until the result cannot be further evaluated into a different form
        while (temp != null && !returnVal.equals(temp)) {
            returnVal = temp;
            try {
                temp = returnVal.evaluate();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        return (returnVal == null) ? input : returnVal;
    }

    /**
     *
     * @param identifier
     * @return a function evaluator capable of evaluating the properties of
     * symbol
     */
    public static CalcSymbol getSymbol(String identifier) {
        if (identifier.equals("SIN")) {
            return (CalcSymbol) SIN.clone();
        } else if (identifier.equals("COS")) {
            return (CalcSymbol) COS.clone();
        } else if (identifier.equals("TAN")) {
            return (CalcSymbol) TAN.clone();
        } else if (identifier.equals("LN")) {
            return (CalcSymbol) LN.clone();
        } else if (identifier.equals("ABS")) {
            return (CalcSymbol) ABS.clone();
        } else if (identifier.equals("DIFF")) {
            return (CalcSymbol) DIFF.clone();
        } else if (identifier.equals("INT")) {
            return (CalcSymbol) INT.clone();
        } else if (identifier.equals("INTBYPARTS")) {
            return (CalcSymbol) INTBYPARTS.clone();
        } else if (identifier.equals("EXPAND")) {
            return (CalcSymbol) EXPAND.clone();
        } else if (identifier.equals("DEPTH")) {
            return (CalcSymbol) DEPTH.clone();
        } else if (identifier.equals("SIMPLIFY")) {
            return (CalcSymbol) SIMPLIFY.clone();
        } else if (identifier.equals("DEFINE")) {
            return (CalcSymbol) DEFINE.clone();
        } else if (identifier.equals("PLOT")) {
            return (CalcSymbol) PLOT.clone();
        } else if (identifier.equals("PLOT3D")) {
            return (CalcSymbol) PLOT3D.clone();
        } else if (identifier.equals("TAYLOR")) {
            return (CalcSymbol) TAYLOR.clone();
        } else if (identifier.equals("GAMMA")) {
            return (CalcSymbol) GAMMA.clone();
        } else if (identifier.equals("DET")) {
            return (CalcSymbol) DET.clone();
        } else if (identifier.equals("PI")) {
            return PI;
        } else if (identifier.equals("E")) {
            return E;
        } else {
            String name = "javacalculus.evaluator.Calc" + identifier;

            Class cls = null;
            CalcFunctionEvaluator evaluator;
            CalcSymbol symbol = new CalcSymbol(identifier);

            try {
                cls = Class.forName(name);
            } catch (ClassNotFoundException e) {
            }

            try {
                evaluator = (CalcFunctionEvaluator) cls.newInstance();
                symbol.setEvaluator(evaluator);
                return symbol;
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static void setMathContext(int precision) {
        mathcontext = new MathContext(precision);
    }

    public static void toggleOperatorNotation() {
        operator_notation = !operator_notation;
    }
}
