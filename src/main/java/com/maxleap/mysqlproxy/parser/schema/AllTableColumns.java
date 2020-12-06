/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package com.maxleap.mysqlproxy.parser.schema;

import com.maxleap.mysqlproxy.parser.expression.Expression;
import com.maxleap.mysqlproxy.parser.expression.ExpressionList;
import com.maxleap.mysqlproxy.parser.statement.SelectItem;

public class AllTableColumns   implements SelectItem,Expression {

    private Table table;
    private boolean isAll = true;

    public AllTableColumns() {
    }

    public AllTableColumns(ExpressionList expressionList) {
    	if(expressionList!=null) {
    		this.table =new Table();
    		for( Expression expresson : expressionList.getExpressions() )
    	    	this.table.addPartItem(expresson);
    	}
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }


    @Override
    public String toString() {
        return table + ".*";
    }
}
