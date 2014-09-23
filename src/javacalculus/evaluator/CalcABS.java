/**
 *
 */
package javacalculus.evaluator;

import javacalculus.core.CALC;
import javacalculus.evaluator.extend.Calc1ParamFunctionEvaluator;
import javacalculus.evaluator.extend.CalcOperatorEvaluator;
import javacalculus.struct.CalcDouble;
import javacalculus.struct.CalcFraction;
import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcInteger;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

public class CalcABS extends Calc1ParamFunctionEvaluator implements CalcOperatorEvaluator {

    @Override
    public CalcObject evaluateObject(CalcObject obj) {
        return null;
    }

    @Override
    protected CalcObject evaluateDouble(CalcDouble input) {
        if (input.isNegative()) {
            return input.negate();
        }
        return input;
    }

    @Override
    protected CalcObject evaluateFraction(CalcFraction input) {
        if (input.isNegative()) {
            input.negate();
        }
        return input;
    }

    @Override
    protected CalcObject evaluateFunction(CalcFunction input) {
        if (input.getHeader().equals(CALC.ABS)) {
            return CALC.ABS.createFunction(input.get(0));
        }
        return CALC.ABS.createFunction(input);
    }

    @Override
    protected CalcObject evaluateInteger(CalcInteger input) {
        if (input.isNegative()) {
            return input.negate();
        } else {
            return input;
        }
    }

    @Override
    protected CalcObject evaluateSymbol(CalcSymbol input) {
        if (input.equals(CALC.ERROR)) {
            return CALC.ERROR;
        }
        return CALC.ABS.createFunction(input);
    }

    @Override
    public int getPrecedence() {
        return 700;
    }

    @Override
    public String toOperatorString(CalcFunction function) {
        StringBuffer buffer = new StringBuffer();
        //char operatorChar = '|';
        CalcObject temp = function.get(0);

//    	if (temp.getPrecedence() < getPrecedence()) {
//    		buffer.append('(');
//    	}
        //buffer.append(operatorChar).append(temp.toString()).append(operatorChar);
        //I CHANGED THIS
        buffer.append("ABS(").append(temp.toString()).append(")");

//    	if (temp.getPrecedence() < getPrecedence()) {
//    		buffer.append(')');
//    	}
//    	
        return buffer.toString();
    }
}
