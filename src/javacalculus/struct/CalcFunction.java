/**
 * 
 */
package javacalculus.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javacalculus.core.CALC;
import javacalculus.evaluator.extend.CalcFunctionEvaluator;
import javacalculus.evaluator.extend.CalcOperatorEvaluator;
import java.io.Serializable;


/**
 * Represents all functions in an expression, including operators.
 * May take on an arbitrary number of CalcObject parameters, hence
 * forming a recursive N-ary tree. The type of function is represented by a
 * header CalcSymbol, which also contains a specific functionEvaluator that
 * evaluates this function.
 * @see CalcSymbol
 * @see javacalculus.evaluator.extend.CalcFunctionEvaluator
 * @author Duyun Chen <A HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>,
 * Seth Shannin <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *  
 *
 */

public class CalcFunction implements CalcObject, Iterable<CalcObject>, Serializable {
	
	/**
	 * Property constants
	 */
	public static final int NO_PROPERTY = 0x0000;
	public static final int ASSOCIATIVE_EVALUATED = 0x0001;
	public static final int COMMUTATIVE_EVALUATED = 0x0002;
	
	private CalcSymbol functionHeader;
	protected ArrayList<CalcObject> parameters = new ArrayList<CalcObject>();
	
	private int properties = 0x0000;
	
	/**
	 * Basic constructor with header CalcSymbol
	 * @param calcSymbol header symbol
	 */
	public CalcFunction(CalcSymbol calcSymbol) {
		functionHeader = calcSymbol;
	}
	
	/**
	 * Constructor with one parameter
	 * @param calcSymbol header symbol
	 * @param obj1 the parameter
	 */
	public CalcFunction(CalcSymbol calcSymbol, CalcObject obj1) {
		functionHeader = calcSymbol;
		add(obj1);
	}
	
	/**
	 * Constructor with two parameters
	 * @param calcSymbol header symbol
	 * @param obj1 first parameter
	 * @param obj2 second parameter
	 */
	public CalcFunction(CalcSymbol calcSymbol, CalcObject obj1, CalcObject obj2) {
		functionHeader = calcSymbol;
		add(obj1);
		add(obj2);
	}
	
	/**
	 * Constructor with three parameters
	 * @param calcSymbol
	 * @param obj1
	 * @param obj2
	 * @param obj3
	 */
	public CalcFunction(CalcSymbol calcSymbol, CalcObject obj1,
			CalcObject obj2, CalcObject obj3) {
		functionHeader = calcSymbol;
		add(obj1);
		add(obj2);
		add(obj3);
	}
	
	/**
	 * Constructor that creates a function from a certain range of parameters from another function
	 * @param calcSymbol the header
	 * @param function the function to be copied
	 * @param start start index on function
	 * @param end end index on function
	 */
	public CalcFunction(CalcSymbol calcSymbol, CalcFunction function, int start, int end) {
		functionHeader = calcSymbol;
		for (int ii = start; ii < end; ii++) {
			add(function.get(ii));
		}
	}
	
	/**
	 * Constructor that creates a function from a given header and parameter arraylist.
	 * @param calcSymbol
	 * @param params
	 */
	public CalcFunction(CalcSymbol calcSymbol, ArrayList<CalcObject> params) {
		functionHeader = calcSymbol;
		parameters = params;
	}
	

	/**
	 * 
	 * @param obj add obj to parameter list of this function
	 */
	public void add(CalcObject obj) {
		parameters.add(obj);
	}
	
	/**
	 * Adds all of the parameters from function into this function
	 * @param function
	 */
	public void addAll(CalcFunction function) {
		parameters.addAll(function.getAll());
	}
	
	public void addVariable(CalcSymbol var) {
		functionHeader.addVariable(var);
	}
	
	public CalcSymbol getVariable(int index) {
		return functionHeader.getVariable(index);
	}
	
	public void removeVariable(int index) {
		functionHeader.removeVariable(index);
	}
	
	public void removeAllVariables() {
		functionHeader.removeAllVariables();
	}
	
	public int getNumberOfVariables() {
		return functionHeader.getNumberOfVariables();
	}
	
	/**
	 * 
	 * @param symbol
	 * @return the index of <b>symbol</b> in <b>variables</b>. If not found, return -1.
	 */
	public int getVariableIndex(CalcSymbol symbol) {
		for (int ii = 0; ii < getNumberOfVariables(); ii++) {
			if (getVariable(ii).equals(symbol)) return ii;
		}
		return -1;
	}
	
	/**
	 * @param index
	 * @return parameter of this function at index
	 */
	public CalcObject get(int index) {
		return parameters.get(index);
	}
	
	/**
	 * 
	 * @return all parameters of this function
	 */
	public ArrayList<CalcObject> getAll() {
		return parameters;
	}
	
	/**
	 * 
	 * @param index remove parameter of this function at index
	 */
	public void remove(int index) {
		parameters.remove(index);
	}
	
	/**
	 * replace the parameter at index with obj
	 * @param index
	 * @param obj
	 */
	public void set(int index, CalcObject obj) {
		parameters.set(index, obj);
	}
	
	public void sort() {
		Collections.sort(parameters);
	}
	
	/**
	 * Set the header for the function
	 * @param newHeader
	 */
	public void setHeader(CalcSymbol newHeader) {
		functionHeader = newHeader;
	}
	
	/**
	 * @return the header symbol of this function
	 * @see CalcSymbol
	 */
	public CalcSymbol getHeader() {
		return functionHeader;
	}
	
	/**
	 * @return the properties associated with this function
	 */
	public final int getProperty() {
		return properties;
	}
	
	/**
	 * 
	 * @param prop
	 * @return true if prop is a property of this function. False otherwise.
	 */
	public final boolean hasProperty(int prop) {
		return ((prop & properties) == prop);
	}
	
	/**
	 * 
	 * @param startIndex
	 * @param comparee
	 * @param compareeStartIndex
	 * @return Whether this function's parameters since startIndex are equal to comparee function's 
	 * parameters since compareeStartIndex on comparee.
	 */
	public boolean equalsFromIndex(int startIndex, CalcFunction comparee, int compareeStartIndex) {
		if ((size() - startIndex) != (comparee.size() - compareeStartIndex)) {
			return false;
		}
		
		for (int ii = startIndex; ii < size(); ii++) {
			if (!get(ii).equals(comparee.get(compareeStartIndex++))) return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @return the number of parameters in this function
	 */
	public final int size() {
		return parameters.size();
	}
	
	@Override
	public Object clone() {
		return new CalcFunction(functionHeader, parameters);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CalcFunction) || !(obj instanceof CalcObject)) return false;
		
		if (!((CalcFunction)obj).getHeader().equals(functionHeader)) return false;
		
		if (((CalcFunction)obj).size() != size()) return false;
		
		for (int ii = 0; ii < size(); ii++)	{
			if (!(get(ii).equals(((CalcFunction)obj).get(ii)))) return false;
		}
		
		return true;
	}
	
	/**
	 * Convert the parameters of this function to StringBuffer
	 * @return the StringBuffer containing the parameters
	 */
	private StringBuffer parametersToString() {
		StringBuffer returnVal = new StringBuffer();
		
		for (int ii = 0; ii < parameters.size(); ii++) {
			returnVal.append(parameters.get(ii).toString());
			if (ii != parameters.size() - 1) {
				returnVal.append(",");		
			}
		}
		return returnVal;
	}
	
	/**
	 * 
	 * @param prop evaluation properties
	 * @return a copy of the function with appropriate parameters evaluated
	 * @see CalcSymbol
	 */
	public CalcObject evaluateParameters() {
		CalcObject temp;
		CalcFunction result = (CalcFunction)clone();
		boolean evaluated = false;
		
		if (functionHeader.hasProperty(CalcSymbol.UNIPARAM_IDENTITY) && size() == 1) {
			return get(0);
		}
		if (!functionHeader.hasProperty(CalcSymbol.NO_EVAL_FIRST)) {
			temp = CALC.SYM_EVAL(get(0));
			if (temp != null) {
				result.set(0, temp);
				evaluated = true;
			}
		}
		if (!functionHeader.hasProperty(CalcSymbol.ONLY_EVAL_FIRST)) {
			for (int ii = 1; ii < size(); ii++) {
				temp = CALC.SYM_EVAL(get(ii));
				if (temp != null) {
					result.set(ii, temp);
					evaluated = true;
				}
			}
		}
		//Function is commutative. Order of parameters does not matter. Sort parameters for consistency.
		if (functionHeader.hasProperty(CalcSymbol.COMMUTATIVE) && !hasProperty(COMMUTATIVE_EVALUATED)) {
			result.sort();
			//properties |= COMMUTATIVE_EVALUATED;
		}
		//Function is associative. Convert f(x,f(y,z)...) to f(x,y,z....)
		if (functionHeader.hasProperty(CalcSymbol.ASSOCIATIVE) && !hasProperty(ASSOCIATIVE_EVALUATED)) {
			result = associativeSimplify();
		}
		if (evaluated) return result;
		else return null;
	}
	
	/**
	 * 
	 * @return f(x,y, f(z,w..)..) = f(x,y,z,w...)
	 */
	public CalcFunction associativeSimplify() {
		CalcFunction tempFunction = new CalcFunction(functionHeader);
		for (int ii = 0; ii < size(); ii++) {
			CalcObject current = get(ii);
			if (current instanceof CalcFunction && functionHeader.equals(current.getHeader())) {
				tempFunction.addAll((CalcFunction)current);
			}
			else {
				tempFunction.add(current);
			}
		}
		//properties |= ASSOCIATIVE_EVALUATED;
		return tempFunction;
	}
	
	@Override
	public CalcObject evaluate() throws Exception {
		return functionHeader.evaluateFunction(this);
	}	
	
	@Override
	public String toString() {
		if (CALC.operator_notation) {
			CalcFunctionEvaluator e = functionHeader.getEvaluator();
			if (e instanceof CalcOperatorEvaluator) {
				return ((CalcOperatorEvaluator)e).toOperatorString(this);
			}
		}
		
		StringBuffer out = new StringBuffer();
			
		out.append(functionHeader.toString());
		out.append("(");
		out.append(parametersToString());
		out.append(")");
			
		return out.toString();
	}

	@Override
	public boolean isNumber() {
		return false;
	}

	@Override
	public int compareTo(CalcObject obj) {
		if (getHierarchy() > obj.getHierarchy()) {
			return 1;
		}
		else if (getHierarchy() < obj.getHierarchy()) {
			return -1;
		}
		return 0;
	}

	@Override
	public int getHierarchy() {
		return CalcObject.FUNCTION;
	}

	@Override
	public int getPrecedence() {
		return functionHeader.getPrecedence();
	}

	@Override
	public Iterator<CalcObject> iterator() {
		return parameters.iterator();
	}
}
