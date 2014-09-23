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
public class CalcTRIGSIMPLIFY implements CalcFunctionEvaluator {

    public CalcTRIGSIMPLIFY() {
    }

    @Override
    public CalcObject evaluate(CalcFunction function) {
        if (function.size() == 1) {
            return trigSimplify(function.get(0));
        } else {
            throw new CalcWrongParametersException("TRIGSIMPLIFY -> wrong number of parameters");
        }
    }

    public CalcObject trigSimplify(CalcObject obj) {
        //TRIGSIMPLIFY(SIN(x)^2+COS(x)^2)
        if (obj instanceof CalcFunction) {
            CalcFunction object = (CalcFunction) obj;
            if (object.getHeader().equals(CALC.ADD) && object.size() == 2) {//we have ?+?
                if (object.get(0) instanceof CalcFunction && object.get(1) instanceof CalcFunction) {//we have f(x)+?
                    CalcFunction input1 = (CalcFunction) object.get(0);
                    CalcFunction input2 = (CalcFunction) object.get(1);
                    //Testing SIN(f(x))^2+COS(f(x)^2)
                    if (input1.getHeader().equals(CALC.POWER) && input2.getHeader().equals(CALC.POWER)) { //?^?+?^?
                        if (input1.get(1).equals(CALC.TWO) && input2.get(1).equals(CALC.TWO)) { //?^2+?^2
                            //test for SIN(f(x))^2+COS(g(x))^2
                            if ((input1.get(0).getHeader().equals(CALC.SIN) && input2.get(0).getHeader().equals(CALC.COS))
                                    || (input1.get(0).getHeader().equals(CALC.COS) && input2.get(0).getHeader().equals(CALC.SIN))) {
                                // So far we know its at least SIN(f(x))^2+COS(g(x))^2
                                CalcObject first = ((CalcFunction) input1.get(0)).get(0);
                                CalcObject second = ((CalcFunction) input2.get(0)).get(0);
                                //System.out.println(first + " = " + second + " ? " + first.equals(second));
                                if (first.equals(second)) {
                                    //SIN(f(x))^2+COS(f(x))^2 = 1
                                    return CALC.ONE;
                                }
                            }
                            //test for SEC(f(x))^2-TAN(g(x))^2
                            //if ((input1.get(0).equals(CALC.SYM_EVAL(obj)) && input2.get(0).getHeader().equals(CALC.COS))
                            //        || (input1.get(0).getHeader().equals(CALC.COS) && input2.get(0).getHeader().equals(CALC.SIN))) {
                            //}
                        }
                    }
                }
                if (object.get(0).isNumber() && object.get(1) instanceof CalcFunction) {
                    CalcFunction input2 = (CalcFunction) object.get(1);
                    if (object.get(0).equals(CALC.ONE)) { // 1+?
                        if (input2.getHeader().equals(CALC.MULTIPLY) && input2.get(0).equals(CALC.NEG_ONE) && input2.get(1) instanceof CalcFunction) {
                            CalcFunction input2More = (CalcFunction) input2.get(1);
                            if (input2More.getHeader().equals(CALC.POWER)) { //1-?^?
                                if (input2More.get(1).equals(CALC.TWO)) { //1-?^2
                                    if ((input2More.get(0).getHeader().equals(CALC.SIN))) { //1-SIN(x)^2
                                        CalcObject innards = ((CalcFunction) input2More.get(0)).get(0);
                                        return CALC.POWER.createFunction(CALC.COS.createFunction(innards), CALC.TWO);
                                    }
                                    if ((input2More.get(0).getHeader().equals(CALC.COS))) { //1-COS(x)^2
                                        CalcObject innards = ((CalcFunction) input2More.get(0)).get(0);
                                        return CALC.POWER.createFunction(CALC.SIN.createFunction(innards), CALC.TWO);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (object.getHeader().equals(CALC.POWER)) { // ?^? (Double angle)
                //System.out.println("POWER BRANCH");
                if (object.get(1).equals(CALC.TWO)) { // ?^2
                    //System.out.println("TESTING " + object.get(0));
                    if (object.get(0).getHeader().equals(CALC.SIN)) { // SIN(x)^2
                        CalcObject innards = ((CalcFunction) object.get(0)).get(0);
                        //return 0.5-COS(2x)*0.5
                        return CALC.ADD.createFunction(CALC.MULTIPLY.createFunction(CALC.NEG_HALF, CALC.COS.createFunction(CALC.MULTIPLY.createFunction(CALC.TWO, innards))), CALC.HALF);
                    }
                    //System.out.println("TESTING COS");
                    if (object.get(0).getHeader().equals(CALC.COS)) { // COS(x)^2
                        CalcObject innards = ((CalcFunction) object.get(0)).get(0);
                        //return 0.5+0.5*COS(2x)
                        return CALC.ADD.createFunction(CALC.MULTIPLY.createFunction(CALC.D_HALF, CALC.COS.createFunction(CALC.MULTIPLY.createFunction(CALC.TWO, innards))), CALC.HALF);
                    }
                }
                if (object.get(1).equals(CALC.NEG_TWO)) { // ?^2
                    //System.out.println("TESTING " + object.get(0));
                    if (object.get(0).getHeader().equals(CALC.SIN)) { // SIN(x)^2
                        CalcObject innards = ((CalcFunction) object.get(0)).get(0);
                        //return 0.5-COS(2x)*0.5
                        return CALC.POWER.createFunction(CALC.ADD.createFunction(CALC.MULTIPLY.createFunction(CALC.NEG_HALF, CALC.COS.createFunction(CALC.MULTIPLY.createFunction(CALC.TWO, innards))), CALC.HALF), CALC.NEG_ONE);
                    }
                    //System.out.println("TESTING COS");
                    if (object.get(0).getHeader().equals(CALC.COS)) { // COS(x)^2
                        CalcObject innards = ((CalcFunction) object.get(0)).get(0);
                        //return 0.5+0.5*COS(2x)
                        return CALC.POWER.createFunction(CALC.ADD.createFunction(CALC.MULTIPLY.createFunction(CALC.D_HALF, CALC.COS.createFunction(CALC.MULTIPLY.createFunction(CALC.TWO, innards))), CALC.HALF), CALC.NEG_ONE);
                    }
                }
            }
        }
        return obj;
    }
}
