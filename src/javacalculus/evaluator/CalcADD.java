package javacalculus.evaluator;

import javacalculus.core.CALC;
import javacalculus.evaluator.extend.CalcNParamFunctionEvaluator;
import javacalculus.evaluator.extend.CalcOperatorEvaluator;
import javacalculus.struct.*;

/**
 * Evaluator that handles addition of expressions. Handles basic simplification.
 * @author Duyun Chen <A HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>,
 * Seth Shannin <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *  
 *
 */
public class CalcADD extends CalcNParamFunctionEvaluator implements CalcOperatorEvaluator {

	@Override
	protected CalcObject evaluateObject(CalcObject input1, CalcObject input2) {
		//optimization cases
		if (input1.equals(CALC.ZERO)) {
			return input2;
		}
		else if (input2.equals(CALC.ZERO)) {
			return input1;
		}
		else if (input1.equals(input2)) {
			return CALC.MULTIPLY.createFunction(CALC.TWO, input1);
		}
		//else if (input1 instanceof CalcSymbol || input2 instanceof CalcSymbol) {
		//	return CALC.ADD.createFunction(input1, input2); //if either input is a symbol, can't evaluate..return original
		//}
		//end optimization cases
		//simplifiable cases
		else if (input1.getHeader().equals(CALC.MULTIPLY) &&
				((CalcFunction)input1).size() > 1) {
			CalcFunction function1 = (CalcFunction)input1;
			
			if (function1.get(0).isNumber()) {
				if (function1.size() == 2 && function1.get(1).equals(input2)) {
					return CALC.MULTIPLY.createFunction(CALC.ADD.createFunction(CALC.ONE, function1.get(0)), input2);
				}
				else if (input2.getHeader().equals(CALC.MULTIPLY) && ((CalcFunction)input2).size() > 1) {
					CalcFunction function2 = (CalcFunction)input2;
					
					if (function2.get(0).isNumber()) {
						if (function1.equalsFromIndex(1, function2, 1)) {
							CalcFunction result = new CalcFunction(CALC.MULTIPLY, function1, 1, function1.size());
							return CALC.MULTIPLY.createFunction(CALC.ADD.createFunction(function1.get(0), function2.get(0)), result);
						}
					}
					else {
						if (function1.equalsFromIndex(1, function2, 0)) {
							CalcFunction result = new CalcFunction(CALC.MULTIPLY, function1, 1, function1.size());
							return CALC.MULTIPLY.createFunction(CALC.ADD.createFunction(CALC.ONE, function1.get(0)), result);
						}						
					}
				}
			}
			else {
	            if (input2.getHeader().equals(CALC.MULTIPLY) && (((CalcFunction) input2).size() > 1)) {
	               CalcFunction function2 = (CalcFunction) input2;

	               if (function2.get(0).isNumber()) {
	                  if (function1.equalsFromIndex(0, function2, 1)) {
	                     CalcFunction result = new CalcFunction(CALC.MULTIPLY, function2, 1, function2.size());

	                     return CALC.MULTIPLY.createFunction(CALC.ADD.createFunction(CALC.ONE, function2.get(0)), result);
	                  }
	               }
	            }				
			}
		}
		
		if (input2.getHeader().equals(CALC.MULTIPLY) 
				&& (((CalcFunction) input2).size() > 1) 
				&& ((CalcFunction) input2).get(0).isNumber()) {
			CalcFunction function2 = (CalcFunction) input2;
			
			if ((function2.size() == 2) && function2.get(1).equals(input1)) {
	                return CALC.MULTIPLY.createFunction(CALC.ADD.createFunction(CALC.ONE, function2.get(0)), input1);
	        }
		}

		return null;		
	}
	
	@Override
	protected CalcObject evaluateInteger(CalcInteger input1, CalcInteger input2) {
		// TODO Auto-generated method stub
		return input1.add(input2);
	}
	
	@Override
	protected CalcObject evaluateDouble(CalcDouble input1, CalcDouble input2) {
		return input1.add(input2);
	}

	@Override
	protected CalcObject evaluateFraction(CalcFraction input1,
			CalcFraction input2) {
		return input1.add(input2);
	}
	
	protected CalcObject evaluateVector(CalcVector input1, CalcVector input2) {
		return input1.add(input2);
	}
	
	protected CalcObject evaluateMatrix(CalcMatrix input1, CalcMatrix input2) {
		return input1.add(input2);
	}

	@Override
	protected CalcObject evaluateFunction(CalcFunction input1,
			CalcFunction input2) {
		return null;
	}

	@Override
	protected CalcObject evaluateFunctionAndInteger(CalcFunction input1,
			CalcInteger input2) {
		return null;
	}

	@Override
    protected CalcObject evaluateSymbol(CalcSymbol input1, CalcSymbol input2) {
        if (input1.equals(CALC.ERROR) || input2.equals(CALC.ERROR)) {
            return CALC.ERROR;
        }
        return null;
    }

	@Override
	public int getPrecedence() {
		return 100;
	}

	@Override
	public String toOperatorString(CalcFunction function) {
		int precedence = getPrecedence();
		char operatorChar = '+';
		StringBuffer buffer = new StringBuffer();
		CalcObject temp;

		for (int ii = 0; ii < function.size(); ii++) {
			temp = function.get(ii);

            if (temp.getHeader().equals(CALC.MULTIPLY) &&
            		((CalcFunction)temp).get(0).compareTo(CALC.ZERO) < 0) {
               // special case -> negative number
               buffer.append(temp);
               continue;
            }
            else {
            	if (ii > 0) {
            		buffer.append(operatorChar);
            	}

            	if (temp.getPrecedence() < precedence) {
            		buffer.append('(');
            	}

            	buffer.append(temp.toString());

            	if (temp.getPrecedence() < precedence) {
            		buffer.append(')');
            	}
            }
		}

		return buffer.toString();
	}

}
