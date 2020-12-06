package com.maxleap.mysqlproxy.parser.expression;


/**
 * A clause of following syntax: WHEN condition THEN expression. Which is part of a CaseExpression.
 */
public class WhenClause  implements Expression {

    private Expression whenExpression;
    private Expression thenExpression;

     

    public Expression getThenExpression() {
        return thenExpression;
    }

    public void setThenExpression(Expression thenExpression) {
        this.thenExpression = thenExpression;
    }

    /**
     * @return Returns the whenExpression.
     */
    public Expression getWhenExpression() {
        return whenExpression;
    }

    /**
     * @param whenExpression The whenExpression to set.
     */
    public void setWhenExpression(Expression whenExpression) {
        this.whenExpression = whenExpression;
    }

    @Override
    public String toString() {
        return "WHEN " + whenExpression + " THEN " + thenExpression;
    }
}

