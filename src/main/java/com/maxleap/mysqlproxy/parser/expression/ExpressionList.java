package com.maxleap.mysqlproxy.parser.expression;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

 

/**
 * A list of expressions, as in SELECT A FROM TAB WHERE B IN (expr1,expr2,expr3)
 */
public class ExpressionList implements Expression {

    private List<Expression> expressions = new ArrayList<>();
    
    private boolean comma=true;
    
    private boolean paren =false;

    public ExpressionList() {
    }

    public void addExpression(Expression expression) {
    	expressions.add(expression);
    }
    
    
    
    public boolean isComma() {
		return comma;
	}

	public void setComma(boolean comma) {
		this.comma = comma;
	}

	public boolean isParen() {
		return paren;
	}

	public void setParen(boolean paren) {
		this.paren = paren;
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<Expression> expressions) {
		this.expressions = expressions;
	}

	public ExpressionList(Expression ... expressions) {
        this.expressions = Arrays.asList(expressions);
    }
    public String toName() {
    	 StringBuilder ans = new StringBuilder();
        for( Iterator<Expression> it =expressions.iterator();it.hasNext(); ) {
        	Expression e = it.next();
        	ans.append(e);
        	if(it.hasNext()) {
        		ans.append(",");
        	}
        }
      return ans.toString();
    }
    @Override
    public String toString() {
    	
        StringBuilder ans = new StringBuilder();
        if(paren) {
        	ans.append("(");
        }
        for( Iterator<Expression> it =expressions.iterator();it.hasNext(); ) {
        	Expression e = it.next();
        	ans.append(e);
        	if(it.hasNext()) {
        		if(comma) {
        			ans.append(",");
        		} else {
        			ans.append(" ");
        		}
        	}
        }
        if(paren) {
        	ans.append(")");
        }
      return ans.toString();
}
 
}

