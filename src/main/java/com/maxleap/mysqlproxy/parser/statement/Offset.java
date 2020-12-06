package com.maxleap.mysqlproxy.parser.statement;

import com.maxleap.mysqlproxy.parser.expression.Expression;

public class Offset {

    private long offset;
    private Expression offsetJdbcParameter = null;
    private String offsetParam = null;

    public long getOffset() {
        return offset;
    }

    public String getOffsetParam() {
        return offsetParam;
    }

    public void setOffset(long l) {
        offset = l;
    }

    public void setOffsetParam(String s) {
        offsetParam = s;
    }

    public Expression getOffsetJdbcParameter() {
        return offsetJdbcParameter;
    }


    @Override
    public String toString() {
        return " OFFSET " + (offsetJdbcParameter!=null ? offsetJdbcParameter.toString() : offset) + (offsetParam != null ? " " + offsetParam : "");
    }
}

