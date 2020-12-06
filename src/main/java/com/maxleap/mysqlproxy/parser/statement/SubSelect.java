package com.maxleap.mysqlproxy.parser.statement;

import com.maxleap.mysqlproxy.parser.expression.Alias;
import com.maxleap.mysqlproxy.parser.expression.Expression;

public class SubSelect implements FromItem, Expression{

	private Expression expression;
	
	private Alias alias;
	
	@Override
	public Alias getAlias() {
		return alias;
	}

	@Override
	public void setAlias(Alias alias) {
		this.alias = alias;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append( expression.toString() );
		if(alias !=null) {
			str.append(" ");
			if(alias.isUseAs())
			str.append(" AS ");
			str.append(alias.getName());
		}
		return str.toString() ;
	}

}
