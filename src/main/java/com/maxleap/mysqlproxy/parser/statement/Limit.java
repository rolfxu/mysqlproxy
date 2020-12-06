package com.maxleap.mysqlproxy.parser.statement;

import com.maxleap.mysqlproxy.parser.expression.Expression;

public class Limit implements Expression  {

	private Expression offset;
    private Expression rowCount;
    private boolean limitAll;
    private boolean limitNull = false;
    private boolean hasOffset = false;

    public Expression getOffset() {
        return offset;
    }

    public Expression getRowCount() {
        return rowCount;
    }

    public void setOffset(Expression l) {
        offset = l;
    }

    public void setRowCount(Expression l) {
        rowCount = l;
    }

    public boolean isLimitAll() {
        return limitAll;
    }

    public void setLimitAll(boolean b) {
        limitAll = b;
    }

    public boolean isLimitNull() {
        return limitNull;
    }

    public void setLimitNull(boolean b) {
        limitNull = b;
    }

    
    public boolean isHasOffset() {
		return hasOffset;
	}

	public void setHasOffset(boolean hasOffset) {
		this.hasOffset = hasOffset;
	}

	@Override
    public String toString() {
        String retVal = " LIMIT ";
        if (limitNull) {
            retVal += "NULL";
        } else {
            if (limitAll) {
                retVal += "ALL";
            } else {
            	if(hasOffset) {
                    retVal += rowCount;
                    retVal +=" OFFSET ";
                    retVal += offset ;
                   
            	} else {
            		if (null != offset) {
                        retVal += offset;
                    }
                    if (null != rowCount) {
                        retVal += ", "+ rowCount;
                    }
            	}
                
            }
        }

        return retVal;
    }
}

