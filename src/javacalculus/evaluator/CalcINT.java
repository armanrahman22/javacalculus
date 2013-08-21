package javacalculus.evaluator;

import java.util.ArrayList;
import java.util.List;
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
                return integrate(function.get(0), (CalcSymbol) function.get(1));
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
        //////////do u sub before any operation
        if (recDepth < CALC.max_recursion_depth) {
            CalcObject[] uSub = parseU(obj, var);
            if (uSub != null) {
                //System.out.println("FUNC: " + uSub[0]);
                //System.out.println("U   : " + uSub[1]);
                CalcINT doit = new CalcINT(recDepth + 1);
                String resultString = CALC.SYM_EVAL(doit.integrate(uSub[0], var)).toString().replaceAll(var.toString(), "(" + uSub[1].toString() + ")");
                CalcParser parser = new CalcParser();
                try {
                    return CALC.SYM_EVAL(parser.parse(resultString));
                } catch (Exception e) {
                    System.err.println("error parsing new function");
                    e.printStackTrace(System.err);
                    return CALC.ERROR;
                }
            }
        }
        //////////
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
                CalcObject expanded = CALC.SYM_EVAL(CALC.EXPAND.createFunction(obj));
                if (obj.equals(expanded)) {
                    if (CALC.full_integrate_mode && recDepth < CALC.max_recursion_depth) {
                        CalcINTBYPARTS temp = new CalcINTBYPARTS(recDepth);
                        return CALC.SYM_EVAL(temp.integrate(obj, var));
                    } else {
                        return CALC.ERROR;
                    }
                } else {
                    //System.out.println(recDepth);
                    CalcINT tempInt = new CalcINT(recDepth);
                    CalcObject answer = CALC.SYM_EVAL(tempInt.integrate(expanded, var));
                    if (answer instanceof CalcError) {
                        if (CALC.full_integrate_mode && recDepth < CALC.max_recursion_depth) {
                            CalcINTBYPARTS temp = new CalcINTBYPARTS(recDepth);
                            return CALC.SYM_EVAL(temp.integrate(obj, var));
                        } else {
                            return CALC.ERROR;
                        }
                    } else {
                        return answer;
                    }
                }
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

    private boolean superEquals(CalcObject first, CalcObject second) {
        CalcObject firstObj = CALC.SYM_EVAL(CALC.EXPAND.createFunction(first));
        CalcObject secondObj = CALC.SYM_EVAL(CALC.EXPAND.createFunction(second));
        return firstObj.equals(secondObj);
    }

    private ArrayList<CalcObject> giveList(CalcSymbol operator, CalcObject func) {
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

    private CalcObject[] parseU(CalcObject input, CalcSymbol var) //First bucket contains new function, second contains u subbed piece
    {
        if (!(input instanceof CalcFunction)) {
            return null;
        }
        ArrayList<CalcObject> objects = giveList(CALC.MULTIPLY, input);
        //System.out.println("OBJECTS: " + objects);
        ArrayList<CalcObject> allCandidates = new ArrayList<>();
        for (CalcObject piece : objects) {
            allCandidates.addAll(parseNestedFunction(piece));
        }
        //System.out.println("ALL CANDIDATES: " + allCandidates);
        for (int i = 0; i < allCandidates.size(); i++) {
            if (allCandidates.get(i).isNumber() || allCandidates.get(i).equals(var) || allCandidates.get(i).equals(CALC.MULTIPLY.createFunction(var, CALC.NEG_ONE))) {
                allCandidates.remove(i);
                i--;
            }
        }
        for (CalcObject testU : allCandidates) {
            CalcObject diffTestU = CALC.SYM_EVAL(CALC.DIFF.createFunction(testU, var));
            CalcObject testDiv = CALC.SYM_EVAL(CALC.SIMPLIFY.createFunction(CALC.MULTIPLY.createFunction(input, CALC.POWER.createFunction(diffTestU, CALC.NEG_ONE))));
            CalcParser parser = new CalcParser();
            //System.out.println("RESULT: " + testDiv.toString());
            //System.out.println("U IS: " + testU.toString());
            String testResult = testDiv.toString().replace(testU.toString(), "VARIABLE");
            //System.out.println("REPLACED: " + testResult);
            if (!testResult.contains(var.toString())) {
                try {
                    CalcObject[] uSub = new CalcObject[2];
                    CalcObject result = CALC.SYM_EVAL(parser.parse(testResult.toString().replace("VARIABLE", var.toString())));
                    uSub[0] = result;
                    uSub[1] = testU;
                    return uSub;
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    private ArrayList<CalcObject> parseNestedFunction(CalcObject func) {
        ArrayList<CalcObject> list = new ArrayList<>();
        if (func instanceof CalcFunction) {
            CalcFunction function = (CalcFunction) func;
            CalcSymbol header = function.getHeader();
            ArrayList<CalcObject> funcParts = giveList(header, function);
            if (header.equals(CALC.POWER)) {
                ArrayList<CalcObject> temp = new ArrayList<>();
                for (int i = 0; i < funcParts.size(); i++) {
                    temp.add(combinePowers(funcParts.subList(i, funcParts.size())));
                }
                funcParts.addAll(temp);
                list.addAll(funcParts);
            } else {
                list.add(func);
                for (int i = 0; i < funcParts.size(); i++) {
                    CalcObject firstObj = funcParts.get(i);
                    list.addAll(parseNestedFunction(firstObj));
                }
            }
        } else {
            list.add(func);
        }
        return list;
    }

    private CalcObject combinePowers(List<CalcObject> list) {
        if (list == null) {
            return null;
        }
        CalcObject power = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            power = CALC.POWER.createFunction(power, list.get(i));
        }
        return power;
    }
}
