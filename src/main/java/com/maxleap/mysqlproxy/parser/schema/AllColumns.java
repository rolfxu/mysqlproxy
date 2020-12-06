package com.maxleap.mysqlproxy.parser.schema;

import com.maxleap.mysqlproxy.parser.expression.Expression;
import com.maxleap.mysqlproxy.parser.statement.SelectItem;

public class AllColumns   implements SelectItem ,Expression{

    public AllColumns() {
    }

    @Override
    public String toString() {
        return "*";
    }
}