package com.maxleap.mysqlproxy.parser.statement;

import com.maxleap.mysqlproxy.parser.expression.Expression;

public class SqlOperation implements Expression{
	
	public static final SqlOperation UNION = new SqlOperation("UNION");
	
	public static final SqlOperation UNION_ALL = new SqlOperation("UNION");
	
	public static final SqlOperation MINUS = new SqlOperation("MINUS");
	
	
	private String operator;
	
	public SqlOperation(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	
}
