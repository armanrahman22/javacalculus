package javacalculus.evaluator;

import javacalculus.core.CALC;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.exception.CalcWrongParametersException;
import javacalculus.struct.*;

/**
 *
 *
 * @author Seva Luchianov
 */
public class CalcTRIGEXPAND implements CalcFunctionEvaluator {

    public CalcTRIGEXPAND() {
    }

    @Override
    public CalcObject evaluate(CalcFunction function) {
        if (function.size() == 1) {
            return trigExpand(function.get(0));
        } else {
            throw new CalcWrongParametersException("TRIGEXPAND -> wrong number of parameters");
        }
    }

    public CalcObject trigExpand(CalcObject obj) {
        //TRIGSIMPLIFY(SIN(x)^2+COS(x)^2)
        if (obj instanceof CalcFunction) {
            CalcFunction object = (CalcFunction) obj;
            if (object.getHeader().equals(CALC.POWER)) { // ?^? (Double angle)
                //System.out.println("POWER BRANCH");
                CalcObject pow = object.get(1);
                if (pow.isNumber()) {
                    CalcDouble powerMult = (CalcDouble) CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(pow, CALC.HALF));
                    if (powerMult.isInteger()) {
                        if (!powerMult.isNegative()) { // ?^2
                            //System.out.println("TESTING " + object.get(0));
                            if (object.get(0).getHeader().equals(CALC.SIN)) { // SIN(x)^2
                                CalcObject innards = ((CalcFunction) object.get(0)).get(0);
                                //return 1-COS(x)^2
                                return CALC.POWER.createFunction(CALC.ADD.createFunction(CALC.MULTIPLY.createFunction(CALC.POWER.createFunction(CALC.COS.createFunction(innards), CALC.TWO), CALC.NEG_ONE), CALC.ONE), powerMult);
                            }
                            //System.out.println("TESTING COS");
                            if (object.get(0).getHeader().equals(CALC.COS)) { // COS(x)^2
                                CalcObject innards = ((CalcFunction) object.get(0)).get(0);
                                //return 1-SIN(x)^2
                                return CALC.POWER.createFunction(CALC.ADD.createFunction(CALC.MULTIPLY.createFunction(CALC.POWER.createFunction(CALC.SIN.createFunction(innards), CALC.TWO), CALC.NEG_ONE), CALC.ONE), powerMult);
                            }
                        } else { // ?^-2
                            //System.out.println("TESTING " + object.get(0));
                            if (object.get(0).getHeader().equals(CALC.SIN)) { // SIN(x)^2
                                CalcObject innards = ((CalcFunction) object.get(0)).get(0);
                                //return 1-COS(x)^2
                                return CALC.POWER.createFunction(CALC.ADD.createFunction(CALC.MULTIPLY.createFunction(CALC.POWER.createFunction(CALC.COS.createFunction(innards), CALC.TWO), CALC.NEG_ONE), CALC.ONE), CALC.MULTIPLY.createFunction(CALC.NEG_ONE, powerMult));
                            }
                            //System.out.println("TESTING COS");
                            if (object.get(0).getHeader().equals(CALC.COS)) { // COS(x)^2
                                CalcObject innards = ((CalcFunction) object.get(0)).get(0);
                                //return 1-SIN(x)^2
                                return CALC.POWER.createFunction(CALC.ADD.createFunction(CALC.MULTIPLY.createFunction(CALC.POWER.createFunction(CALC.SIN.createFunction(innards), CALC.TWO), CALC.NEG_ONE), CALC.ONE), CALC.MULTIPLY.createFunction(CALC.NEG_ONE, powerMult));
                            }
                        }
                    }
                }
            }
        }
        return obj;
    }
}
