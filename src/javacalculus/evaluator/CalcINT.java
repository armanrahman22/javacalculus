/**
 *
 */
package javacalculus.evaluator;

import javacalculus.core.CALC;
import javacalculus.core.CalcParser;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.exception.CalcWrongParametersException;
import javacalculus.struct.*;

/**
 * This function evaluator applies the Integral operator to a function with
 * respect to a given variable.
 *
 * @author Duyun Chen <A
 * HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>, Seth Shannin
 * <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *
 * @author Seva Luchianov
 */

public class CalcINT implements CalcFunctionEvaluator {

    public CalcINT() {
    }

    @Override
    public CalcObject evaluate(CalcFunction function) {
        if (function.size() == 2) {	//case INT(function, variable)
            if (function.get(1) instanceof CalcSymbol) {	//evaluate, adding an arbitrary constant for good practice
                //return CALC.ADD.createFunction(integrate(function.get(0), (CalcSymbol) function.get(1)), new CalcSymbol("C"));
                return CALC.ADD.createFunction(integrate(function.get(0), (CalcSymbol) function.get(1)));
            } else {
                throw new CalcWrongParametersException("INT -> 2nd parameter syntax");
            }
        } else {
            throw new CalcWrongParametersException("INT -> wrong number of parameters");
        }
    }

    public CalcObject integrate(CalcObject obj, CalcSymbol var) {
        if (obj instanceof CalcFunction) { //input f(x..xn)
            obj = CALC.SYM_EVAL(obj); //evaluate the function before attempting integration
        }
        if (obj.isNumber() || (obj instanceof CalcSymbol && !((CalcSymbol) obj).equals(var))) {	//	INT(c,x) = c*x
            return CALC.MULTIPLY.createFunction(obj, var);
        }
        if (obj.equals(var)) { //	INT(x, x) = x^2/2
            return CALC.MULTIPLY.createFunction(CALC.POWER.createFunction(var, CALC.TWO), CALC.HALF);
        }
        if (obj.getHeader().equals(CALC.ADD) && ((CalcFunction) obj).size() > 1) { //	INT(y1+y2+...,x) = INT(y1,x) + INT(y2,x) + ...
            CalcFunction function = (CalcFunction) obj;
            CalcFunction functionB = new CalcFunction(CALC.ADD, function, 1, function.size());
            return CALC.ADD.createFunction(integrate(function.get(0), var), integrate(functionB, var));
        }
        if (obj.getHeader().equals(CALC.MULTIPLY)) {	//INT(c*f(x),x) = c*INT(f(x),x)
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.isNumber()) {
                return CALC.MULTIPLY.createFunction(function.get(0),
                        integrate(new CalcFunction(CALC.MULTIPLY, function, 1, function.size()), var));
            } else { //	INT(f(x)*g(x),x) = ?? (u-sub)
                //f(g(x)) = f'(g(x))*g'(x)
                //Lets try to handle g'(x)*f(g(x))
                CalcObject secondObj = CALC.SYM_EVAL(function.get(1));
                CalcObject inOfFunc = null;
                CalcObject diffSecond;
                CalcObject checkU = null;
                CalcObject toBeInt = null;
                if (secondObj.getHeader().equals(CALC.POWER)) {//f'(x)*(f(x))^k
                    inOfFunc = ((CalcFunction) secondObj).get(0);
                    diffSecond = CALC.SYM_EVAL(CALC.DIFF.createFunction(inOfFunc, var));
                    CalcObject power = ((CalcFunction) secondObj).get(1);
                    checkU = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(firstObj, CALC.POWER.createFunction(diffSecond, CALC.NEG_ONE)));
                    toBeInt = CALC.SYM_EVAL(CALC.POWER.createFunction(var, power));
                } else if (secondObj.getHeader().equals(CALC.SIN)) {//f'(x)*sin(f(x))
                    inOfFunc = ((CalcFunction) secondObj).get(0);
                    diffSecond = CALC.SYM_EVAL(CALC.DIFF.createFunction(inOfFunc, var));
                    checkU = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(firstObj, CALC.POWER.createFunction(diffSecond, CALC.NEG_ONE)));
                    toBeInt = CALC.SYM_EVAL(CALC.SIN.createFunction(var));
                } else if (secondObj.getHeader().equals(CALC.COS)) {//f'(x)*cos(f(x))
                    inOfFunc = ((CalcFunction) secondObj).get(0);
                    diffSecond = CALC.SYM_EVAL(CALC.DIFF.createFunction(inOfFunc, var));
                    checkU = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(firstObj, CALC.POWER.createFunction(diffSecond, CALC.NEG_ONE)));
                    toBeInt = CALC.SYM_EVAL(CALC.COS.createFunction(var));
                } else if (firstObj.getHeader().equals(CALC.POWER)) {
                    inOfFunc = ((CalcFunction) firstObj).get(0);
                    diffSecond = CALC.SYM_EVAL(CALC.DIFF.createFunction(inOfFunc, var));
                    CalcObject power = ((CalcFunction) firstObj).get(1);
                    checkU = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(secondObj, CALC.POWER.createFunction(diffSecond, CALC.NEG_ONE)));
                    toBeInt = CALC.SYM_EVAL(CALC.POWER.createFunction(var, power));
                } else if (firstObj.getHeader().equals(CALC.SIN)) {//f'(x)*sin(f(x))
                    inOfFunc = ((CalcFunction) firstObj).get(0);
                    diffSecond = CALC.SYM_EVAL(CALC.DIFF.createFunction(inOfFunc, var));
                    checkU = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(secondObj, CALC.POWER.createFunction(diffSecond, CALC.NEG_ONE)));
                    toBeInt = CALC.SYM_EVAL(CALC.SIN.createFunction(var));
                } else if (secondObj.getHeader().equals(CALC.COS)) {//f'(x)*cos(f(x))
                    inOfFunc = ((CalcFunction) secondObj).get(0);
                    diffSecond = CALC.SYM_EVAL(CALC.DIFF.createFunction(inOfFunc, var));
                    checkU = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(firstObj, CALC.POWER.createFunction(diffSecond, CALC.NEG_ONE)));
                    toBeInt = CALC.SYM_EVAL(CALC.COS.createFunction(var));
                }
                if (checkU != null && checkU.isNumber()) {
                    CalcObject result = ((CalcFunction) integrate(toBeInt, var));
                    String resultString = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(checkU, result)).toString().replaceAll(var.toString(), "(" + inOfFunc.toString() + ")");
                    CalcParser parser = new CalcParser();
                    try {
                        return CALC.SYM_EVAL(parser.parse(resultString));
                    } catch (Exception e) {
                        return null;
                    }
                }
                return null;
                //return obj; //TODO implement reverse chain rule (u-sub)?? Very tricky.
            }
        }
        if (obj.getHeader().equals(CALC.POWER)) { //this part is probably trickiest (form f(x)^g(x)). A lot of integrals here does not evaluate into elementary functions
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            CalcObject secondObj = function.get(1);
            if (firstObj instanceof CalcSymbol) {
                if (secondObj.isNumber() || secondObj instanceof CalcSymbol && !(secondObj.equals(var))) { //	INT(x^n,x) = x^(n+1)/(n+1)
                    if (!secondObj.equals(CALC.NEG_ONE)) {//handle 1/x
                        CalcObject temp = CALC.MULTIPLY.createFunction(
                                CALC.POWER.createFunction(firstObj, CALC.ADD.createFunction(secondObj, CALC.ONE)),
                                CALC.POWER.createFunction(CALC.ADD.createFunction(secondObj, CALC.ONE), CALC.NEG_ONE));
                        //System.out.println("WE ARE IN THE 1/x BRANCH");
                        //System.out.println(temp);
                        return temp;
                    } else {
                        return CALC.LN.createFunction(CALC.ABS.createFunction(firstObj));
                    }
                }
            } else if (firstObj.isNumber()) {	// INT(c^x,x) = c^x/ln(c)
                return CALC.MULTIPLY.createFunction(obj, CALC.POWER.createFunction(CALC.LN.createFunction(firstObj), CALC.NEG_ONE));
            }
        }
        if (obj.getHeader().equals(CALC.LN)) {	//	INT(LN(x),x) = x*LN(x) - x
            return CALC.ADD.createFunction(
                    CALC.MULTIPLY.createFunction(var, obj),
                    CALC.MULTIPLY.createFunction(var, CALC.NEG_ONE));
        }
        if (obj.getHeader().equals(CALC.SIN)) {	//	INT(SIN(x),x) = -COS(x)
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.MULTIPLY.createFunction(CALC.NEG_ONE, CALC.COS.createFunction(firstObj));
            }
        }
        if (obj.getHeader().equals(CALC.COS)) {	//	INT(COS(x),x) = SIN(x)
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.SIN.createFunction(firstObj);
            }
        }
        if (obj.getHeader().equals(CALC.TAN)) {	//	INT(TAN(x),x) = -LN(|COS(x)|)
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.MULTIPLY.createFunction(CALC.NEG_ONE,
                        CALC.LN.createFunction(CALC.ABS.createFunction(CALC.COS.createFunction(var))));
            }
        }
        if (obj.getHeader().equals(CALC.ABS)) {	//	INT(|x|,x) = x*|x|/2
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.MULTIPLY.createFunction(var, CALC.HALF,
                        CALC.ABS.createFunction(var));
            }
        }
        return null;
        //return CALC.INT.createFunction(obj, var); //don't know how to integrate (yet). Return original expression.
    }
}
