package com.maxleap.mysqlproxy.parser.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.maxleap.mysqlproxy.parser.expression.Alias;
import com.maxleap.mysqlproxy.parser.expression.Expression;
import com.maxleap.mysqlproxy.parser.statement.FromItem;

public class ValuesList   implements FromItem {

    private List<Expression> expressions = new ArrayList<>();

    public ValuesList() {
    }

    public void addExpression(Expression expression) {
    	expressions.add(expression);
    }
    
    
    public ValuesList(Expression ... expressions) {
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
    	
        StringBuilder ans = new StringBuilder( );
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
	public Alias getAlias() {
		return null;
	}

	@Override
	public void setAlias(Alias alias) {
		
	}
 
}
