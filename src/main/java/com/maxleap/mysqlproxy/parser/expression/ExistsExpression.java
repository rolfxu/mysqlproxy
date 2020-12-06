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

 

public class ExistsExpression  implements Expression {

    private Expression rightExpression;
    private boolean not = false;
    public   ExistsExpression() {
    	
    }
    public   ExistsExpression(Expression expression) {
    	rightExpression = expression;
    }
    
    public Expression getRightExpression() {
        return rightExpression;
    }

    public void setRightExpression(Expression expression) {
        rightExpression = expression;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean b) {
        not = b;
    }

    

    public String getStringExpression() {
        return (not ? "NOT " : "") + "EXISTS";
    }

    @Override
    public String toString() {
        return getStringExpression() + " " + rightExpression.toString();
    }
}
