package com.maxleap.mysqlproxy.parser.statement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.maxleap.mysqlproxy.parser.expression.Expression;

public class GroupByElements implements Expression{

    private List<Expression> groupByExpressions = new ArrayList<>();

    

    public List<Expression> getGroupByExpressions() {
        return groupByExpressions;
    }

    public void setGroupByExpressions(List<Expression> groupByExpressions) {
        this.groupByExpressions = groupByExpressions;
    }

    public void addGroupByExpression(Expression groupByExpression) {
        groupByExpressions.add(groupByExpression);
    }

     

    

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("GROUP BY ");

        if (groupByExpressions.size() > 0) {
        	for( Iterator<Expression> it = groupByExpressions.iterator();it.hasNext(); ) {
        		Expression e = it.next();
        		 b.append(e);
        		 if(it.hasNext()) {
        			 b.append(",");
        		 }
        	}
           
        }  

        return b.toString();
    }
}

