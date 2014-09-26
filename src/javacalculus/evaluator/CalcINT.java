package javacalculus.evaluator;

import java.util.ArrayList;
import java.util.List;
import javacalculus.core.CALC;
import javacalculus.core.DeepCopy;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.exception.CalcWrongParametersException;
import javacalculus.struct.*;

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
        //System.out.println("ATTEMPTING TO INTEGRATE " + object.toString());
        CalcObject obj = object;
        if (obj instanceof CalcFunction) { //input f(x..xn)
            ////////System.out.println("FINNA EVAL THAT OBJ");
            obj = CALC.SYM_EVAL(obj); //evaluate the function before attempting integration
        }
        //System.out.println("DEPTH " + recDepth + " ATTEMPTING TO INTEGRATE " + object.toString());
        //////////do u sub before any operation
        if (recDepth < CALC.max_recursion_depth) {
            CalcObject sub = excecuteSub(obj, var);
            if (!sub.equals(CALC.ERROR)) {
                return sub;
            }
        }
        //////////
        if (obj.isNumber() || (obj instanceof CalcSymbol && !((CalcSymbol) obj).equals(var))) {	//	INT(c,x) = c*x
            ////////System.out.println("INTEGRATING NUMBER");
            return CALC.MULTIPLY.createFunction(obj, var);
        }
        if (obj.equals(var)) { //	INT(x, x) = x^2/2
            ////////System.out.println("INTEGRATING VAR");
            return CALC.MULTIPLY.createFunction(CALC.POWER.createFunction(var, CALC.TWO), CALC.HALF);
        }
        if (obj.getHeader().equals(CALC.ADD) && ((CalcFunction) obj).size() > 1) { //	INT(y1+y2+...,x) = INT(y1,x) + INT(y2,x) + ...
            ////////System.out.println("INTEGRATING ADD");
            CalcFunction function = (CalcFunction) obj;
            CalcFunction functionB = new CalcFunction(CALC.ADD, function, 1, function.size());
            return CALC.ADD.createFunction(integrate(function.get(0), var), integrate(functionB, var));
        }
        if (obj.getHeader().equals(CALC.MULTIPLY) || obj.getHeader().equals(CALC.POWER) && ((CalcFunction) obj).get(0).getHeader().equals(CALC.LN)) {	//INT(c*f(x),x) = c*INT(f(x),x)
            ////////System.out.println("INTEGRATING MULTIPLY");
            CalcFunction function = new CalcFunction(CALC.MULTIPLY);
            function.addAll((CalcFunction) obj);
            //function = (CalcFunction) CALC.SYM_EVAL(function);
            CalcObject firstObj = function.get(0);
            if (firstObj.isNumber()) {
                return CALC.MULTIPLY.createFunction(function.get(0),
                        integrate(new CalcFunction(CALC.MULTIPLY, function, 1, function.size()), var));
            } else { //	INT(f(x)*g(x),x) = ?? (u-sub)
                //TODO There are known combos that can be integrated
                //SEC(x)*TAN(x) => SEC(x)
                //CSC(x)*COT(x) => -CSC(x)
                //CalcObject tempx = (CalcObject) DeepCopy.copy(obj);

                //WHAT THE FUCK IS THIS
                /*CalcObject wtf;
                 CalcParser p = new CalcParser();
                 try {
                 wtf = p.parse(obj.toString());
                 } catch (CalcSyntaxException ex) {
                 return CALC.ERROR;
                 }*/
                //CalcObject expanded = CALC.SYM_EVAL(CALC.EXPAND.createFunction(wtf));
                //System.out.println("LETS EXPAND " + obj);
                CalcObject expanded = CALC.SYM_EVAL(CALC.EXPAND.createFunction(obj));
                //System.out.println(wtf);
                //System.out.println("IN MULTIPLICATION, " + obj + " AND EXPANDED " + expanded);

                //LIKE WHAT THE ACTUAL FUCK
                if (obj.equals(expanded)) {
                    //System.out.println("NOT EXPANSION BRANCH");
                    if (CALC.full_integrate_mode && recDepth < CALC.max_recursion_depth) {
                        CalcINTBYPARTS temp = new CalcINTBYPARTS(recDepth + 1);
                        //System.out.println("GOING HARD MODE");
                        return CALC.SYM_EVAL(temp.integrate(obj, var));
                    } else {
                        ////////System.out.println("LOL2");
                        return CALC.ERROR;
                    }
                } else {
                    //System.out.println("Expansion Branch");
                    ////////System.out.println(recDepth);
                    CalcINT tempInt = new CalcINT(recDepth + 1);
                    CalcObject answer = CALC.SYM_EVAL(tempInt.integrate(expanded, var));
                    if (answer instanceof CalcError) {
                        if (CALC.full_integrate_mode && recDepth < CALC.max_recursion_depth) {
                            CalcINTBYPARTS temp = new CalcINTBYPARTS(recDepth + 1);
                            //System.out.println("EXPANSION BRANCH GOING HARD MODE");
                            return CALC.SYM_EVAL(temp.integrate(obj, var));
                        } else {
                            ////////System.out.println("LOL4");
                            return CALC.ERROR;
                        }
                    } else {
                        ////////System.out.println("LOL5");
                        return answer;
                    }
                }
            }
        }
        //try parts here instead NO
        /*{
         System.out.println("WE ARE HERE");
         if (CALC.full_integrate_mode && recDepth < CALC.max_recursion_depth) {
         CalcObject temp = CALC.SYM_EVAL(new CalcINTBYPARTS(recDepth + 1).integrate(object, var));
         System.out.println("GOING HARD MODE");
         if (temp != null && !temp.equals(CALC.ERROR)) {
         return temp;
         }
         }
         }*/
        if (obj.getHeader().equals(CALC.POWER)) { //this part is probably trickiest (form f(x)^g(x)). A lot of integrals here does not evaluate into elementary functions
            ////////System.out.println("INTEGRATING POWER");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            CalcObject secondObj = function.get(1);
            //////////System.out.println("POWER? " + firstObj);
            //////////System.out.println("POWER2? " + secondObj);
            if (firstObj instanceof CalcSymbol) {
                //////////System.out.println("Symbol");
                if (secondObj.isNumber() || secondObj instanceof CalcSymbol && !(secondObj.equals(var))) { //	INT(x^n,x) = x^(n+1)/(n+1)
                    if (!secondObj.equals(CALC.NEG_ONE)) {//handle 1/x
                        CalcObject temp = CALC.MULTIPLY.createFunction(
                                CALC.POWER.createFunction(firstObj, CALC.ADD.createFunction(secondObj, CALC.ONE)),
                                CALC.POWER.createFunction(CALC.ADD.createFunction(secondObj, CALC.ONE), CALC.NEG_ONE));
                        //////////System.out.println("BROKEN? " + temp.toString());
                        ////////////System.out.println("WE ARE IN THE 1/x BRANCH");
                        ////////////System.out.println(temp);
                        return temp;
                    } else {
                        return CALC.LN.createFunction(CALC.ABS.createFunction(firstObj));
                    }
                }
            } else if (firstObj.isNumber()) {	// INT(c^x,x) = c^x/ln(c)
                if (secondObj instanceof CalcSymbol) {
                    //////////System.out.println("WE are in here arent we?");
                    return CALC.MULTIPLY.createFunction(obj, CALC.POWER.createFunction(CALC.LN.createFunction(firstObj), CALC.NEG_ONE));
                } else {
                    //////////System.out.println("this worked before...");
                    //////////System.out.println(obj);
                    // INT(c^f(x),x) = IDK
                    //return CALC.ERROR;
                }
            } else if (firstObj instanceof CalcFunction) { // INT(f(x)^c,x)
                if (secondObj.equals(CALC.NEG_TWO)) {
                    //Several cases we can handle
                    //COS(x)^(-2) = TAN(x)
                    //SIN(x)^(-2) = -COT(x)
                    if (firstObj.getHeader().equals(CALC.COS)) {
                        return CALC.TAN.createFunction(var);
                    }
                    if (firstObj.getHeader().equals(CALC.SIN)) {
                        return CALC.MULTIPLY.createFunction(CALC.COT.createFunction(var), CALC.NEG_ONE);
                    }
                } else { // INT(f(x)^g(x),x)
                }
            }
        }
        if (obj.getHeader().equals(CALC.LN)) {	//	INT(LN(x),x) = x*LN(x) - x
            ////////System.out.println("INTEGRATING LN");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.ADD.createFunction(
                        CALC.MULTIPLY.createFunction(var, obj),
                        CALC.MULTIPLY.createFunction(var, CALC.NEG_ONE));
            }
        }
        if (obj.getHeader().equals(CALC.SIN)) {	//	INT(SIN(x),x) = -COS(x)
            ////////System.out.println("INTEGRATING SIN");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.MULTIPLY.createFunction(CALC.NEG_ONE, CALC.COS.createFunction(firstObj));
            }
        }
        if (obj.getHeader().equals(CALC.COS)) {	//	INT(COS(x),x) = SIN(x)
            ////////System.out.println("INTEGRATING COS");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.SIN.createFunction(firstObj);
            }
        }
        //u-Sub now handles this case in terms of sin and cos
        /*if (obj.getHeader().equals(CALC.TAN)) {	//	INT(TAN(x),x) = -LN(|COS(x)|)
         //System.out.println("INTEGRATING TAN");
         CalcFunction function = (CalcFunction) obj;
         CalcObject firstObj = function.get(0);
         if (firstObj.equals(var)) {
         return CALC.MULTIPLY.createFunction(CALC.NEG_ONE,
         CALC.LN.createFunction(CALC.ABS.createFunction(CALC.COS.createFunction(var))));
         }
         }*/
        if (obj.getHeader().equals(CALC.ABS)) {	//	INT(|x|,x) = x*|x|/2
            ////////System.out.println("INTEGRATING ABS");
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = function.get(0);
            if (firstObj.equals(var)) {
                return CALC.MULTIPLY.createFunction(var, CALC.HALF,
                        CALC.ABS.createFunction(var));
            }
        }
        ////////////System.out.println("Integration Failed");
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
        ////////////System.out.println(func);
        if (func instanceof CalcFunction && func.getHeader().equals(operator)) {
            ArrayList<CalcObject> funcParts = ((CalcFunction) func).getAll();
            for (CalcObject firstObj : funcParts) {
                //if (firstObj instanceof CalcFunction && ((CalcFunction) firstObj).getHeader().equals(operator)) {
                list.addAll(giveList(operator, firstObj));
                //}
            }
        } else {
            list.add(func);
            ////////////System.out.println("LIST" + list);
        }
        return list;
    }

    private ArrayList<CalcObject> ParseCandidates(CalcObject input, CalcSymbol var) {
        ArrayList<CalcObject> objects = giveList(CALC.MULTIPLY, input);
        ArrayList<CalcObject> allCandidates = new ArrayList<>();
        for (CalcObject piece : objects) {
            allCandidates.addAll(parseNestedFunction(piece));
        }
        ////System.out.println("ALL CANDIDATES: " + allCandidates);
        for (int i = 0; i < allCandidates.size(); i++) {
            CalcObject test = allCandidates.get(i);
            if (test.isNumber() || test.equals(var)
                    || test.equals(CALC.MULTIPLY.createFunction(var, CALC.NEG_ONE))
                    || 0 != contains(test, CALC.USUB, 0)) {
                ////System.out.println("KILLING: " + test);
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
        return allCandidates;
    }

    public CalcObject[] doUSub(CalcObject input, CalcSymbol var) {
        ArrayList<CalcObject> allCandidates = ParseCandidates(input, var);
        ArrayList<CalcObject[]> reSub = new ArrayList<>();
        for (CalcObject testU : allCandidates) {
            ////System.out.println("U IS: " + testU.toString());
            // TODO if U is a trig sub, attempt to replace and continue
            CalcObject diffTestU = CALC.SYM_EVAL(CALC.DIFF.createFunction(testU, var));
            ////////System.out.println("DIFF RESULT: " + diffTestU.toString());
            if (!diffTestU.equals(CALC.ZERO)) {
                CalcObject testDiv = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction((CalcFunction) DeepCopy.copy(input), CALC.POWER.createFunction(diffTestU, CALC.NEG_ONE)));
                ////////System.out.println("RESULT: " + testDiv.toString());
                testDiv = CALC.SYM_EVAL(CALC.SIMPLIFY.createFunction(testDiv));
                ////////System.out.println("RESULT SIMPLIFIED: " + testDiv.toString());
                //testDiv = CALC.SYM_EVAL(CALC.SIMPLIFY.createFunction(testDiv));
                ////////////System.out.println("RESULT SIMPLIFIED AGAIN: " + testDiv.toString());
                CalcObject testResult = substitute(testDiv, testU, CALC.USUB);
                //String testResult = testDiv.toString().replace(testU.toString(), "VARIABLE");
                ////System.out.println("REPLACED: " + testResult);
                CalcObject[] uSub = new CalcObject[2];
                if (0 == contains(testResult, var, 0)) {
                    //System.out.println("It worked");
                    CalcObject result = USubReplace(testResult, var);
                    //System.out.println("RESULT AGAIN: " + result);
                    uSub[0] = result;
                    uSub[1] = testU;
                    ////System.out.println("OBJECTTTT " + input);
                    //////System.out.println("We cloned this a long time ago: "+temp);
                    return uSub;
                } else {
                    //System.out.println("WE MUST GO DEEPER, adding " + testResult);
                    uSub[0] = testResult;
                    uSub[1] = testU;
                    reSub.add(uSub);
                    //USubReplace(testResult, testU);
                    ////System.out.println(testResult.toString() + " IS INCOMPATIBLE");
                }
            } else {
                //////System.out.println("Div by 0");
            }
        }
        //System.out.println("USUB failed for " + input + " going balls deep into trig sub");
        //System.out.println(reSub.size() + " rejected candidates");
        for (CalcObject[] candidate : reSub) {
            ArrayList<CalcObject> trigSub = new ArrayList<>();
            trigSub.addAll(TrigSub(ParseCandidates(candidate[0], var), candidate[0]));
            for (CalcObject obj : trigSub) {
                CalcObject testResult = substitute(obj, candidate[1], CALC.USUB);
                if (0 == contains(testResult, var, 0)) {
                    //System.out.println("It worked");
                    CalcObject result = USubReplace(testResult, var);
                    //System.out.println("RESULT AGAIN: " + result);
                    CalcObject[] uSub = new CalcObject[2];
                    uSub[0] = result;
                    uSub[1] = candidate[1];
                    ////System.out.println("OBJECTTTT " + input);
                    //////System.out.println("We cloned this a long time ago: "+temp);
                    return uSub;
                }
            }
        }
        //System.out.println("USUB failed for TrigSub");
        return null;
    }

    private CalcObject excecuteSub(CalcObject input, CalcSymbol var) //First bucket contains new function, second contains u subbed piece
    {
        //System.out.println("Attempting u-SUB");
        if (!(input instanceof CalcFunction)) {
            //System.out.println("NOT A FUNCTION");
            return CALC.ERROR;
        }
        ArrayList<CalcObject> toIntegrate = new ArrayList<>();
        CalcObject[] sub = doUSub(input, var);
        if (sub != null) {
            //System.out.println("uSub Func: " + sub[0] + " Sub: " + sub[1]);
            CalcINT subIntegrator = new CalcINT(recDepth + 1);
            CalcObject intResult = CALC.SYM_EVAL(subIntegrator.integrate(sub[0], var));
            intResult = substitute(intResult, var, sub[1]);
            //System.out.println("INT RESULT: " + intResult);
            return intResult;
        }
        //System.out.println("Attempting trig IDENTS");
        toIntegrate.addAll(TrigSub(ParseCandidates(input, var), input));
        //System.out.println(toIntegrate.size() + " potential Functions after trig-Sub");
        //System.out.println(toIntegrate);
        for (CalcObject trigSubbed : toIntegrate) {
            CalcINT trigIntegrator = new CalcINT(recDepth + 1);
            CalcObject answer = CALC.SYM_EVAL(trigIntegrator.integrate((CalcObject) DeepCopy.copy(trigSubbed), var));
            if (!answer.equals(CALC.ERROR)) {
                //System.out.println("Trig Func: " + trigSubbed + " Int: " + answer);
                return answer;
            }
        }
        //System.out.println("TRig sub failed, attempting u-trig mix");
        for (CalcObject test : toIntegrate) {
            sub = doUSub(test, var);
            if (sub != null) {
                //System.out.println("Trig-uSub Func: " + sub[0] + " Sub: " + sub[1]);
                CalcINT trigIntegrator = new CalcINT(recDepth + 1);
                CalcObject intResult = CALC.SYM_EVAL(trigIntegrator.integrate(sub[0], var));
                intResult = substitute(intResult, var, sub[1]);
                //System.out.println("INT RESULT: " + intResult);
                return intResult;
            }
        }
        //System.out.println("USUB HAS FAILED");
        return CALC.ERROR;
    }

    private ArrayList<CalcObject> TrigSub(ArrayList<CalcObject> parts, CalcObject input) {
        ArrayList<CalcObject> toReturn = new ArrayList<>();
        if (!(input instanceof CalcFunction)) {
            return toReturn;
        }
        CalcFunction input1 = (CalcFunction) DeepCopy.copy(input);
        CalcFunction input2 = (CalcFunction) DeepCopy.copy(input);

        ////System.out.println("Try trig simplification");
        ArrayList<CalcObject> replacements = new ArrayList<>();
        replacements.ensureCapacity(parts.size());
        for (CalcObject prev : parts) {
            CalcObject eval = CALC.SYM_EVAL(CALC.TRIGEXPAND.createFunction(prev));
            if (!prev.equals(eval)) {
                replacements.add(eval);
            } else {
                replacements.add(CALC.ERROR);
            }
        }
        ////System.out.println(parts);
        ////System.out.println(replacements);
        //oh boy this is about to be more recursion isnt it?
        for (int i = 0; i < parts.size(); i++) {
            if (!replacements.get(i).equals(CALC.ERROR)) {
                CalcObject trigSubbed = CALC.SYM_EVAL(substitute(input1, parts.get(i), replacements.get(i)));
                ////System.out.println("TRIG SIMP SUBBED: " + trigSubbed);
                toReturn.add(trigSubbed);
                /*if (recDepth < CALC.max_recursion_depth) {
                 CalcINT trigIntegrator = new CalcINT(recDepth);
                 CalcObject answer = trigIntegrator.integrate(trigSubbed, var);
                 if (!answer.equals(CALC.ERROR)) {
                 ////System.out.println("NO ERROR IN TRIG SUB: " + answer);
                        
                 }
                 }*/
            }
        }
        ////System.out.println("Try trig expansion");
        replacements = new ArrayList<>();
        replacements.ensureCapacity(parts.size());
        for (CalcObject prev : parts) {
            CalcObject eval = CALC.SYM_EVAL(CALC.TRIGSIMPLIFY.createFunction(prev));
            if (!prev.equals(eval)) {
                replacements.add(eval);
            } else {
                replacements.add(CALC.ERROR);
            }
        }
        ////System.out.println(parts);
        ////System.out.println(replacements);
        ////System.out.println(input1);
        ////System.out.println(input2);
        //oh boy this is about to be more recursion isnt it?
        for (int i = 0; i < parts.size(); i++) {
            if (!replacements.get(i).equals(CALC.ERROR)) {
                ////System.out.println(replacements.get(i));
                CalcObject trigSubbed = CALC.SYM_EVAL(substitute(input2, parts.get(i), replacements.get(i)));
                ////System.out.println("TRIG EXPAND SUBBED: " + trigSubbed);
                toReturn.add(trigSubbed);
                /*if (recDepth < CALC.max_recursion_depth) {
                 CalcINT trigIntegrator = new CalcINT(recDepth);
                 CalcObject answer = trigIntegrator.integrate(trigSubbed, var);
                 if (!answer.equals(CALC.ERROR)) {
                 ////System.out.println("NO ERROR IN TRIG SUB: " + answer);
                 return answer;
                 }
                 }*/
            }
        }
        return toReturn;
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
                ////System.out.println("ADDING FUNC: "+func);
                temp.add(func);
                ////System.out.println("IN 000 : "+funcParts.get(0));
                //apparently i forgot to parse the inside of the power...
                temp.addAll(parseNestedFunction(funcParts.get(0)));//here it is
                ////System.out.println("WHAT DO WE HAVE NOW: "+funcParts);
                for (int i = 1; i < funcParts.size(); i++) {
                    CalcObject toAdd = combinePowers(funcParts.subList(i, funcParts.size()));
                    temp.add(toAdd);
                    list.addAll(parseNestedFunction(toAdd));
                    ////System.out.println("What: " + temp);
                }
                funcParts.addAll(temp);
                list.addAll(funcParts);
            } else {
                ////System.out.println("WHERE IS COS? "+func);
                list.add(func);
                for (CalcObject obj : funcParts) {
                    list.addAll(parseNestedFunction(obj));
                }
            }
        } else {
            ////System.out.println("NOT A FUNCTION: "+func);
            list.add(func);
        }
        return list;
    }

    private CalcObject combinePowers(List<CalcObject> list) {
        ////System.out.println("SOME SHIT SOMEWHERE: "+list);
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

    private CalcObject substitute(CalcObject node, CalcObject u, CalcObject replacement) {
        ////System.out.println("NODE: " + node + " U: " + u);
        if (node instanceof CalcFunction) {
            //////////System.out.println("IM GOIN IN");
            CalcFunction func = (CalcFunction) node;
            ArrayList<CalcObject> objects = func.getAll();
            for (int i = 0; i < objects.size(); i++) {
                CalcObject substitute = substitute(objects.get(i), u, replacement);
                func.set(i, substitute);
            }
        }
        if (node.equals(u)) {
            ////System.out.println("WE GONNA REPLACE: " + node);
            node = replacement;
        }
        //else {
        //    ////////System.out.println("NOT GONNA REPLACE: " + node);
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
            ////////System.out.println("LOOK AT MY LEAF: " + node);
            if (node.compareTo(CALC.USUB) == 0) {
                ////////System.out.println("WE FIXIN IT UP");
                return var;
            }
        }
        return node;
    }

    private int contains(CalcObject node, CalcObject test, int amount) {
        if (node instanceof CalcFunction) {
            //////////System.out.println("IM GOIN IN");
            CalcFunction func = (CalcFunction) node;
            ArrayList<CalcObject> objects = func.getAll();
            for (CalcObject object : objects) {
                amount = contains(object, test, amount);
            }
        }
        if (node.compareTo(test) == 0) {
            amount++;
        }
        return amount;
    }
}
