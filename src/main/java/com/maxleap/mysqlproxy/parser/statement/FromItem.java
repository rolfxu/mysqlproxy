package com.maxleap.mysqlproxy.parser.statement;

import com.maxleap.mysqlproxy.parser.expression.Alias;
import com.maxleap.mysqlproxy.parser.expression.Expression;

public interface FromItem extends Expression{


    Alias getAlias();

    void setAlias(Alias alias);

}

