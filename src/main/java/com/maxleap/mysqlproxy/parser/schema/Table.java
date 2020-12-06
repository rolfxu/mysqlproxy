package com.maxleap.mysqlproxy.parser.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.maxleap.mysqlproxy.parser.expression.Alias;
import com.maxleap.mysqlproxy.parser.expression.Expression;
import com.maxleap.mysqlproxy.parser.statement.FromItem;

public class Table implements FromItem {
	
	private List<Expression> partItems = new ArrayList<>();

	private Alias alias;

	public Table() {
	}
	public void addPartItem(Expression part) {
		partItems.add(part);
	}
	public void addPartItems(List<Expression> partItems) {
		if(partItems!=null)
		for(Expression e:partItems) {
			this.partItems.add(e);
		}
	}
	 
    public String getFullyQualifiedName() {
        StringBuilder fqn = new StringBuilder();

        for (Iterator<Expression> it = partItems.iterator();it.hasNext();) {
        	Expression part = it.next();
            
            fqn.append(part);
            if (it.hasNext()) {
                fqn.append(".");
            }
        }
        return fqn.toString();
    }
	public Alias getAlias() {
		return alias;
	}

	public void setAlias(Alias alias) {
		this.alias = alias;
	}
    @Override
    public String toString() {
        return getFullyQualifiedName()
                + ((alias != null) ? alias.toString() : "")
              ;
    }
}
