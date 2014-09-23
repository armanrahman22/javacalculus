package javacalculus.struct;

import java.math.BigInteger;

import javacalculus.core.CALC;
import javacalculus.exception.CalcArithmeticException;
import javacalculus.exception.CalcUnsupportedException;
import java.io.Serializable;

/**
 * Hierarchially encapsulates the java integer. BigInteger is used as the medium
 * because of the lack of size restrictions.
 * @author Duyun Chen <A HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>,
 * Seth Shannin <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *  
 *
 */
public class CalcInteger implements CalcObject, Serializable {
	private BigInteger value;
	
	public CalcInteger(int in) {
		value = BigInteger.valueOf(in);
	}
	
	/**
	 * Constructor
	 * @param intString input string that represents an integer
	 * @param radix the radix (base) of the integer
	 */
	public CalcInteger(String intString, int radix) {
		value = new BigInteger(intString, radix);
	}
	
	public CalcInteger(String intString) {
		value = new BigInteger(intString, 10);
	}
	
	public CalcInteger(byte[] byteArrayIn) {
		value = new BigInteger(byteArrayIn);
	}
	
	public CalcInteger(BigInteger bigIntegerIn) {
		value = bigIntegerIn;
	}

	public int intValue() {
		return value.intValue();
	}
	
	public BigInteger bigIntegerValue() {
		return value;
	}
	
	/**
	 * 
	 * @return whether this integer is zero
	 */
	public boolean isZero() {
		return equals(CALC.ZERO);
	}
	
	/**
	 * 
	 * @return whether this integer is negative
	 */
	public boolean isNegative() {
		return (value.compareTo(CALC.ZERO.bigIntegerValue()) < 0);
	}
	
	/**
	 * 
	 * @return whether this integer is even
	 */
	public boolean isEven() {
		return (value.mod(CALC.TWO.bigIntegerValue()).equals(CALC.ZERO.bigIntegerValue()));
	}
	
	public CalcInteger negate() {
		return new CalcInteger(value.negate());
	}
	
	public CalcInteger add(CalcInteger input) {
		return new CalcInteger(value.add(input.bigIntegerValue()));
	}
	
	public CalcInteger multiply(CalcInteger input) {
		return new CalcInteger(value.multiply(input.bigIntegerValue()));
	}
	
	public CalcInteger divide(CalcInteger input) {
		return new CalcInteger(value.divide(input.bigIntegerValue()));
	}
	
	/**
	 * 
	 * @param n
	 * @return the nth power of this integer
	 */
	public CalcInteger power(int n) {
		return new CalcInteger(value.pow(n));
	}
	
	public CalcInteger mod(CalcInteger input) {
		return new CalcInteger(value.mod(input.bigIntegerValue()));
	}
	
	/**
	 * 
	 * @param n
	 * @return the nth root of this integer
	 */
	public CalcInteger root(int n) {
		if (isNegative() && ((n % 2) == 0)) throw new CalcArithmeticException("Even root of a negative number.");
		
		int temp = n - 1;
		CalcInteger Temp = new CalcInteger(temp);
		CalcInteger N = new CalcInteger(n);
		
		CalcInteger A = this;
		CalcInteger B = this.add(Temp).divide(N);
		
		while (B.compareTo(A) < 0) {
			A = B;
			B = add(Temp.multiply(A.power(temp)).divide(N.multiply(A.power(n))));
		}
		
		return A;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof CalcInteger) {
			return value.equals(((CalcInteger)obj).bigIntegerValue());
		}
		else if (obj instanceof CalcDouble) {
			if (value == null || !((CalcDouble)obj).isInteger()) return false;
			else return value.intValue() == (int)((CalcDouble)obj).doubleValue();
		}
		else return false;
	}
	
	@Override
	public boolean isNumber() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public CalcSymbol getHeader() {
		return CALC.INTEGER;
	}
	
	@Override
	public CalcObject evaluate() {
		return this;
	}
	
	public String toString() {
		return value.toString();
	}

	@Override
	public int compareTo(CalcObject obj) {
		if (obj.isNumber()) {
			if (obj instanceof CalcInteger) {
				return value.compareTo(((CalcInteger)obj).bigIntegerValue());
			}
			else if (obj instanceof CalcDouble) {
				if ((double)value.intValue() < ((CalcDouble)obj).doubleValue()) {
					return -1;
				}
				else if ((double)value.intValue() > ((CalcDouble)obj).doubleValue()) {
					return 1;
				}
				else return 0;
			}
			else throw new CalcUnsupportedException(obj.toString());
		}
		else if (getHierarchy() > obj.getHierarchy()) {
			return 1;
		}
		else if (getHierarchy() < obj.getHierarchy()) {
			return -1;
		}

		else return 0;
	}

	@Override
	public int getHierarchy() {
		return CalcObject.INTEGER;
	}

	@Override
	public int getPrecedence() {
		if (value.compareTo(BigInteger.ZERO) < 0) {
			//return 100;
		}
		return 9999999;
	}
}
