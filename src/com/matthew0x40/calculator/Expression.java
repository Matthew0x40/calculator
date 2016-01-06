package com.matthew0x40.calculator;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Expression {
	public ArrayList<Variable> var = new ArrayList<Variable>();
	public String exp;
	static final ArrayList<Character> var_name = new ArrayList<Character>();
	
	public static class Variable {
		public String name = "";
		public double value = 0;
		
		public Variable() {}
		public Variable(String name) {
			this.name = name;
		}
		public Variable(String name, double value) {
			this.name = name;
			this.value = value;
		}
		
		public String roundedValue(int decPlaces) {
			StringBuilder places = new StringBuilder();
			for (int i = 0; i < decPlaces; i++)
				places.append('#');
			DecimalFormat format = new DecimalFormat("#."+places.toString());
			format.setRoundingMode(RoundingMode.HALF_UP);
			return format.format(value);
		}
	}
	
	static {
	    for (char c : "abcdefghijklmnopqrstuvwxyz1234567890_-".toCharArray()) {
	    	var_name.add(c);
	    }
	}
	
	{
		var.add(new Variable("PI", Math.PI));
		var.add(new Variable("e", Math.E));
	}
	
	public Expression(String s) {
		this.exp = s;
	}
	
	public Expression() {
		
	}

	public Double getDouble(String s) {
		if (s == null)
			return null;
		try {
			return new Double(Double.parseDouble(s));
		} catch (Exception e) {
			return getVar(s);
		}
	}
	
	public Variable getVarObj(String name) {
		for (Variable v : var) {
			if (v.name.equals(name)) {
				return v;
			}
		}
		return null;
	}
	
	/**
	 * Get a variable's value from its name. Case sensitive.
	 * 
	 * @param name
	 * @return
	 */
	public Double getVar(String name) {
		for (Variable v : var) {
			if (v.name.equals(name)) {
				return v.value;
			}
		}
		return null;
	}
	
	/**
	 * Set a variable's value. Case sensitive.
	 * @param name
	 * @param value
	 * @return
	 */
	public boolean setVar(String name, double value) {
		for (char c : name.toCharArray()) {
			if (!var_name.contains(c)) {
				return false;
			}
		}
		
		if (restricted(name)) {
			return false;
		}
		
		Variable v = getVarObj(name);
		if (v == null) {
			var.add(new Variable(name, value));
		} else {
			v.value = value;
		}
		return true;
	}
	
	/**
	 * Delete a variable. Case sensitive.
	 * 
	 * @param name
	 * @return
	 */
	public boolean delVar(String name) {
		if (restricted(name)) {
			return false;
		}
		
		var.remove(getVarObj(name));
		return true;
	}

	public boolean restricted(String name) {
		try {
			Double.parseDouble(name);
			return true;
		} catch(Exception e) {}
		
		if (name.equals("PI") || name.equals("pi") || name.equals("Pi") || name.equals("pI") ||
				name.equals("E") || name.equals("e")) {
			return true;
		}
		
		if (name.length() > 16) {
			return true;
		}
		
		return false;
	}

	public Double evaluate() {
		try {
			return Node.evaluate(new Node(exp, this));
		} catch(Exception e) {
			return null;
		}
	}
	
	public String toString() {
		return new Node(exp, this).toString();
	}
	
	public void replaceVars(Expression vars) {
		this.var.clear();
		this.var.addAll(vars.var);
	}

	public void addVars(Expression vars) {
		this.var.addAll(vars.var);
	}

	public boolean containsVar(String name) {
		return getVarObj(name) != null;
	}
}
