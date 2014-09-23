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

/**
 * Factorial evaluator (x! = x*(x-1)*(x-2)*...*1). Supports negative numbers.
 * @author Duyun Chen <A HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>,
 * Seth Shannin <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *  
 *
 */
public class CalcFACTORIAL extends Calc1ParamFunctionEvaluator implements CalcOperatorEvaluator {
	
	@Override
	protected CalcObject evaluateObject(CalcObject input) {
		return null;
	}
	
	@Override
	protected CalcObject evaluateDouble(CalcDouble input) {
		//use the generalized function GAMMA to evaluate doubles and fractions
		return CALC.GAMMA.createFunction(input.add(CALC.D_ONE));
	}

	@Override
	protected CalcObject evaluateFraction(CalcFraction input) {
		return CALC.GAMMA.createFunction(input.add(new CalcFraction(CALC.ONE, CALC.ONE)));
	}

	@Override
	protected CalcObject evaluateFunction(CalcFunction input) {
		return CALC.FACTORIAL.createFunction(input);
	}

	@Override
	protected CalcObject evaluateInteger(CalcInteger input) {
		return factorial(input);
	}

	@Override
    protected CalcObject evaluateSymbol(CalcSymbol input) {
        //cannot evaluate symbols, so just return the original function
        if (input.equals(CALC.ERROR)) {
            return CALC.ERROR;
        }
        return CALC.FACTORIAL.createFunction(input);
    }


	public CalcInteger factorial(CalcInteger input) {

		CalcInteger result = CALC.ONE;

		if (input.isNegative()) {
			result = CALC.NEG_ONE;
			
			for (CalcInteger ii = CALC.NEG_TWO; ii.compareTo(input) >= 0; ii = ii.add(CALC.NEG_ONE)) {
				result = result.multiply(ii);
			}
		} 
		else {
			for (CalcInteger ii = CALC.TWO; ii.compareTo(input) <= 0; ii = ii.add(CALC.ONE)) {
				result = result.multiply(ii);
			}
		}

		return result;
	}

	@Override
	public int getPrecedence() {
		return 700;
	}

	@Override
	public String toOperatorString(CalcFunction function) {
		StringBuffer buffer = new StringBuffer();
		char operatorChar = '!';
		CalcObject temp = function.get(0);
		
    	if (temp.getPrecedence() < getPrecedence()) {
    		buffer.append('(');
    	}

    	buffer.append(temp.toString());

    	if (temp.getPrecedence() < getPrecedence()) {
    		buffer.append(')');
    	}
    	
    	buffer.append(operatorChar);
    	
    	return buffer.toString();
	}
}
