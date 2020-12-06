package com.maxleap.mysqlproxy.parser.expression;

import java.util.List;

public class CaseExpression  implements Expression {

    private Expression switchExpression;
    private List<WhenClause> whenClauses;
    private Expression elseExpression;

//    @Override
//    public void accept(ExpressionVisitor expressionVisitor) {
//        expressionVisitor.visit(this);
//    }

    public Expression getSwitchExpression() {
        return switchExpression;
    }

    public void setSwitchExpression(Expression switchExpression) {
        this.switchExpression = switchExpression;
    }

    /**
     * @return Returns the elseExpression.
     */
    public Expression getElseExpression() {
        return elseExpression;
    }

    /**
     * @param elseExpression The elseExpression to set.
     */
    public void setElseExpression(Expression elseExpression) {
        this.elseExpression = elseExpression;
    }

    /**
     * @return Returns the whenClauses.
     */
    public List<WhenClause> getWhenClauses() {
        return whenClauses;
    }

    /**
     * @param whenClauses The whenClauses to set.
     */
    public void setWhenClauses(List<WhenClause> whenClauses) {
        this.whenClauses = whenClauses;
    }

    @Override
    public String toString() {
        return "CASE " + ((switchExpression != null) ? switchExpression + " " : "")
                + whenClauses+ " "
                + ((elseExpression != null) ? "ELSE " + elseExpression + " " : "") + "END";
    }
}

