package com.maxleap.mysqlproxy.parser.expression;

import java.math.BigInteger;

/**
 * Every number without a point or an exponential format is a LongValue.
 */
public class LongValue   implements Expression {

    private String stringValue;

    public LongValue(final String value) {
        String val = value;
        if (val.charAt(0) == '+') {
            val = val.substring(1);
        }
        this.stringValue = val;
    }

    public LongValue(long value) {
        stringValue = String.valueOf(value);
    }

    

    public long getValue() {
        return Long.parseLong(stringValue);
    }

    public BigInteger getBigIntegerValue() {
        return new BigInteger(stringValue);
    }

    public void setValue(long d) {
        stringValue = String.valueOf(d);
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String string) {
        stringValue = string;
    }

    @Override
    public String toString() {
        return getStringValue();
    }
}

