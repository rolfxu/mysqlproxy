package com.maxleap.mysqlproxy.parser.statement;

import java.util.List;

import com.maxleap.mysqlproxy.parser.expression.Expression;

public class Join implements Expression  {

    private boolean outer = false;
    private boolean right = false;
    private boolean left = false;
    private boolean natural = false;
    private boolean full = false;
    private boolean inner = false;
    private boolean simple = false;
    private boolean cross = false;
    private boolean semi = false;
    private boolean straight = false;
    private boolean apply = false;
    private Expression rightItem;
    private Expression onExpression;
    private List<Column> usingColumns;

    public boolean isSimple() {
        return simple;
    }

    public void setSimple(boolean b) {
        simple = b;
    }

    public boolean isInner() {
        return inner;
    }

    public void setInner(boolean b) {
        inner = b;
    }

    public boolean isStraight() {
        return straight;
    }

    public void setStraight(boolean b) {
        straight = b;
    }

    /**
     * Whether is a "OUTER" join
     *
     * @return true if is a "OUTER" join
     */
    public boolean isOuter() {
        return outer;
    }

    public void setOuter(boolean b) {
        outer = b;
    }

    public boolean isApply() {
        return apply;
    }

    public void setApply(boolean apply) {
        this.apply = apply;
    }

    /**
     * Whether is a "SEMI" join
     *
     * @return true if is a "SEMI" join
     */
    public boolean isSemi() {
        return semi;
    }

    public void setSemi(boolean b) {
        semi = b;
    }

    /**
     * Whether is a "LEFT" join
     *
     * @return true if is a "LEFT" join
     */
    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean b) {
        left = b;
    }

    /**
     * Whether is a "RIGHT" join
     *
     * @return true if is a "RIGHT" join
     */
    public boolean isRight() {
        return right;
    }

    public void setRight(boolean b) {
        right = b;
    }

    /**
     * Whether is a "NATURAL" join
     *
     * @return true if is a "NATURAL" join
     */
    public boolean isNatural() {
        return natural;
    }

    public void setNatural(boolean b) {
        natural = b;
    }

    /**
     * Whether is a "FULL" join
     *
     * @return true if is a "FULL" join
     */
    public boolean isFull() {
        return full;
    }

    public void setFull(boolean b) {
        full = b;
    }

    public boolean isCross() {
        return cross;
    }

    public void setCross(boolean cross) {
        this.cross = cross;
    }

    /**
     * Returns the right item of the join
     */
    public Expression getRightItem() {
        return rightItem;
    }

    public void setRightItem(Expression item) {
        rightItem = item;
    }

    /**
     * Returns the "ON" expression (if any)
     */
    public Expression getOnExpression() {
        return onExpression;
    }

    public void setOnExpression(Expression expression) {
        onExpression = expression;
    }

    /**
     * Returns the "USING" list of {@link net.sf.jsqlparser.schema.Column}s (if any)
     */
    public List<Column> getUsingColumns() {
        return usingColumns;
    }

    public void setUsingColumns(List<Column> list) {
        usingColumns = list;
    }


    
    @Override
    public String toString() {
        if (isSimple() && isOuter()) {
            return "OUTER " + rightItem;
        } else if (isSimple()) {
            return "" + rightItem;
        } else {
            String type = "";

            if (isRight()) {
                type += "RIGHT ";
            } else if (isNatural()) {
                type += "NATURAL ";
            } else if (isFull()) {
                type += "FULL ";
            } else if (isLeft()) {
                type += "LEFT ";
            } else if (isCross()) {
                type += "CROSS ";
            }

            if (isOuter()) {
                type += "OUTER ";
            } else if (isInner()) {
                type += "INNER ";
            } else if (isSemi()) {
                type += "SEMI ";
            }

            if (isStraight()) {
                type = "STRAIGHT_JOIN ";
            } else if (isApply()) {
                type += "APPLY ";
            } else {
                type += "JOIN ";
            }

            return type + rightItem 
                    + ((onExpression != null) ? " ON " + onExpression + "" : "")
                     ;
        }

    }
    public static void main(String[] args) {
    	switch (args[0]) {
    	case "df":
		case "":
			
			break;

		default:
			break;
		}
	}
}
