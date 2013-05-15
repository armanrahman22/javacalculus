package javacalculus.evaluator;

import java.util.ArrayList;
import java.util.Iterator;
import javacalculus.core.CALC;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.exception.CalcWrongParametersException;
import javacalculus.struct.CalcFunction;
import javacalculus.struct.CalcInteger;
import javacalculus.struct.CalcObject;
import javacalculus.struct.CalcSymbol;

public class CalcEXPAND implements CalcFunctionEvaluator {

    @Override
    public CalcObject evaluate(CalcFunction input) {
        if (input.size() == 1) {
            CalcObject obj = input.get(0);
            if (obj.getHeader().equals(CALC.ADD) && ((CalcFunction) obj).size() > 1) { //	EXPAND(y1+y2+...,x) = EXPAND(y1,x) + EXPAND(y2,x) + ...
                CalcFunction function = (CalcFunction) obj;
                CalcFunction functionB = new CalcFunction(CALC.ADD, function, 1, function.size());
                return CALC.ADD.createFunction(expand(function.get(0)), expand(functionB));
            } else {
                return expand(obj);
            }
        } else {
            throw new CalcWrongParametersException("INT -> wrong number of parameters");
        }
    }

    public CalcObject expand(CalcObject object) {
        CalcObject factored = CALC.ZERO;
        CalcObject obj = object;
        if (obj instanceof CalcFunction) { //input f(x..xn)
            obj = CALC.SYM_EVAL(obj); //evaluate the function before attempting to expand
        }
        System.out.println("WE ARE EXPANDING " + obj);
        if (obj.isNumber() || (obj instanceof CalcSymbol)) {
            System.out.println("you cant expand a number");
            return obj;
        }
        if (obj.getHeader().equals(CALC.POWER)) {
            CalcFunction function = (CalcFunction) obj;
            CalcObject firstObj = CALC.SYM_EVAL(function.get(0));
            CalcObject secondObj = CALC.SYM_EVAL(function.get(1));
            System.out.println("This is a function in the power branch: " + function);
            if (firstObj instanceof CalcFunction && secondObj.isNumber() && secondObj instanceof CalcInteger) {//f(x)^k
                int pow = ((CalcInteger) secondObj).intValue();
                System.out.println("WE ARE IN THE f(x)^k branch");
                Iterator iter = ((CalcFunction) firstObj).iterator();
                ArrayList<CalcObject> resultFunc = new ArrayList<>();
                System.out.println("This is the first part of the function " + firstObj);
                System.out.println("This is the second part of the function " + secondObj);
                while (iter.hasNext()) {
                    resultFunc.add(CALC.SYM_EVAL(CALC.EXPAND.createFunction(CALC.MULTIPLY.createFunction((CalcObject) iter.next(), firstObj))));
                }
                System.err.println(resultFunc);
                for (CalcObject temp : resultFunc) {
                    factored = CALC.SYM_EVAL(CALC.ADD.createFunction(factored, temp));
                }
                for (int i = 0; i < pow - 2; i++) {
                    factored = CALC.SYM_EVAL(CALC.EXPAND.createFunction(CALC.MULTIPLY.createFunction(firstObj, factored)));
                }
                System.out.println("RESULT of f(x)^k: " + factored + "\n" + CALC.SYM_EVAL(factored));
                return factored;
            }
        } else if (obj.getHeader().equals(CALC.MULTIPLY)) {
            CalcFunction function = (CalcFunction) obj;
            ArrayList<CalcObject> allParts = function.getAll();
            CalcObject firstObj = CALC.SYM_EVAL(allParts.get(0));
            CalcObject secondObj = CALC.ONE;
            for (int i = 1; i < allParts.size(); i++) {
                secondObj = CALC.SYM_EVAL(CALC.EXPAND.createFunction(CALC.MULTIPLY.createFunction(secondObj, allParts.get(i))));
            }
            System.out.println("This is a function in the multiply branch: " + function);
            System.out.println("This is the first part of the function " + firstObj);
            System.out.println("This is the second part of the function " + secondObj);
            if (firstObj.isNumber() || (firstObj instanceof CalcSymbol)) {//this is the k*f(x) branch
                System.out.println("firstObj " + firstObj + " is a number or symbol");
                if (secondObj.isNumber() || (secondObj instanceof CalcSymbol) || !secondObj.getHeader().equals(CALC.ADD)) {//this is a*b
                    System.out.println("secondObj " + secondObj + " is a number or a symbol and not ADD");
                    return CALC.SYM_EVAL(CALC.MULTIPLY.createFunction(firstObj, secondObj));
                } else {//this if k*f(x)
                    System.out.println("secondObj " + secondObj + " is an ADD function");
                    Iterator iter = ((CalcFunction) secondObj).iterator();
                    System.out.println("This is the first part of the function " + firstObj);
                    System.out.println("This is the second part of the function " + secondObj);
                    while (iter.hasNext()) {
                        factored = CALC.SYM_EVAL(CALC.ADD.createFunction(factored, CALC.MULTIPLY.createFunction(firstObj, (CalcObject) iter.next())));
                    }
                    System.out.println("RESULT of k*f(x): " + factored + "\n" + factored);
                    return factored;
                }
            } else if (firstObj.getHeader().equals(CALC.ADD)) {//this is f(x)*g(x)
                System.out.println("WE ARE IN THE f(x)*g(x) branch");
                Iterator iter = ((CalcFunction) firstObj).iterator();
                ArrayList<CalcObject> resultFunc = new ArrayList<>();
                System.out.println("This is the first part of the function " + firstObj);
                System.out.println("This is the second part of the function " + secondObj);
                while (iter.hasNext()) {
                    resultFunc.add(CALC.SYM_EVAL(CALC.EXPAND.createFunction(CALC.MULTIPLY.createFunction((CalcObject) iter.next(), secondObj))));
                }
                System.err.println(resultFunc);
                for (CalcObject temp : resultFunc) {
                    factored = CALC.SYM_EVAL(CALC.ADD.createFunction(factored, temp));
                }
                System.out.println("RESULT of f(x)*g(x): " + factored + "\n" + CALC.SYM_EVAL(factored));
                return factored;
            } else if (secondObj.getHeader().equals(CALC.ADD)) {
                System.out.println("WE ARE IN THE g(x)*f(x) branch");
                Iterator iter = ((CalcFunction) secondObj).iterator();
                ArrayList<CalcObject> resultFunc = new ArrayList<>();
                System.out.println("This is the first part of the function " + firstObj);
                System.out.println("This is the second part of the function " + secondObj);
                while (iter.hasNext()) {
                    resultFunc.add(CALC.SYM_EVAL(CALC.EXPAND.createFunction(CALC.MULTIPLY.createFunction((CalcObject) iter.next(), firstObj))));
                }
                System.err.println(resultFunc);
                for (CalcObject temp : resultFunc) {
                    factored = CALC.SYM_EVAL(CALC.ADD.createFunction(factored, temp));
                }
                System.out.println("RESULT of g(x)*f(x): " + factored + "\n" + CALC.SYM_EVAL(factored));
                return factored;
            }
        }
        return obj;
    }
}