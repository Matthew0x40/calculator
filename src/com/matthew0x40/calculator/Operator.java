package com.matthew0x40.calculator;

public enum Operator {

	ADD("+", 1, 100, 2) {

		@Override
		protected double resolve(Double... d) {
			return d[0] + d[1];
		}
	},
	SUBTRACT("-", 1, 100, 2) {

		@Override
		protected double resolve(Double... d) {
			return d[0] - d[1];
		}
	},
	MULTIPLY("*", 1, 200, 2) {

		@Override
		protected double resolve(Double... d) {
			return d[0] * d[1];
		}
	},
	DIVIDE("/", 1, 200, 2) {

		@Override
		protected double resolve(Double... d) {
			return d[0] / d[1];
		}
	},
	MOD("%", 1, 200, 2) {

		@Override
		protected double resolve(Double... d) {
			return d[0] % d[1];
		}
	},
	POW("^", 1, 200, 2) {
		@Override
		public double resolve(Double... d) {
			return Math.pow(d[0], d[1]);
		}

	},
	POW2("pow", 2, 200, 2) {
		@Override
		public double resolve(Double... d) {
			return Math.pow(d[0], d[1]);
		}

	},
	COS("cos", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.cos(d[0]);
		}

	},
	SIN("sin", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.sin(d[0]);
		}

	},
	TAN("tan", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.tan(d[0]);
		}

	},
	ACOS("acos", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.acos(d[0]);
		}

	},
	ASIN("asin", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.asin(d[0]);
		}

	},
	ATAN("atan", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.atan(d[0]);
		}

	},
	SQRT("sqrt", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.sqrt(d[0]);
		}

	},
	SQR("sqr", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return d[0] * d[0];
		}

	},
	LOG10("log10", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.log10(d[0]);
		}

	},
	LOG("log", 2, 300, 2) {
		@Override
		public double resolve(Double... d) {
			return Math.log(d[0]) / Math.log(d[1]);
		}

	},
	LN("ln", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.log(d[0]);
		}

	},
	FLOOR("floor", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.floor(d[0]);
		}

	},
	CEIL("ceil", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.ceil(d[0]);
		}

	},
	ABS("abs", 2, 300, 1) {
		@Override
		public double resolve(Double... d) {
			return Math.abs(d[0]);
		}

	},

	;

	public static final int OPERAND_SINGLE = 1;
	public static final int OPERAND_DOUBLE = 2;
	public static final int TYPE_OP = 1;
	public static final int TYPE_FUNC = 2;
	public final String op;
	public final int type;
	public final int priority;
	public final int operands;

	Operator(String op, int type, int priority, int operands) {
		this.op = op;
		this.type = type;
		this.priority = priority;
		this.operands = operands;
	}

	public String getName() {
		return op;
	}

	public int getPriority() {
		return priority;
	}

	public double evaluate(Double... d) {
		if (d.length < operands) {
			throw new IllegalArgumentException(this.toString() + ": Not enough operands, required: " + operands);
		} else if (d.length > operands) {
			throw new IllegalArgumentException(this.toString() + ": Too many operands, required: " + operands);
		}

		for (int i = 0; i < operands; i++) {
			if (d[i] == null) {
				throw new IllegalArgumentException(this.toString() + ": Null operand, Idx: " + i);
			}
		}

		return this.resolve(d);
	}

	protected abstract double resolve(Double... d);
	
	@Override
	public String toString() {
		return "Operator [name="+op+", priority="+priority+", operands"+operands+"]";
	}
}
