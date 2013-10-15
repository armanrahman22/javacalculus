package javacalculus.evaluator;

import java.util.ArrayList;
import javacalculus.core.CALC;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.exception.CalcWrongParametersException;
import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcInteger;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

/**
 * This function evaluator applies the FACTOR operator to a function.
 *
 * @author Seva Luchianov
 */
public class CalcFACTOR implements CalcFunctionEvaluator {

    @Override
    public CalcObject evaluate(CalcFunction input) {
        if (input.size() == 1) {
            CalcObject obj = input.get(0);
            ArrayList<CalcObject> allParts = giveList(CALC.MULTIPLY, obj);
            CalcObject result = CALC.ONE;
            for (CalcObject temp : allParts) {
                //System.out.println(result);
                result = CALC.MULTIPLY.createFunction(result, factor(temp));
            }
            //System.out.println(result);
            return CALC.SYM_EVAL(result);
        } else {
            throw new CalcWrongParametersException("FACTOR -> wrong number of parameters");
        }
    }

    public CalcObject factor(CalcObject object) {
        //EXPAND first, i.e. simplify, then factor out the basic similarites. not going to be factoring (ax^2+bx+c) just yet
        //object = CALC.SYM_EVAL(CALC.EXPAND.createFunction(object));
        if (object.getHeader().equals(CALC.ADD)) {
            CalcFunction func = (CalcFunction) object;
            //System.out.println("STARTING WITH:" + func);
            CalcObject firstPart = func.get(0);
            ArrayList<CalcObject> allParts = giveList(CALC.MULTIPLY, firstPart);
            //System.out.println("FACTOR CHECKING WITH:" + firstPart);
            //sort allParts in order of complexity, most complex first DONE
            //System.out.println("ALLPARTS:" + allParts);
            allParts = sort(allParts);
            //System.out.println("ALLPARTSSORTED:" + allParts);
            //foreach piece of func, check if piece contains allParts iter
            CalcObject newFunc;
            ArrayList<CalcObject> funcList = giveList(CALC.ADD, func);
            CalcObject temp = funcList.remove(0);
            for (CalcObject div : allParts) {
                //System.out.println("DIV:" + div);
                boolean win = true;
                //System.out.println("FUNCLIST:" + funcList);
                newFunc = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(temp, CALC.POWER.createFunction(div, CALC.NEG_ONE)));
                for (int i = 0; i < funcList.size(); i++) {
                    CalcObject piece = funcList.get(i);
                    //System.out.println("PIECE:" + piece);
                    CalcObject divResult = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(piece, CALC.POWER.createFunction(div, CALC.NEG_ONE)));
                    newFunc = CALC.ADD.createFunction(newFunc, divResult);
                    int depthInit = findTotalDepth(piece);
                    int depthFinal = findTotalDepth(divResult);
                    //System.out.println("INITIAL PART:" + piece + " with depth " + depthInit);
                    //System.out.println("FINAL PART:" + divResult + " with depth " + depthFinal);
                    if (piece.equals(divResult) || depthFinal > depthInit) {
                        //System.out.println("FAILED, " + divResult);
                        win = false;
                        i = funcList.size();
                    }
                }
                if (funcList.isEmpty()) {
                    win = false;
                }
                //System.out.println("Checks Finished, win=" + win);
                //after loop finishes, combine all removed parts and the remaining func pieces with CALC.MULTIPLY, and return
                if (win == true) {
                    newFunc = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(div, newFunc));
                    //System.out.println("NO ERRORS, new Func: " + newFunc + " AND OLD:" + object);
                    if (!newFunc.equals(object)) {
                        //return CALC.MULTIPLY.createFunction(div, factor(newFunc));
                        return factor(newFunc);
                    } else {
                        return object;
                    }
                    //return CALC.MULTIPLY.createFunction(div, newFunc);
                }
            }
            //System.out.println("Win was a faliure, OBJ: "+object);
            return object;
        }
        return object;
    }

    private int findTotalDepth(CalcObject test) {
        ArrayList<CalcObject> allParts = giveList(test.getHeader(), test);
        int totalDepth = 0;
        for (CalcObject piece : allParts) {
            totalDepth += ((CalcInteger) CALC.SYM_EVAL(CALC.DEPTH.createFunction(piece))).intValue();
        }
        return totalDepth;
    }

    /*private boolean superEquals(CalcObject first, CalcObject second) {
     CalcObject firstObj = CALC.SYM_EVAL(CALC.EXPAND.createFunction(first));
     CalcObject secondObj = CALC.SYM_EVAL(CALC.EXPAND.createFunction(second));
     return firstObj.equals(secondObj);
     }*/
    private ArrayList<CalcObject> sort(ArrayList<CalcObject> input) {
        if (input == null) {
            return null;
        }
        if (input.isEmpty()) {
            return input;
        }
        ArrayList<CalcObject> result = new ArrayList<>();
        result.add(input.remove(0));
        while (!input.isEmpty()) {
            CalcObject check = input.remove(0);
            boolean added = false;
            for (int i = 0; i < result.size(); i++) {
                if (compare(check, result.get(i))) {
                    result.add(i, check);
                    added = true;
                    i = result.size();
                }
            }
            if (!added) {
                result.add(check);
            }
        }
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).equals(CALC.ONE)) {
                result.remove(i);
                i--;
            }
        }
        ArrayList<CalcObject> boom = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            ////System.out.println("BOOM:" + boom);
            if (result.get(i).getHeader().equals(CALC.POWER)) {
                CalcObject pow = ((CalcFunction) result.get(i)).get(1);
                if (pow instanceof CalcInteger) {
                    CalcObject base = ((CalcFunction) result.get(i)).get(0);
                    ////System.out.println("WE GOT ONE:" + base + "^" + pow);
                    while (!pow.equals(CALC.ZERO)) {
                        boom.add(CALC.POWER.createFunction(base, pow));
                        if (((CalcInteger) pow).isNegative()) {
                            pow = CALC.SYM_EVAL(CALC.ADD.createFunction(pow, CALC.ONE));
                            ////System.out.println("INC:" + pow);
                        } else {
                            pow = CALC.SYM_EVAL(CALC.ADD.createFunction(pow, CALC.NEG_ONE));
                            ////System.out.println("DEC:" + pow);
                        }
                    }
                } else {
                    boom.add(result.get(i));
                }
            } else {
                boom.add(result.get(i));
            }
        }
        return boom;
    }

    //true if first is "better"
    private boolean compare(CalcObject first, CalcObject second) {
        if (first.getHeader().equals(CALC.POWER)) {
            if (second.getHeader().equals(CALC.POWER)) {
                return compare(((CalcFunction) first).get(1), ((CalcFunction) second).get(1));
            } else {
                return true;
            }
        }
        if (second.getHeader().equals(CALC.POWER)) {
            return false;
        }
        if (first instanceof CalcSymbol) {
            return true;
        }
        if (second instanceof CalcSymbol) {
            return false;
        }
        if (first.isNumber()) {
            return true;
        }
        if (second.isNumber()) {
            return false;
        }
        //variables, trigonometry, and everything else really is unranked for this sort
        return true;
    }

    private ArrayList<CalcObject> giveList(CalcSymbol operator, CalcObject func) {
        ArrayList<CalcObject> list = new ArrayList<>();
        ////System.out.println(func);
        if (func instanceof CalcFunction && func.getHeader().equals(operator)) {
            ArrayList<CalcObject> funcParts = ((CalcFunction) func).getAll();
            for (int i = 0; i < funcParts.size(); i++) {
                CalcObject firstObj = funcParts.get(i);
                //if (firstObj instanceof CalcFunction && ((CalcFunction) firstObj).getHeader().equals(operator)) {
                list.addAll(giveList(operator, firstObj));
                //}
            }
            ////System.out.println("LIST in loop" + list);
        } else {
            list.add(func);
            ////System.out.println("LIST" + list);
        }
        return list;
    }
}