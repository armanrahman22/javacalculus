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
        //System.out.println("CHECKING: " + object);
        if (object instanceof CalcFunction) {
            ArrayList<CalcObject> multiplyParts = giveList(CALC.MULTIPLY, object);
            CalcObject numeObj = CALC.ONE;
            CalcObject denomObj = CALC.ONE;
            for (CalcObject piece : multiplyParts) {
                if (piece instanceof CalcFunction && ((CalcFunction) piece).getHeader().equals(CALC.POWER) && ((CalcFunction) piece).get(1).equals(CALC.NEG_ONE)) {
                    denomObj = CALC.MULTIPLY.createFunction(denomObj, ((CalcFunction) piece).get(0));
                } else {
                    numeObj = CALC.MULTIPLY.createFunction(numeObj, piece);
                }
            }
            denomObj = CALC.SYM_EVAL(denomObj);
            numeObj = CALC.SYM_EVAL(numeObj);
            ArrayList<CalcObject> nume = giveList(CALC.MULTIPLY, numeObj);
            ArrayList<CalcObject> denom = giveList(CALC.MULTIPLY, denomObj);
            //System.out.println(nume + "///");
            //System.out.println("///" + denom);
            ArrayList<CalcObject> process = new ArrayList<>();
            boolean foundSomething = false;
            for (int i = 0; i < nume.size(); i++) {
                CalcObject piece = nume.get(i);
                int index = containsPiece(denom, piece);
                if (index != -1) {
                    foundSomething = true;
                    process.add(CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(piece, CALC.POWER.createFunction(denom.get(index), CALC.NEG_ONE))));
                    denom.remove(index);
                } else {
                    process.add(piece);
                }
                nume.remove(i);
                i--;
            }
            for (CalcObject piece : denom) {
                process.add(CALC.POWER.createFunction(piece, CALC.NEG_ONE));
            }
            if (foundSomething) {
                CalcObject result = CALC.ONE;
                for (CalcObject piece : process) {
                    result = CALC.MULTIPLY.createFunction(result, piece);
                }
                return CALC.SYM_EVAL(result);
            } else {
                return CALC.MULTIPLY.createFunction(numeObj, CALC.POWER.createFunction(denomObj, CALC.NEG_ONE));
            }
        } else {
            return object;
        }
    }

    private int containsPiece(ArrayList<CalcObject> denom, CalcObject piece) {
        CalcObject testPiece;
        for (int i = 0; i < denom.size(); i++) {
            testPiece = denom.get(i);
            if (testPiece.equals(piece)) {
                return i;
            }
            if (testPiece instanceof CalcFunction) {
                CalcFunction temp = (CalcFunction) testPiece;
                if (temp.getHeader().equals(CALC.POWER)) {
                    if (temp.get(1).isNumber() && temp.get(0).equals(piece)) {
                        return i;
                    }
                }
            }
        }
        return -1;
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
}
