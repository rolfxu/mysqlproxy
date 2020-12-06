package com.maxleap.mysqlproxy.parser.utils;

import java.util.ArrayList;
import java.util.List;


import com.google.common.collect.ImmutableList;
import com.maxleap.mysqlproxy.parser.BinaryOperator;
import com.maxleap.mysqlproxy.parser.expression.BinaryExpression;
import com.maxleap.mysqlproxy.parser.expression.Expression;

 

public class SqlParserUtils {
	
	
	
	public static Expression toTree(List<Expression> list) {
		
		LinkedToken linkedToken = new LinkedToken(list);
		
		for (;;) {
			Token h = linkedToken.highest();
			if(h==null) {
				break;
			}
			Op op = new Op(h.o, ImmutableList.of(h.previous,h.next));
			linkedToken.replace(op, h.previous.previous, h.next.next);
		}
		
		
		Expression e =convert(linkedToken.first);
		return e;
	}
	static Expression convert(Token t) {
		if(t instanceof Op) {
			Op op = (Op) t;
			BinaryExpression be = new BinaryExpression();
			be.setOperator(op.o);
			be.setLeftExpression(convert(op.args.get(0)));
			be.setRightExpression( convert(op.args.get(1)));
			return be;
		} else {
			return t.o;
		}
	}
	
	public static class LinkedToken {
		private Token first;
		private Token last;
		LinkedToken(List<Expression> list){
			Token p = null;
		    for (Expression e : list) {
		      Token token = new Token(e);
		      if (p != null) {
		        p.next = token;
		      } else {
		        first = token;
		      }
		      token.previous = p;
		      token.next = null;
		      p = token;
		    }
		    last = p;
		}
		
		private void replace(Token t, Token previous, Token next) {
		    t.previous = previous;
		    t.next = next;
		    if (previous == null) {
		      first = t;
		    } else {
		      previous.next = t;
		    }
		    if (next == null) {
		      last = t;
		    } else {
		      next.previous = t;
		    }
		  }
		
		private Token highest() {
		    Token highest = null;
		    for (Token t = first; 
		    		t != null;
		    		t = t.next) {
		    	if((t instanceof Op) ) {
		    		continue;
		    	}
		      if (t.o instanceof BinaryOperator) {
		    	  BinaryOperator op =   (BinaryOperator)t.o;
		    	  if( highest==null ) {
		    		  highest = t;
		    	  } else if( op.getPrecedence()> ((BinaryOperator)highest.o).getPrecedence()) {
		    		  highest = t;
		    	  }
		      }
		    }
		    return highest;
		  }

		
	}
	
	
	public static class Op extends Token{

		ImmutableList<Token> args;
		
		public Op(Expression o,ImmutableList<Token> args) {
			super(o);
			this.args = args;
		}
		
	}
	
	
	public static class Token implements Cloneable{
	    Token previous;
	    Token next;
	    public final Expression o;

	    Token( Expression o ) {
	      this.o = o;
	    }

	    @Override public String toString() {
	      return o.toString();
	    }
	    public  Token clone() {
	    	return new Token(o);
	    }

	    protected StringBuilder print(StringBuilder b) {
	      return b.append(o);
	    }

	  }
	
}
