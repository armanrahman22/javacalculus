package javacalculus.evaluator;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import javacalculus.core.CALC;
import javacalculus.core.CalcParser;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.exception.CalcWrongParametersException;
import javacalculus.struct.*;
import java.util.concurrent.Executors;

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

    public CalcINTBYPARTS intByPartsCarryOver = null;
    public int recDepth;

    public CalcINT() {
        recDepth = 0;
    }
    
    public CalcINT(int recDepth) {
        this.recDepth = recDepth;
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

    public CalcObject integrate(CalcObject object, CalcSymbol var) {
        CalcObject obj = object;
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
            CalcFunction function = new CalcFunction(CALC.MULTIPLY);
            function.addAll((CalcFunction) obj);
            //function = (CalcFunction) CALC.SYM_EVAL(function);
            CalcObject firstObj = function.get(0);
            if (firstObj.isNumber()) {
                return CALC.MULTIPLY.createFunction(function.get(0),
                        integrate(new CalcFunction(CALC.MULTIPLY, function, 1, function.size()), var));
            } else { //	INT(f(x)*g(x),x) = ?? (u-sub)
                /*
                 * could be integration by parts
                 * INT(u*dv) = u*v-INT(v*du)
                 * 
                 * 2 options
                 * LAITE
                 * or
                 * pick the most complicated thing that can be integrated for dv (parse all possibilities?)
                 *   start here first, will assume only 2 functions being multiplied. 6/20/13
                 * 
                 */

                //f(g(x)) = f'(g(x))*g'(x)
                //Lets try to handle g'(x)*f(g(x))
                int maxDepth = 0;
                int index = 0;
                int tempDepth;
                ArrayList<CalcObject> funcObjects = giveList(CALC.MULTIPLY, function);
                for (int i = 0; i < funcObjects.size(); i++) {
                    tempDepth = ((CalcInteger) CALC.SYM_EVAL(CALC.DEPTH.createFunction(funcObjects.get(i)))).intValue();
                    if (tempDepth > maxDepth) {
                        maxDepth = tempDepth;
                        index = i;
                    }
                }
                firstObj = funcObjects.remove(index);
                CalcObject secondObj = CALC.ONE;
                CalcObject coef = CALC.ONE;
                for (CalcObject temp : funcObjects) {
                    secondObj = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(secondObj, temp));
                }
                //System.err.println(firstObj);
                //System.err.println(secondObj);
                CalcObject inOfFunc = null;
                CalcObject diffSecond;
                CalcObject checkU = null;
                CalcObject toBeInt = null;
                if (firstObj.getHeader().equals(CALC.POWER)) {
                    if (((CalcFunction) firstObj).get(1).isNumber()) {//f(x)^k*f'(x)
                        inOfFunc = ((CalcFunction) firstObj).get(0);
                        //System.out.println("inOfFunc: " + inOfFunc);
                        diffSecond = CALC.SYM_EVAL(CALC.DIFF.createFunction(inOfFunc, var));
                        if (diffSecond.getHeader().equals(CALC.MULTIPLY)) {
                            if (((CalcFunction) diffSecond).get(0).isNumber()) {
                                coef = ((CalcFunction) diffSecond).get(0);//numbers always in front?
                                ((CalcFunction) diffSecond).remove(0);
                            }
                        }
                        CalcObject power = ((CalcFunction) firstObj).get(1);
                        //System.out.println("diffSecond: " + diffSecond);
                        //diffSecond = CALC.SYM_EVAL(CALC.EXPAND.createFunction(diffSecond));
                        //checkU = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(CALC.EXPAND.createFunction(secondObj), CALC.POWER.createFunction(diffSecond, CALC.NEG_ONE)));
                        if (superEquals(secondObj, diffSecond)) {
                            checkU = CALC.ONE;
                        }
                        //System.out.println("checkU: " + checkU);
                        toBeInt = CALC.POWER.createFunction(var, power);
                        //System.out.println("toBeInt: " + toBeInt);
                    } else if (((CalcFunction) firstObj).get(0).isNumber()) {//k^f(x)*f'(x)...
                        inOfFunc = ((CalcFunction) firstObj).get(1);
                        //System.out.println("inOfFunc: " + inOfFunc);
                        diffSecond = CALC.SYM_EVAL(CALC.DIFF.createFunction(inOfFunc, var));
                        if (diffSecond.getHeader().equals(CALC.MULTIPLY)) {
                            if (((CalcFunction) diffSecond).get(0).isNumber()) {
                                coef = ((CalcFunction) diffSecond).get(0);//numbers always in front?
                                ((CalcFunction) diffSecond).remove(0);
                            }
                        }
                        CalcObject base = ((CalcFunction) firstObj).get(0);
                        //System.out.println("diffSecond: " + diffSecond);
                        //diffSecond = CALC.SYM_EVAL(CALC.EXPAND.createFunction(diffSecond));
                        //checkU = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(CALC.EXPAND.createFunction(secondObj), CALC.POWER.createFunction(diffSecond, CALC.NEG_ONE)));
                        if (superEquals(secondObj, diffSecond)) {
                            checkU = CALC.ONE;
                        }
                        //System.out.println("checkU: " + checkU);
                        toBeInt = CALC.POWER.createFunction(base, var);
                        //System.out.println("toBeInt: " + toBeInt);
                    }
                } else if (firstObj.getHeader().equals(CALC.SIN)) {//f'(x)*sin(f(x))
                    inOfFunc = ((CalcFunction) firstObj).get(0);
                    //System.out.println("inOfFunc: " + inOfFunc);
                    diffSecond = CALC.SYM_EVAL(CALC.DIFF.createFunction(inOfFunc, var));
                    if (diffSecond.getHeader().equals(CALC.MULTIPLY)) {
                        if (((CalcFunction) diffSecond).get(0).isNumber()) {
                            coef = ((CalcFunction) diffSecond).get(0);//numbers always in front?
                            ((CalcFunction) diffSecond).remove(0);
                        }
                    }
                    //System.out.println("diffSecond: " + diffSecond);
                    //diffSecond = CALC.SYM_EVAL(CALC.EXPAND.createFunction(diffSecond));
                    //checkU = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(CALC.EXPAND.createFunction(secondObj), CALC.POWER.createFunction(diffSecond, CALC.NEG_ONE)));
                    if (superEquals(secondObj, diffSecond)) {
                        checkU = CALC.ONE;
                    }
                    //System.out.println("checkU: " + checkU);
                    toBeInt = CALC.SIN.createFunction(var);
                    //System.out.println("toBeInt: " + toBeInt);
                } else if (firstObj.getHeader().equals(CALC.COS)) {//f'(x)*cos(f(x))
                    inOfFunc = ((CalcFunction) firstObj).get(0);
                    //System.out.println("inOfFunc: " + inOfFunc);
                    diffSecond = CALC.SYM_EVAL(CALC.DIFF.createFunction(inOfFunc, var));
                    if (diffSecond.getHeader().equals(CALC.MULTIPLY)) {
                        if (((CalcFunction) diffSecond).get(0).isNumber()) {
                            coef = ((CalcFunction) diffSecond).get(0);//numbers always in front?
                            ((CalcFunction) diffSecond).remove(0);
                        }
                    }
                    //System.out.println("diffSecond: " + diffSecond);
                    //diffSecond = CALC.SYM_EVAL(CALC.EXPAND.createFunction(diffSecond));
                    //checkU = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(CALC.EXPAND.createFunction(secondObj), CALC.POWER.createFunction(diffSecond, CALC.NEG_ONE)));
                    if (superEquals(secondObj, diffSecond)) {
                        checkU = CALC.ONE;
                    }
                    //System.out.println("checkU: " + checkU);
                    toBeInt = CALC.COS.createFunction(var);
                    //System.out.println("toBeInt: " + toBeInt);
                }
                if (checkU != null && checkU.equals(CALC.ONE)) {
                    //System.out.println("U is one: " + checkU);
                    CalcObject result = integrate(toBeInt, var);
                    String resultString = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(CALC.POWER.createFunction(coef, CALC.NEG_ONE), result)).toString().replaceAll(var.toString(), "(" + inOfFunc.toString() + ")");
                    CalcParser parser = new CalcParser();
                    try {
                        return CALC.SYM_EVAL(parser.parse(resultString));
                    } catch (Exception e) {
                        System.err.println("error parsing new function");
                        e.printStackTrace(System.err);
                        return CALC.ERROR;
                        //return CALC.INT.createFunction(obj, var);
                    }
                } else {
                    CalcObject expanded = CALC.SYM_EVAL(CALC.EXPAND.createFunction(obj));
                    if (obj.equals(expanded)) {
                        if (recDepth < CALC.max_recursion_depth) {
                            CalcINTBYPARTS temp = new CalcINTBYPARTS(intByPartsCarryOver, recDepth);
                            return CALC.SYM_EVAL(temp.integrate(obj, var));
                        } else {
                            return CALC.ERROR;
                        }
                    } else {
                        //System.out.println(recDepth);
                        CalcINT tempInt = new CalcINT(recDepth);
                        CalcObject answer = CALC.SYM_EVAL(tempInt.integrate(expanded, var));
                        if (answer instanceof CalcError) {
                            if (recDepth < CALC.max_recursion_depth) {
                            CalcINTBYPARTS temp = new CalcINTBYPARTS(intByPartsCarryOver, recDepth);
                            return CALC.SYM_EVAL(temp.integrate(obj, var));
                        } else {
                            return CALC.ERROR;
                        }
                        } else {
                            return answer;
                        }
                    }
                }
                //System.out.println("U did not work out: " + checkU);
                //System.out.println("THIS IS THE OBJECT: " + obj);
                //System.out.println("THIS IS SECOND OBJ: " + secondObj);
                //return obj;
                //return CALC.INT.createFunction(obj, var);

                //we have gotten to here, u-sub has produced no result, must now try integration by parts
                //we assume there are only 2 sub functions in multiply and that the most "complex" functions are those with the most nested operations

                //perhaps this should be implemented along side with u sub

                //pull out all the parts necessary to make g'(x) for f'(g(x)), use it as dv, and leave the rest behind for u...

                /*
                 * who care which function is more difficult? this is a computer, simultaniously integrate every possibility until one of them completes.
                 * This avoids the infinite recursion problem.
                 * 6-21-13
                 * 
                 * 7-24-13
                 * Infinite recursion problem is still a thing, threads are generated faster than the program can shut them down. Will need to use thread pools...
                 * 
                 * 7-25-13 done :)
                 * 
                 * BUT OTHER PROBLEMS...
                 * 
                 * INPUT: INT(x*(1+x),x)
                 * ANSWER: ADD(MULTIPLY(x,ADD(x,MULTIPLY(0.5,POWER(x,2)))),MULTIPLY(-1,ADD(MULTIPLY(0.5,POWER(x,2)),MULTIPLY(0.16666665,POWER(x,3)))))
                 * OUT: x*(x+0.5*x^2)-(0.5*x^2+0.16666665*x^3)   <------Correct
                 * INPUT: INT(x*(1+x),x)
                 * ANSWER: ADD(MULTIPLY(0.5,ADD(1,x),POWER(x,2)),MULTIPLY(-0.16666665,POWER(x,3)))
                 * OUT: 0.5*(1+x)*x^2-0.16666665*x^3             <------Incorect
                 * 
                 * HOW IS THIS HAPPENING
                 * 
                 * program is running smooth... error is in the method itself. never integrate by parts unless other methods are exausted. Did it by hand
                 * and got diff answers depending on starting parameters...
                 * 
                 * Best bet is a problem with u sub... gotta note with thoughts //nope, but still a thing
                 * 
                 * NEW RULE, master thread cannot spawn new master threads...
                 * 
                 */

                //START INTEGRATION BY PARTS
                //System.out.println("Lets do integration by parts");
                //secondObj = function.get(1);
                //System.out.println("FirstObj: " + firstObj);
                //ArrayList<CalcObject> funcObjects = giveList(CALC.MULTIPLY, function);
                //return obj; //should never have to return obj at end, if statements above handle it
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
        //System.out.println("Integration Failed");
        //return obj;
        //return CALC.INT.createFunction(obj, var); //don't know how to integrate (yet). Return original expression.
        return CALC.ERROR;
        //return obj;
    }

    public boolean superEquals(CalcObject first, CalcObject second) {
        CalcObject firstObj = CALC.SYM_EVAL(CALC.EXPAND.createFunction(first));
        CalcObject secondObj = CALC.SYM_EVAL(CALC.EXPAND.createFunction(second));
        return firstObj.equals(secondObj);
    }

    public ArrayList<CalcObject> giveList(CalcSymbol operator, CalcObject func) {
        ArrayList<CalcObject> list = new ArrayList<>();
        //System.out.println(func);
        if (func instanceof CalcFunction && func.getHeader().equals(operator)) {
            ArrayList<CalcObject> funcParts = ((CalcFunction) func).getAll();
            for (int i = 0; i < funcParts.size(); i++) {
                CalcObject firstObj = funcParts.get(i);
                //if (firstObj instanceof CalcFunction && ((CalcFunction) firstObj).getHeader().equals(operator)) {
                list.addAll(giveList(operator, firstObj));
                //}
            }
            //System.out.println("LIST in loop" + list);
        } else {
            list.add(func);
            //System.out.println("LIST" + list);
        }
        return list;
    }
    //insert private method integrate(function, var, u-sub)
    //use for recursion to simplify other u-sub cases
}
