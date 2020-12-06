package com.maxleap.mysqlproxy.parser.statement;

import java.util.List;

import com.maxleap.mysqlproxy.parser.expression.Expression;

public class Select implements Expression {
	
	private Expression leftSelect;
	
	private List<SqlOperation> operators;
	
	private List<PlainSelect> rightSelects;

	public Select() {
	}
	public Select(Expression leftSelect,List<SqlOperation> operators,List<PlainSelect> rightSelects) {
		this.leftSelect = leftSelect;
		this.operators = operators;
		this.rightSelects = rightSelects;
	}
	public Expression getLeftSelect() {
		return leftSelect;
	}

	public void setLeftSelect(Expression leftSelect) {
		this.leftSelect = leftSelect;
	}

	public List<SqlOperation> getOperators() {
		return operators;
	}

	public void setOperators(List<SqlOperation> operators) {
		this.operators = operators;
	}

	public List<PlainSelect> getRightSelects() {
		return rightSelects;
	}

	public void setRightSelects(List<PlainSelect> rightSelects) {
		this.rightSelects = rightSelects;
	}

}
