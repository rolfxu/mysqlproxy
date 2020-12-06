package com.maxleap.mysqlproxy.parser;

import com.maxleap.mysqlproxy.parser.expression.Expression;

public class BinaryOperator implements Expression {
	
	private String name;
	
	private int precedence;
	
	public BinaryOperator() {
	}
	public BinaryOperator(String name,int precedence) {
		this.name = name;
		this.precedence = precedence;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrecedence() {
		return precedence;
	}

	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
