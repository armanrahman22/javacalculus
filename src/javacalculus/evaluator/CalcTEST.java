/*
 * 
 */
package javacalculus.evaluator;

import java.math.MathContext;
import java.util.ArrayList;
import javacalculus.core.CALC;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.exception.CalcWrongParametersException;
import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcInteger;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

/**
 * shows the depth of the nested functions
 *
 * @author Seva
 */
public class CalcTEST implements CalcFunctionEvaluator {

    @Override
    public CalcObject evaluate(CalcFunction input) {
        if (input.size() == 1) {
            CalcObject obj = input.get(0);
            return doTest(obj);
        } else {
            throw new CalcWrongParametersException("TEST -> wrong number of parameters");
        }
    }

    private CalcObject doTest(CalcObject obj) {
        System.out.println(CALC.mathcontext);
        return obj;
    }
}
