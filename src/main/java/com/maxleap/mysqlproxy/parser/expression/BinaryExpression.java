/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package com.maxleap.mysqlproxy.parser.expression;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A basic class for binary expressions, that is expressions having a left member and a right member
 * which are in turn expressions.
 */
@JsonInclude
public  class BinaryExpression   implements Expression {

    private Expression leftExpression;
    private Expression rightExpression;
    
    private Expression operator;
//    private boolean not = false;

    public BinaryExpression() {
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    public void setLeftExpression(Expression expression) {
        leftExpression = expression;
    }

    public void setRightExpression(Expression expression) {
        rightExpression = expression;
    }
    
    

    public Expression getOperator() {
		return operator;
	}

	public void setOperator(Expression operator) {
		this.operator = operator;
	}

	//    public void setNot() {
//        not = true;
//    }
//    
//    public void removeNot() {
//        not = false;
//    }
// 
//    public boolean isNot() {
//        return not;
//    }
    @Override
    public String toString() {
        return //(not ? "NOT " : "") + 
                getLeftExpression() + " " + getOperator() + " " + getRightExpression();
    }


}
