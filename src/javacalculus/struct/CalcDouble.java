package javacalculus.struct;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javacalculus.core.CALC;
import javacalculus.exception.CalcUnsupportedException;
import java.io.Serializable;

/**
 *
 * @author Duyun Chen <A
 * HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>, Seth Shannin
 * <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 * @see java.math.BigDecimal
 *
 */
public class CalcDouble implements CalcObject, Serializable{

    private BigDecimal value;
    private boolean isPositiveInfinity = false;
    private boolean isNegativeInfinity = false;
    private boolean isNaN = false;

    public CalcDouble() {
        value = new BigDecimal(0.0d);
    }

    public CalcDouble(BigDecimal bigDecimalIn) {
        //value = bigDecimalIn;
        if (CALC.fix_rounding_errors) {
            BigDecimal temp = bigDecimalIn;
            temp.setScale(0, RoundingMode.HALF_UP);
            //BigDecimal temp = new BigDecimal(bigDecimalIn, new MathContext(CALC.mathcontext.getPrecision(), RoundingMode.HALF_UP));
            int intVal = temp.intValue();
            double diff = Math.abs(intVal - bigDecimalIn.doubleValue());
            //System.out.println("DIFF: " + diff);
            if (diff < 0.00001) {
                //System.out.println("LETS MAKE " + bigDecimalIn.doubleValue() + " an int with value " + intVal);
                value = new BigDecimal(intVal, CALC.mathcontext);
            } else {
                value = new BigDecimal(bigDecimalIn.doubleValue(), CALC.mathcontext);
            }
        } else {
            value = new BigDecimal(bigDecimalIn.doubleValue(), CALC.mathcontext);
        }
    }

    public CalcDouble(double doubleIn) {
        if (!Double.isNaN(doubleIn) && !Double.isInfinite(doubleIn)) {
            if (CALC.fix_rounding_errors) {
                BigDecimal temp = new BigDecimal(doubleIn, CALC.mathcontext);
                temp.setScale(0, RoundingMode.HALF_UP);
                //BigDecimal temp = new BigDecimal(bigDecimalIn, new MathContext(CALC.mathcontext.getPrecision(), RoundingMode.HALF_UP));
                int intVal = temp.intValue();
                double diff = Math.abs(intVal - doubleIn);
                //System.out.println("DIFF: " + diff);
                if (diff < 0.00001) {
                    //System.out.println("LETS MAKE " + doubleIn + " an int with value " + intVal);
                    value = new BigDecimal(intVal, CALC.mathcontext);
                } else {
                    value = new BigDecimal(doubleIn, CALC.mathcontext);
                }
            } else {
                value = new BigDecimal(doubleIn, CALC.mathcontext);
            }
        } else { //mathcontext does not apply for infinitesimal values
            value = null;
            if (doubleIn == Double.POSITIVE_INFINITY) {
                isPositiveInfinity = true;
            }
            if (doubleIn == Double.NEGATIVE_INFINITY) {
                isNegativeInfinity = true;
            }
            if (Double.isNaN(doubleIn)) {
                isNaN = true;
            }
        }
    }

    public CalcDouble(String stringIn) {
        value = new BigDecimal(stringIn, CALC.mathcontext);
    }

    public CalcDouble(CalcInteger calcIntegerIn) {
        value = new BigDecimal(calcIntegerIn.bigIntegerValue());
    }

    public double doubleValue() {
        if (isPositiveInfinity) {
            return Double.POSITIVE_INFINITY;
        } else if (isNegativeInfinity) {
            return Double.NEGATIVE_INFINITY;
        } else if (isNaN) {
            return Double.NaN;
        } else {
            return value.doubleValue();
        }
    }

    public BigDecimal bigDecimalValue() {
        return value;
    }

    @Override
    public CalcObject evaluate() {
        if (CALC.fix_rounding_errors) {
            //System.out.println("STARTA");
            //System.out.println(value);
            BigDecimal temp = value;
            temp = temp.setScale(0, RoundingMode.HALF_UP);
            //System.out.println(value);
            //System.out.println(temp);
            //System.out.println("FINNA");
            //BigDecimal temp = new BigDecimal(bigDecimalIn, new MathContext(CALC.mathcontext.getPrecision(), RoundingMode.HALF_UP));
            int intVal = temp.intValue();
            //System.out.println("intval:" + intVal);
            //System.out.println("doubleval:" + value.doubleValue());
            double diff = Math.abs(intVal - value.doubleValue());
            //System.out.println("DIFF: " + diff);
            if (diff < 0.00001) {
                //System.out.println("LETS MAKE " + value.doubleValue() + " an int with value " + intVal);
                value = new BigDecimal(intVal, CALC.mathcontext);
            } else {
                value = new BigDecimal(value.doubleValue(), CALC.mathcontext);
            }
        }
        return this;
    }

    @Override
    public String toString() {
        if (isPositiveInfinity) {
            return "INFINITY";
        } else if (isNegativeInfinity) {
            return "-INFINITY";
        } else if (isNaN) {
            return "NaN";
        } else {
            return value.toString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CalcDouble) {
            if (isPositiveInfinity) {
                return ((CalcDouble) obj).isPositiveInfinity();
            }
            if (isNegativeInfinity) {
                return ((CalcDouble) obj).isNegativeInfinity();
            }
            if (isNaN) {
                return ((CalcDouble) obj).isNaN();
            }
            return value.doubleValue() == (((CalcDouble) obj).doubleValue());
        } else if (obj instanceof CalcInteger) {
            if (value == null) {
                return false;
            } else {
                return value.doubleValue() == (double) ((CalcInteger) obj).intValue();
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isNumber() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isEven() {
        return mod(CALC.D_TWO).equals(CALC.D_ZERO);
    }

    public boolean isPositiveInfinity() {
        return isPositiveInfinity;
    }

    public boolean isNegativeInfinity() {
        return isNegativeInfinity;
    }

    public boolean isNaN() {
        return isNaN;
    }

    @Override
    public CalcSymbol getHeader() {
        return CALC.DOUBLE;
    }

    public CalcDouble add(CalcDouble input) {
        if (isNaN || input.isNaN()) {
            return new CalcDouble(Double.NaN);
        } else if (isPositiveInfinity) {
            return input.isNegativeInfinity() ? new CalcDouble(0.0D) : new CalcDouble(Double.POSITIVE_INFINITY);
        } else if (isNegativeInfinity) {
            return input.isPositiveInfinity() ? new CalcDouble(0.0D) : new CalcDouble(Double.NEGATIVE_INFINITY);
        } else if (input.isPositiveInfinity()) {
            return isNegativeInfinity() ? new CalcDouble(0.0D) : new CalcDouble(Double.POSITIVE_INFINITY);
        } else if (input.isNegativeInfinity()) {
            return isPositiveInfinity() ? new CalcDouble(0.0D) : new CalcDouble(Double.NEGATIVE_INFINITY);
        } else {
            return new CalcDouble(value.add(input.bigDecimalValue()));
        }
    }

    public CalcDouble multiply(CalcDouble input) {
        if (isNaN || input.isNaN()) {
            return new CalcDouble(Double.NaN);
        } else if (isPositiveInfinity) {
            return input.isNegative() ? new CalcDouble(Double.NEGATIVE_INFINITY) : new CalcDouble(Double.POSITIVE_INFINITY);
        } else if (isNegativeInfinity) {
            return input.isNegative() ? new CalcDouble(Double.POSITIVE_INFINITY) : new CalcDouble(Double.NEGATIVE_INFINITY);
        } else if (input.isPositiveInfinity()) {
            return isNegative() ? new CalcDouble(Double.NEGATIVE_INFINITY) : new CalcDouble(Double.POSITIVE_INFINITY);
        } else if (input.isNegativeInfinity()) {
            return isNegative() ? new CalcDouble(Double.POSITIVE_INFINITY) : new CalcDouble(Double.NEGATIVE_INFINITY);
        } else {
            return new CalcDouble(value.multiply(input.bigDecimalValue()));
        }
    }

    public CalcDouble divide(CalcDouble input) {
        if (isNaN || input.isNaN()) {
            return new CalcDouble(Double.NaN);
        } else if (isPositiveInfinity) {
            if (input.isPositiveInfinity()) {
                return new CalcDouble(1.0D);
            } else if (input.isNegativeInfinity()) {
                return new CalcDouble(-1.0D);
            } else {
                return new CalcDouble(Double.POSITIVE_INFINITY);
            }
        } else if (isNegativeInfinity) {
            if (input.isPositiveInfinity()) {
                return new CalcDouble(-1.0D);
            } else if (input.isNegativeInfinity()) {
                return new CalcDouble(1.0D);
            } else {
                return new CalcDouble(Double.NEGATIVE_INFINITY);
            }
        }
        return new CalcDouble(value.divide(input.bigDecimalValue(), CALC.mathcontext));
    }

    //I DONT LIKE THIS... USING DOUBLES
    public CalcDouble power(CalcDouble input) {
        if (isNaN || input.isNaN()) {
            return new CalcDouble(Double.NaN);
        } else {
            return new CalcDouble(Math.pow(doubleValue(), input.doubleValue()));
        }
        //value.pow(input.doubleValue(), CALC.mathcontext);
    }

    /*public CalcDouble power(CalcInteger input) {
     if (isNaN) {
     return new CalcDouble(Double.NaN);
     } else {
     return new CalcDouble(value.pow(input.intValue(), CALC.mathcontext));
     }

     }*/
    public CalcDouble mod(CalcDouble input) {
        return new CalcDouble(value.remainder(input.bigDecimalValue()));
    }

    public boolean isDivisibleBy(CalcDouble input) {
        return mod(input).equals(CALC.D_ZERO);
    }

    public boolean isNegative() {
        if (value == null) {
            return isNegativeInfinity;
        } else {
            return compareTo(CALC.D_ZERO) < 0;
        }
    }

    public CalcDouble negate() {
        if (isPositiveInfinity) {
            return new CalcDouble(Double.NEGATIVE_INFINITY);
        } else if (isNegativeInfinity) {
            return new CalcDouble(Double.POSITIVE_INFINITY);
        } else if (isNaN) {
            return new CalcDouble(Double.NaN);
        } else {
            return new CalcDouble(value.negate());
        }
    }

    public boolean isInteger() {
        if (isNaN || isPositiveInfinity || isNegativeInfinity) {
            return false;
        }
        return (mod(CALC.D_ONE).equals(CALC.D_ZERO));
    }

    public boolean isInfinity() {
        return isPositiveInfinity || isNegativeInfinity;
    }

    @Override
    public int compareTo(CalcObject obj) {
        if (isNaN) {
            return 0;
        }

        if (obj.isNumber()) {
            if (obj instanceof CalcInteger) {
                if (isPositiveInfinity) {
                    return 1;
                } else if (isNegativeInfinity) {
                    return -1;
                } else if (value.doubleValue() < (double) ((CalcInteger) obj).intValue()) {
                    return -1;
                } else if (value.doubleValue() > (double) ((CalcInteger) obj).intValue()) {
                    return 1;
                } else {
                    return 0;
                }
            } else if (obj instanceof CalcDouble) {
                if (isPositiveInfinity) {
                    if (((CalcDouble) obj).isPositiveInfinity()) {
                        return 0;
                    } else if (((CalcDouble) obj).isNegativeInfinity()) {
                        return 1;
                    } else {
                        return 1;
                    }
                } else if (isNegativeInfinity) {
                    if (((CalcDouble) obj).isPositiveInfinity()) {
                        return -1;
                    } else if (((CalcDouble) obj).isNegativeInfinity()) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else if (isNaN || ((CalcDouble) obj).isNaN()) {
                    return 0;
                } else {
                    return value.compareTo(((CalcDouble) obj).bigDecimalValue());
                }
            } else {
                throw new CalcUnsupportedException(obj.toString());
            }
        }
        if (getHierarchy() > obj.getHierarchy()) {
            return 1;
        } else if (getHierarchy() < obj.getHierarchy()) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public int getHierarchy() {
        return CalcObject.DOUBLE;
    }

    @Override
    public int getPrecedence() {
        return 9999999; //it's over NINE MILLION AGAIN!!!!
    }
}
