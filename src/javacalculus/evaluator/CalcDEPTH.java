/*
 * 
 */
package javacalculus.evaluator;

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
public class CalcDEPTH implements CalcFunctionEvaluator {

    @Override
    public CalcObject evaluate(CalcFunction input) {
        if (input.size() == 1) {
            CalcObject obj = input.get(0);
            return findDepth(obj, new CalcInteger(1));
        } else {
            throw new CalcWrongParametersException("DEPTH -> wrong number of parameters");
        }
    }

    public CalcInteger findDepth(CalcObject obj, CalcInteger currDepth) {
        if (obj instanceof CalcFunction) {
            obj = CALC.SYM_EVAL(obj);
        }
        if (obj.isNumber() || (obj instanceof CalcSymbol)) {
            return currDepth;
        } else if (obj instanceof CalcFunction) {
            CalcFunction objFunc = ((CalcFunction) obj);
            ArrayList<CalcObject> allParts = objFunc.getAll();
            CalcInteger testDepth;
            int maxDepth = 0;
            for (CalcObject temp : allParts) {
                CalcDEPTH depthFinder = new CalcDEPTH();
                testDepth = (CalcInteger) CALC.SYM_EVAL(depthFinder.findDepth(temp, currDepth));
                if (testDepth.intValue() > maxDepth) {
                    maxDepth = testDepth.intValue();
                }
            }
            return (CalcInteger) CALC.SYM_EVAL(CALC.ADD.createFunction(currDepth, new CalcInteger(maxDepth)));
        }
        return currDepth;
    }
}
