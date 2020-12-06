package com.maxleap.mysqlproxy.parser.expression;

import com.maxleap.mysqlproxy.parser.statement.FromItem;

public class Parenthesis   implements Expression {

    private Expression expression;

    public Parenthesis() {
    }

    public Parenthesis(Expression expression) {
        setExpression(expression);
    }

    public Expression getExpression() {
        return expression;
    }

    public final void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "(" + expression + ")";
    }
}

