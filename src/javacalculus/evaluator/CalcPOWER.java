package javacalculus.evaluator;

import javacalculus.struct.*;
import javacalculus.core.*;
import javacalculus.evaluator.extend.Calc2ParamFunctionEvaluator;
import javacalculus.evaluator.extend.CalcOperatorEvaluator;
import javacalculus.exception.*;

/**
 * Evaluator that handles exponentiation of expressions. Handles basic simplification.
 * @author Duyun Chen <A HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>,
 * Seth Shannin <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *  
 *
 */
public class CalcPOWER extends Calc2ParamFunctionEvaluator implements CalcOperatorEvaluator {
	
	@Override
	protected CalcObject evaluateObject(CalcObject input1, CalcObject input2) {
		if (input1.equals(CALC.ZERO)) {
			if (input2.equals(CALC.ZERO)) {
				throw new CalcIndeterminateException("0^0");
			}
			if (input2 instanceof CalcInteger &&
				((CalcInteger)input2).isNegative()) {
				return new CalcDouble(Double.POSITIVE_INFINITY);
			}
			return CALC.ZERO;
		}
		if (input2.equals(CALC.ONE)) {
			return input1;
		}
		if (input2.equals(CALC.ZERO)) {
			return CALC.ONE;
		}
		if (input2.equals(CALC.ONE)) {
			return input1;
		}
		if (input2.isNumber() && input1.getHeader().equals(CALC.POWER)) {
			CalcFunction function1 = (CalcFunction)input1;
			
			if (function1.size() == 2 && function1.get(1).isNumber()) {
				return CALC.POWER.createFunction(function1.get(0), CALC.MULTIPLY.createFunction(function1.get(1), input2));
			}
			
		}
		
		if (input2.isNumber() && input1.getHeader().equals(CALC.MULTIPLY)) {
			CalcFunction function1 = (CalcFunction)input1;
			
			if (function1.size() > 0 && function1.get(0).isNumber()) {
				return CALC.MULTIPLY.createFunction(
						CALC.POWER.createFunction(function1.get(0), input2), 
						CALC.POWER.createFunction(new CalcFunction(CALC.MULTIPLY, function1, 1, function1.size()), input2));
			}
		}
		
		return null;
	}

	@Override
	protected CalcObject evaluateDouble(CalcDouble input1, CalcDouble input2) {
		return input1.power(input2);
	}

	@Override
	protected CalcObject evaluateFraction(CalcFraction input1, CalcFraction input2) {
		if (input1.getNumerator().equals(CALC.ZERO)) {
			return CALC.ZERO;
		}
		if (input2.getNumerator().equals(CALC.ZERO)) {
			return CALC.ONE;
		}
		if (input1.getDenominator().equals(CALC.ONE) && input2.getNumerator().equals(CALC.ONE)) {
			boolean negative = false;
			CalcInteger nume1 = new CalcInteger(input1.getNumerator());
			CalcInteger denom2 = new CalcInteger(input2.getDenominator());
			CalcInteger result;
			
			if (denom2.isNegative()) return null;
			
			int root = denom2.intValue();
			
			if (nume1.isNegative()) {
				if (denom2.isEven()) {
					nume1 = nume1.negate();
					result = nume1.root(root);
					
					if (result.power(root).equals(nume1) && root % 4 != 0) {
						throw new CalcArithmeticException("Non-real result");
						//TODO implement imaginary numbers??
					}
					
					return null;
				}
				
				negative = true;
				nume1 = nume1.negate();
			}
			
			result = nume1.root(root);
			
			if (result.power(root).equals(nume1)) {
				if (negative) {
					return result.negate();
				}
				return result;
			}
			
			return null;
		}
		if (!(input2.getDenominator().equals(CALC.TWO.bigIntegerValue()))) {
			return null;
		}
		
		return input1.power(input2.getNumerator().intValue());
	}

	@Override
	protected CalcObject evaluateFunction(CalcFunction input1, CalcFunction input2) {
		return null;
	}

	@Override
	protected CalcObject evaluateFunctionAndInteger(CalcFunction input1, CalcInteger input2) {
		return CALC.POWER.createFunction(input1, input2);
	}

	@Override
	protected CalcObject evaluateInteger(CalcInteger input1, CalcInteger input2) {
		if (input1.equals(CALC.ZERO)) return null;
		if (input2.isNegative()) {
			return new CalcFraction(CALC.ONE, input1.power(input2.negate().intValue()));
		}
		return input1.power(input2.intValue());
	}

	@Override
    protected CalcObject evaluateSymbol(CalcSymbol input1, CalcSymbol input2) {
        if (input1.equals(CALC.ERROR)|| input2.equals(CALC.ERROR)) {
            return CALC.ERROR;
        }
        return null;
    }
    
	@Override
	public int getPrecedence() {
		return 9001; //it's OVER 9000!!!!!!
	}

	@Override
	public String toOperatorString(CalcFunction function) {
	    int precedence = getPrecedence();
		StringBuffer buffer = new StringBuffer();
	    CalcObject temp;

	    if (function.size() > 0) {
	    	if ((function.size() == 2) 
	             && (function.get(1).equals(CALC.NEG_ONE) || function.get(1).equals(CALC.HALF) 
	                 || function.get(1).equals(CALC.NEG_HALF))) {
	    		temp = function.get(0);

	    		if (function.get(1).equals(CALC.HALF)) {
	    			buffer.append("SQRT(");
	    			buffer.append(temp.toString());
	    			buffer.append(")");
	    		}
	    		else {
	    			buffer.append("1/");

	    			if (function.get(1).equals(CALC.NEG_HALF)) {
	    				buffer.append("SQRT(");
	    				buffer.append(temp.toString());
	    				buffer.append(")");
	    			}
	    			else {
	    				if (temp.getPrecedence() < precedence) {
	    					buffer.append('(');
	    				}

	    				buffer.append(temp.toString());

	    				if (temp.getPrecedence() < precedence) {
	    					buffer.append(')');
	    				}
	    			}
	    		}
	    	}
	    	else {
	    		temp = function.get(0);
	    		
	            if (temp.getPrecedence() < precedence) {	
	            	buffer.append('(');
	            }

	            buffer.append(temp.toString());

	            if (temp.getPrecedence() < precedence) {
	            	buffer.append(')');
	            }

	            if (function.size() > 1) {
	            	buffer.append('^');
	            }

	            for (int ii = 1; ii < function.size(); ii++) {
	            	temp = function.get(ii);

	            	if (temp.getPrecedence() < precedence) {
	            		buffer.append('(');
	            	}
	            	
	            	buffer.append(temp.toString());
	            	
	            	if (temp.getPrecedence() < precedence) {
	            		buffer.append(')');
	            	}

	            	if (ii != (function.size() - 1)) {
	            		buffer.append('^');
	            	}
	            }
	    	}	
	    }

	    return buffer.toString();
	}
}
	
