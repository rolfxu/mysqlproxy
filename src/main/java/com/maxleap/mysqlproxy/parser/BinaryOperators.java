package com.maxleap.mysqlproxy.parser;



public class BinaryOperators {
	
	public static final BinaryOperator EQUALS = new BinaryOperator("=",30);

	public static final BinaryOperator EQ2 = new BinaryOperator("<=>",30);
	
	public static final BinaryOperator AND = new BinaryOperator("and",30);
	public static final BinaryOperator OR = new BinaryOperator("or",30);
	public static final BinaryOperator GT = new BinaryOperator(">",30);
	public static final BinaryOperator LT = new BinaryOperator("<",30);
	public static final BinaryOperator LE = new BinaryOperator("<=",30);
	public static final BinaryOperator GE = new BinaryOperator(">=",30);
	public static final BinaryOperator NE = new BinaryOperator("!=",30);
	public static final BinaryOperator NE2 = new BinaryOperator("<>",30);
	public static final BinaryOperator PLUS = new BinaryOperator("+",40);
	public static final BinaryOperator MINUS = new BinaryOperator("-",40);
	public static final BinaryOperator MULTIPLY = new BinaryOperator("*",60);
	public static final BinaryOperator DIVIDE = new BinaryOperator("/",60);
	public static final BinaryOperator MOD = new BinaryOperator("%",60);
	public static final BinaryOperator LIKE = new BinaryOperator("LIKE",60);
	public static final BinaryOperator NOT_LIKE = new BinaryOperator("NOT_LIKE",60);
	public static final BinaryOperator IS_NOT = new BinaryOperator("IS_NOT",60);
	public static final BinaryOperator IN = new BinaryOperator("IN",60);
	public static final BinaryOperator NOT_IN = new BinaryOperator("NOT_IN",60);

	
	public static final PrefixOperator PRE_OP_EXISTS = new PrefixOperator("EXISTS");
	public static final PrefixOperator PRE_OP_NOT = new PrefixOperator("NOT");
	public static final PrefixOperator PRE_OP_PLUS = new PrefixOperator("+");
	public static final PrefixOperator PRE_OP_MINUS = new PrefixOperator("-");
}
