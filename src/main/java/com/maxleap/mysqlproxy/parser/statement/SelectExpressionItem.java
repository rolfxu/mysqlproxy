package com.maxleap.mysqlproxy.parser.statement;

import com.maxleap.mysqlproxy.parser.expression.Alias;
import com.maxleap.mysqlproxy.parser.expression.Expression;

public class SelectExpressionItem   implements SelectItem,Expression {

    private Expression expression;
    private Alias alias;

    public SelectExpressionItem() {
    }

    public SelectExpressionItem(Expression expression) {
        this.expression = expression;
    }

    public Alias getAlias() {
        return alias;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setAlias(Alias alias) {
        this.alias = alias;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
 

    @Override
    public String toString() {
        return expression + ((alias != null) ? alias.toString() : "");
    }
}

