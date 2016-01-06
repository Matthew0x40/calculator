package com.matthew0x40.calculator;

import java.util.ArrayList;

public class Node {
	// Operator
	public Operator op = null;
	// Operator Index
	public int opi = 0;
	// Original equation sequence
	public String s;
	public Node leftNode;
	public Node rightNode;
	public Node[] funcNodeList;
	public Double value;
	public Expression e;

	public Node(String s, Expression e) {
		s = Node.removeBrackets(s.replace(" ", ""));
		s = addZero(s);
		if (!Node.checkBrackets(s)) {
			throw new IllegalArgumentException("Misplaced or missing brackets: " + s);
		}
		
		this.e = e;
		this.s = s;
		value = e.getDouble(s);
		int bracket_match = 0;

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '(')
				bracket_match++;
			else if (s.charAt(i) == ')')
				bracket_match--;
			else {
				if (bracket_match == 0) {
					Operator o = getOperator(s, i);

					if (o != null) {
						if (op == null || op.priority >= o.priority) {
							op = o;
							opi = i;
						}
					}
				}
			}
		}

		if (op != null) {
			if (opi == 0 && op.type == Operator.TYPE_FUNC) {
				if (checkBrackets(s.substring(op.getName().length()))) {
					String argsStr = Node.removeBrackets(s.substring(op.getName().length()));
					String[] funcNodes = Node.splitCommas(argsStr);
					funcNodeList = new Node[funcNodes.length];
					for (int argi = 0; argi < funcNodeList.length; argi++) {
						funcNodeList[argi] = new Node(funcNodes[argi], e);
					}
					return;
				}
				throw new IllegalArgumentException("Missing or misplaced bracket(s): " + s);
			} else if (opi > 0 && op.type == Operator.TYPE_OP) {
				// TYPE_OP should always have 2 operands, no need to check
				// leftNode op rightNode
				leftNode = new Node(s.substring(0, opi), e);
				rightNode = new Node(s.substring(opi + op.getName().length()), e);
			}
		}
	}
	
	// If s startswith + or - then return "0" + s
	public String addZero(String s) {
		if (s.startsWith("+") || s.startsWith("-"))
			return "0" + s;
		return s;
	}

	public static Operator getOperator(String s, int start) {
		Operator[] operators = Operator.values();
		String next = s.substring(start);
		for (int i = 0; i < operators.length; i++)
			if (next.startsWith(operators[i].getName()))
				return operators[i];
		return null;
	}

	// '((1+1))' -> '1+1'
	public static String removeBrackets(String s) {
		String res = s;
		if (s.length() > 2 && res.startsWith("(") && res.endsWith(")")
				&& checkBrackets(s.substring(1, s.length() - 1)))
			res = res.substring(1, res.length() - 1);
		if (res != s)
			return removeBrackets(res);
		return res;
	}
	
	public static String[] splitCommas(String str) {
		ArrayList<String> splitted = new ArrayList<String>();
		int skipCommas = 0;
		boolean skipCommas2 = false;
		String s = "";
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == ',' && skipCommas == 0 && skipCommas2 == false) {
				splitted.add(s);
				s = "";
			} else {
				if (c == '(')
					skipCommas++;
				if (c == ')')
					skipCommas--;
				if (c == '\'' || c == '\"') {
					if (i - 1 >= 0 && str.charAt(i-1) != '\\') {
						skipCommas2 = !skipCommas2;
					}
				}
				s += c;
			}
		}
		
		splitted.add(s);
		return splitted.toArray(new String[splitted.size()]);
	}

	public static boolean checkBrackets(String s) {
		int brackets = 0;
		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) == '(' && brackets >= 0)
				brackets++;
			else if (s.charAt(i) == ')')
				brackets--;
		return brackets == 0;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean hasOperator() {
		return op != null;
	}

	public boolean hasChild() {
		return leftNode != null || rightNode != null || funcNodeList != null;
	}

	public static Double evaluate(Node n) {
		n.value = n.e.getDouble(n.s);
		if (n.hasOperator() && n.hasChild()) {
			if (n.op.type == Operator.TYPE_FUNC) {
				Double[] args = new Double[n.funcNodeList.length];
				for (int argi = 0; argi < n.funcNodeList.length; argi++) {
					args[argi] = evaluate(n.funcNodeList[argi]);
				}
				
				n.setValue(n.op.evaluate(args));
			} else if (n.op.type == Operator.TYPE_OP) {
				n.setValue(n.op.evaluate(evaluate(n.leftNode),
						evaluate(n.rightNode)));
			}
		}
		return n.getValue();
	}

	public String toString() {
		String ln = leftNode == null ? "null" : leftNode.toString();
		String rn = rightNode == null ? "null" : rightNode.toString();

		if (hasChild())
			return "[" + ln + " " + op + " " + rn + "]";
		else {
			value = e.getDouble(s);
			if (value == null) {
				return "[Invalid]";
			}
			return value.toString();
		}
	}
}