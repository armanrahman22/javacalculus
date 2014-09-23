/**
 *
 */
package javacalculus.evaluator;

import javacalculus.core.CALC;
import javacalculus.evaluator.extend.Calc1ParamFunctionEvaluator;
import javacalculus.struct.CalcDouble;
import javacalculus.struct.CalcFraction;
import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcInteger;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

public class CalcSEC extends Calc1ParamFunctionEvaluator {

    @Override
    protected CalcObject evaluateObject(CalcObject input) {
        return CALC.SYM_EVAL(CALC.POWER.createFunction(CALC.COS.createFunction(input), CALC.NEG_ONE));
    }

    @Override
    protected CalcObject evaluateDouble(CalcDouble input) {
        return new CalcDouble(Math.pow(Math.cos(input.doubleValue()),-1));
    }

    @Override
    protected CalcObject evaluateFraction(CalcFraction input) {
        return null;
    }

    @Override
    protected CalcObject evaluateFunction(CalcFunction input) {
        return CALC.POWER.createFunction(CALC.COS.createFunction(input), CALC.NEG_ONE);
    }

    @Override
    protected CalcObject evaluateInteger(CalcInteger input) {
        return new CalcDouble(Math.pow(Math.cos(input.bigIntegerValue().intValue()),-1));
    }

    @Override
    protected CalcObject evaluateSymbol(CalcSymbol input) {
        if (input.equals(CALC.ERROR)) {
            return CALC.ERROR;
        }
        //cannot evaluate symbols, so just return the original function
        return CALC.POWER.createFunction(CALC.COS.createFunction(input), CALC.NEG_ONE);
    }

}
