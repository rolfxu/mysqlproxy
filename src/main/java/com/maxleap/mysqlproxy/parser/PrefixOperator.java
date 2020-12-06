package com.maxleap.mysqlproxy.parser;

import com.maxleap.mysqlproxy.parser.expression.Expression;

public class PrefixOperator implements Expression {
	
	private String name;
	
	public PrefixOperator() {
		
	}
	public PrefixOperator(String name) {
		this.name=name;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
