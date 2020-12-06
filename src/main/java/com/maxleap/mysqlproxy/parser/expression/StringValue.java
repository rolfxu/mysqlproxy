package com.maxleap.mysqlproxy.parser.expression;

import java.util.Arrays;
import java.util.List;

/**
 * A string as in 'example_string'
 */
public final class StringValue  implements Expression {

    private String value = "";
    private String prefix = null;

    public static final List<String> ALLOWED_PREFIXES = Arrays.asList("N", "U", "E", "R", "B", "RB", "_utf8");

    public StringValue(String escapedValue) {
        // removing "'" at the start and at the end
        if (escapedValue.startsWith("'") && escapedValue.endsWith("'")) {
            value = escapedValue.substring(1, escapedValue.length() - 1);
            return;
        }

        if (escapedValue.length() > 2) {
            for (String p : ALLOWED_PREFIXES) {
                if (escapedValue.length() > p.length() && escapedValue.substring(0, p.length()).equalsIgnoreCase(p) && escapedValue.charAt(p.length()) == '\'') {
                    this.prefix = p;
                    value = escapedValue.substring(p.length() + 1, escapedValue.length() - 1);
                    return;
                }
            }
        }

        value = escapedValue;
    }

    public String getValue() {
        return value;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNotExcapedValue() {
        StringBuilder buffer = new StringBuilder(value);
        int index = 0;
        int deletesNum = 0;
        while ((index = value.indexOf("''", index)) != -1) {
            buffer.deleteCharAt(index - deletesNum);
            index += 2;
            deletesNum++;
        }
        return buffer.toString();
    }

    public void setValue(String string) {
        value = string;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    @Override
    public String toString() {
        return (prefix != null ? prefix : "") + value ;
    }
}

