package javacalculus.evaluator;

import java.util.ArrayList;
import javacalculus.core.CALC;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.exception.CalcWrongParametersException;
import javacalculus.struct.*;

/**
 *
 *
 * @author Seva Luchianov
 */
public class CalcSIMPLIFY implements CalcFunctionEvaluator {

    public CalcSIMPLIFY() {
    }

    @Override
    public CalcObject evaluate(CalcFunction function) {
        if (function.size() == 1) {
            return simplify(function.get(0));
        } else {
            throw new CalcWrongParametersException("SIMPLIFY -> wrong number of parameters");
        }
    }

    public CalcObject simplify(CalcObject object) {
        object = CALC.SYM_EVAL(object);
        //System.out.println("SIMPLIFYING: " + object);
        if (object instanceof CalcFunction) {
            ArrayList<CalcObject> multiplyParts = giveList(CALC.MULTIPLY, object);
            CalcObject numeObj = CALC.ONE;
            CalcObject denomObj = CALC.ONE;
            for (CalcObject piece : multiplyParts) {
                if (piece instanceof CalcFunction && ((CalcFunction) piece).getHeader().equals(CALC.POWER) && ((CalcFunction) piece).get(1).compareTo(CALC.ZERO) < 0) {
                    denomObj = CALC.MULTIPLY.createFunction(denomObj, CALC.POWER.createFunction(((CalcFunction) piece).get(0), CALC.ABS.createFunction(((CalcFunction) piece).get(1))));
                } else {
                    numeObj = CALC.MULTIPLY.createFunction(numeObj, piece);
                }
            }
            denomObj = CALC.SYM_EVAL(denomObj);
            numeObj = CALC.SYM_EVAL(numeObj);
            //FACTORING CODE? yeah.
            //so this might be bad... lets see
            //System.out.println("DENOM:" + denomObj);
            //System.out.println("NUME:" + numeObj);
            denomObj = CALC.SYM_EVAL(CALC.FACTOR.createFunction(denomObj));
            numeObj = CALC.SYM_EVAL(CALC.FACTOR.createFunction(numeObj));
            //System.out.println("DENOM FACTORED:" + denomObj);
            //System.out.println("NUME FACTORED:" + numeObj);
            ArrayList<CalcObject> nume = giveList(CALC.MULTIPLY, numeObj);
            ArrayList<CalcObject> denom = giveList(CALC.MULTIPLY, denomObj);
            ////System.out.println(nume + "///");
            ////System.out.println("///" + denom);
            ArrayList<CalcObject> process = new ArrayList<>();
            for (int i = 0; i < nume.size(); i++) {
                CalcObject numerator = nume.get(i);
                for (int j = 0; j < denom.size(); j++) {
                    CalcObject denominator = denom.get(j);
                    //System.out.println("TESTING: " + numerator + " / " + denominator);
                    int initDepth = findTotalDepth(numerator) + findTotalDepth(denominator);
                    CalcObject toAdd = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(numerator, CALC.POWER.createFunction(denominator, CALC.NEG_ONE)));
                    int resultDepth = findTotalDepth(toAdd);
                    //System.out.println("INIT DEPTH: " + initDepth + " TOADD: " + toAdd + " with depth " + resultDepth);
                    if (resultDepth < initDepth) {
                        //System.out.println("ADDING:" + toAdd);
                        process.add(toAdd);
                        //System.out.println("PROCESS: "+process);
                        //System.out.println("OLD NUME: "+nume);
                        //System.out.println("OLD DENOM: "+denom);
                        nume.remove(i);
                        i--;
                        denom.remove(j);
                        j=denom.size();
                        //System.out.println("NEW NUME: "+nume);
                        //System.out.println("NEW DENOM: "+denom);
                    }
                }
            }
            //System.out.println("PROCESS:" + process);
            for (CalcObject piece : denom) {
                process.add(CALC.POWER.createFunction(piece, CALC.NEG_ONE));
            }
            for (CalcObject piece : nume) {
                process.add(piece);
            }
            //System.out.println("MORE PROCESS:" + process);
            CalcObject result = CALC.ONE;
            for (CalcObject piece : process) {
                result = CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(result, piece));
            }
            return result;
        } else {
            return object;
        }
    }

    private int findTotalDepth(CalcObject test) {
        ArrayList<CalcObject> allParts = giveList(test.getHeader(), test);
        int totalDepth = 0;
        for (CalcObject piece : allParts) {
            totalDepth += ((CalcInteger) CALC.SYM_EVAL(CALC.DEPTH.createFunction(piece))).intValue();
        }
        return totalDepth;
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
