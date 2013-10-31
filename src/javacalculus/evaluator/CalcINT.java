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
            //System.out.println("FINNA EVAL THAT OBJ");
            obj = CALC.SYM_EVAL(obj); //evaluate the function before attempting integration
        }
        //////////do u sub before any operation
        if (recDepth < CALC.max_recursion_depth) {
            //System.out.println("U-SUBBING: " + obj);
            CalcObject[] uSub = parseU(obj, var);
            if (uSub != null) {
                //System.out.println("FUNC: " + uSub[0]);
                //System.out.println("X=>U: " + uSub[1]);
                CalcINT doit = new CalcINT(recDepth);
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
            //System.out.println("INTEGRATING NUMBER");
            return CALC.MULTIPLY.createFunction(obj, var);
        }
        if (obj.equals(var)) { //	INT(x, x) = x^2/2
            //System.out.println("INTEGRATING VAR");
            return CALC.MULTIPLY.createFunction(CALC.POWER.createFunction(var, CALC.TWO), CALC.HALF);
        }
        if (obj.getHeader().equals(CALC.ADD) && ((CalcFunction) obj).size() > 1) { //	INT(y1+y2+...,x) = INT(y1,x) + INT(y2,x) + ...
            //System.out.println("INTEGRATING ADD");
            CalcFunction function = (CalcFunction) obj;
            CalcFunction functionB = new CalcFunction(CALC.ADD, function, 1, function.size());
            return CALC.ADD.createFunction(integrate(function.get(0), var), integrate(functionB, var));
        }
        if (obj.getHeader().equals(CALC.MULTIPLY)) {	//INT(c*f(x),x) = c*INT(f(x),x)
            //System.out.println("INTEGRATING MULTIPLY");
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
                        CalcINTBYPARTS temp = new CalcINTBYPARTS(recDepth + 1);
                        //System.out.println("GOING HARD MODE");
                        return CALC.SYM_EVAL(temp.integrate(obj, var));
                    } else {
                        //System.out.println("LOL2");
                        return CALC.ERROR;
                    }
                } else {
                    //System.out.println(recDepth);
                    CalcINT tempInt = new CalcINT(recDepth + 1);
                    CalcObject answer = CALC.SYM_EVAL(tempInt.integrate(expanded, var));
                    if (answer instanceof CalcError) {
                        if (CALC.full_integrate_mode && recDepth < CALC.max_recursion_depth) {
                            CalcINTBYPARTS temp = new CalcINTBYPARTS(recDepth + 1);
                            //System.out.println("EXPANSION BRANCH GOING HARD MODE");
                            return CALC.SYM_EVAL(temp.integrate(obj, var));
                        } else {
                            //System.out.println("LOL4");
                            return CALC.ERROR;
                        }
                    } else {
                        //System.out.println("LOL5");
                        return answer;
                    }
                }
            }
        }
        if (obj.getHeader().equals(CALC.POWER)) { //this part is probably trickiest (form f(x)^g(x)). A lot of integrals here does not evaluate into elementary functions
            //System.out.println("INTEGRATING POWER");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            CalcObject secondObj = function.get(1);
            ////System.out.println("POWER? " + firstObj);
            ////System.out.println("POWER2? " + secondObj);
            if (firstObj instanceof CalcSymbol) {
                ////System.out.println("Symbol");
                if (secondObj.isNumber() || secondObj instanceof CalcSymbol && !(secondObj.equals(var))) { //	INT(x^n,x) = x^(n+1)/(n+1)
                    if (!secondObj.equals(CALC.NEG_ONE)) {//handle 1/x
                        CalcObject temp = CALC.MULTIPLY.createFunction(
                                CALC.POWER.createFunction(firstObj, CALC.ADD.createFunction(secondObj, CALC.ONE)),
                                CALC.POWER.createFunction(CALC.ADD.createFunction(secondObj, CALC.ONE), CALC.NEG_ONE));
                        ////System.out.println("BROKEN? " + temp.toString());
                        //////System.out.println("WE ARE IN THE 1/x BRANCH");
                        //////System.out.println(temp);
                        return temp;
                    } else {
                        return CALC.LN.createFunction(CALC.ABS.createFunction(firstObj));
                    }
                }
            } else if (firstObj.isNumber()) {	// INT(c^x,x) = c^x/ln(c)
                if (secondObj instanceof CalcSymbol) {
                    ////System.out.println("WE are in here arent we?");
                    return CALC.MULTIPLY.createFunction(obj, CALC.POWER.createFunction(CALC.LN.createFunction(firstObj), CALC.NEG_ONE));
                } else {
                    ////System.out.println("this worked before...");
                    ////System.out.println(obj);
                    // INT(c^f(x),x) = IDK
                    return CALC.ERROR;
                }
            }
        }
        if (obj.getHeader().equals(CALC.LN)) {	//	INT(LN(x),x) = x*LN(x) - x
            //System.out.println("INTEGRATING LN");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.ADD.createFunction(
                        CALC.MULTIPLY.createFunction(var, obj),
                        CALC.MULTIPLY.createFunction(var, CALC.NEG_ONE));
            }
        }
        if (obj.getHeader().equals(CALC.SIN)) {	//	INT(SIN(x),x) = -COS(x)
            //System.out.println("INTEGRATING SIN");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.MULTIPLY.createFunction(CALC.NEG_ONE, CALC.COS.createFunction(firstObj));
            }
        }
        if (obj.getHeader().equals(CALC.COS)) {	//	INT(COS(x),x) = SIN(x)
            //System.out.println("INTEGRATING COS");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.SIN.createFunction(firstObj);
            }
        }
        if (obj.getHeader().equals(CALC.TAN)) {	//	INT(TAN(x),x) = -LN(|COS(x)|)
            //System.out.println("INTEGRATING TAN");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.MULTIPLY.createFunction(CALC.NEG_ONE,
                        CALC.LN.createFunction(CALC.ABS.createFunction(CALC.COS.createFunction(var))));
            }
        }
        if (obj.getHeader().equals(CALC.ABS)) {	//	INT(|x|,x) = x*|x|/2
            //System.out.println("INTEGRATING ABS");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.MULTIPLY.createFunction(var, CALC.HALF,
                        CALC.ABS.createFunction(var));
            }
        }
        //////System.out.println("Integration Failed");
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
        //////System.out.println(func);
        if (func instanceof CalcFunction && func.getHeader().equals(operator)) {
            ArrayList<CalcObject> funcParts = ((CalcFunction) func).getAll();
            for (int i = 0; i < funcParts.size(); i++) {
                CalcObject firstObj = funcParts.get(i);
                //if (firstObj instanceof CalcFunction && ((CalcFunction) firstObj).getHeader().equals(operator)) {
                list.addAll(giveList(operator, firstObj));
                //}
            }
            //////System.out.println("LIST in loop" + list);
        } else {
            list.add(func);
            //////System.out.println("LIST" + list);
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
            CalcObject test = allCandidates.get(i);
            if (test.isNumber() || test.equals(var)
                    || test.equals(CALC.MULTIPLY.createFunction(var, CALC.NEG_ONE)) //|| testConstantMult(test)) {
                    ) {
                allCandidates.remove(i);
                i--;
            }
        }
        for (int i = 0; i < allCandidates.size(); i++) {
            for (int j = i + 1; j < allCandidates.size(); j++) {
                CalcObject test = allCandidates.get(i);
                CalcObject check = allCandidates.get(j);
                if (test.equals(check)) {
                    ////System.out.println("KILLING: " + test);
                    allCandidates.remove(j);
                    j--;
                }
            }
        }
        //System.out.println("ALL CANDIDATES FILTERED: " + allCandidates);
        for (CalcObject testU : allCandidates) {
            //System.out.println("U IS: " + testU.toString());
            CalcObject diffTestU = CALC.SYM_EVAL(CALC.DIFF.createFunction(testU, var));
            //System.out.println("DIFF RESULT: " + diffTestU.toString());
            CalcObject testDiv = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(input, CALC.POWER.createFunction(diffTestU, CALC.NEG_ONE)));
            //System.out.println("RESULT: " + testDiv.toString());
            testDiv = CALC.SYM_EVAL(CALC.SIMPLIFY.createFunction(testDiv));
            //System.out.println("RESULT SIMPLIFIED: " + testDiv.toString());
            //testDiv = CALC.SYM_EVAL(CALC.SIMPLIFY.createFunction(testDiv));
            ////System.out.println("RESULT SIMPLIFIED AGAIN: " + testDiv.toString());
            CalcObject testResult = substitute(testDiv, testU);
            //String testResult = testDiv.toString().replace(testU.toString(), "VARIABLE");
            //System.out.println("REPLACED: " + testResult);
            if (0 == contains(testResult, var, 0)) {
                CalcObject[] uSub = new CalcObject[2];
                CalcObject result = USubReplace(testResult, var);
                //System.out.println("RESULT AGAIN: " + result);
                uSub[0] = result;
                uSub[1] = testU;
                return uSub;
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
                ////System.out.println("THE LIST: " + funcParts);
                ArrayList<CalcObject> temp = new ArrayList<>();
                temp.add(func);
                for (int i = 1; i < funcParts.size(); i++) {
                    CalcObject toAdd = combinePowers(funcParts.subList(i, funcParts.size()));
                    temp.add(toAdd);
                    list.addAll(parseNestedFunction(toAdd));
                    ////System.out.println("What: " + temp);

                }
                funcParts.addAll(temp);
                list.addAll(funcParts);
            } else {
                list.add(func);
                ////System.out.println("MAYBE THIS IS MULTIPLICATION? " + func);
                for (int i = 0; i < funcParts.size(); i++) {
                    CalcObject firstObj = funcParts.get(i);
                    ////System.out.println("MAYBE NOT? " + firstObj);
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

    private boolean testConstantMult(CalcObject input) {
        ArrayList<CalcObject> parts = giveList(CALC.MULTIPLY, input);
        if (parts.size() == 2) {
            if (parts.get(0).isNumber() && parts.get(1) instanceof CalcSymbol) {
                return true;
            }
        }
        return false;
    }

    private CalcObject substitute(CalcObject node, CalcObject u) {
        ////System.out.println("NODE: " + node + " U: " + u);
        if (node instanceof CalcFunction) {
            ////System.out.println("IM GOIN IN");
            CalcFunction func = (CalcFunction) node;
            ArrayList<CalcObject> objects = func.getAll();
            for (int i = 0; i < objects.size(); i++) {
                CalcObject substitute = substitute(objects.get(i), u);
                func.set(i, substitute);
            }
        }
        if (node.equals(u)) {
            ////System.out.println("WE GONNA REPLACE: " + node);
            node = CALC.USUB;
        }
        //else {
        //    //System.out.println("NOT GONNA REPLACE: " + node);
        //}
        return node;
    }

    private CalcObject USubReplace(CalcObject node, CalcObject var) {
        if (node instanceof CalcFunction) {
            CalcFunction func = (CalcFunction) node;
            ArrayList<CalcObject> objects = func.getAll();
            for (int i = 0; i < objects.size(); i++) {
                CalcObject substitute = USubReplace(objects.get(i), var);
                func.set(i, substitute);
            }
        } else {
            //System.out.println("LOOK AT MY LEAF: " + node);
            if (node.compareTo(CALC.USUB) == 0) {
                //System.out.println("WE FIXIN IT UP");
                return var;
            }
        }
        return node;
    }

    private int contains(CalcObject node, CalcObject test, int amount) {
        if (node instanceof CalcFunction) {
            ////System.out.println("IM GOIN IN");
            CalcFunction func = (CalcFunction) node;
            ArrayList<CalcObject> objects = func.getAll();
            for (int i = 0; i < objects.size(); i++) {
                amount = contains(objects.get(i), test, amount);
            }
        }
        if (node.compareTo(test) == 0) {
            amount++;
        }
        return amount;
    }
}