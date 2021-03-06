package com.maxleap.mysqlproxy.parser.expression;

import java.util.Arrays;
import java.util.List;

/**
 * A function as MAX,COUNT...
 */
public class Function   implements Expression {

    private List<String> nameparts;
    private ExpressionList parameters;
    private boolean allColumns = false;
    private boolean distinct = false;
    private boolean isEscaped = false;
    private Expression attribute;
    private String attributeName;
    private boolean ignoreNulls = false;

    

    public String getName() {
        return nameparts == null ? null : String.join(".", nameparts);
    }
    
    public List<String> getMultipartName() {
        return nameparts;
    }

    public void setName(String... string) {
        nameparts = Arrays.asList(string);
    }
    
    public void setName(List<String> string) {
        nameparts = string;
    }

    public boolean isAllColumns() {
        return allColumns;
    }

    public void setAllColumns(boolean b) {
        allColumns = b;
    }

    public boolean isIgnoreNulls() {
        return ignoreNulls;
    }

    /**
     * This is at the moment only necessary for AnalyticExpression initialization and not for normal
     * functions. Therefore there is no deparsing for it for normal functions.
     *
     * @param ignoreNulls
     */
    public void setIgnoreNulls(boolean ignoreNulls) {
        this.ignoreNulls = ignoreNulls;
    }

    /**
     * true if the function is "distinct"
     *
     * @return true if the function is "distinct"
     */
    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean b) {
        distinct = b;
    }

    /**
     * The list of parameters of the function (if any, else null) If the parameter is "*",
     * allColumns is set to true
     *
     * @return the list of parameters of the function (if any, else null)
     */
    public ExpressionList getParameters() {
        return parameters;
    }

    public void setParameters(ExpressionList list) {
        parameters = list;
    }

   

    /**
     * Return true if it's in the form "{fn function_body() }"
     *
     * @return true if it's java-escaped
     */
    public boolean isEscaped() {
        return isEscaped;
    }

    public void setEscaped(boolean isEscaped) {
        this.isEscaped = isEscaped;
    }

    public Expression getAttribute() {
        return attribute;
    }

    public void setAttribute(Expression attribute) {
        this.attribute = attribute;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    

    @Override
    public String toString() {
        String params;

        if (parameters != null ) {
            params = parameters.toString();
            if (isDistinct()) {
                params = params.replaceFirst("\\(", "(DISTINCT ");
            } else if (isAllColumns()) {
                params = params.replaceFirst("\\(", "(ALL ");
            }
        } else if (isAllColumns()) {
            params = "(*)";
        } else {
            params = "()";
        }

        String ans = getName() + "(" + params + ")";

        if (attribute != null) {
            ans += "." + attribute.toString();
        } else if (attributeName != null) {
            ans += "." + attributeName;
        }


        if (isEscaped) {
            ans = "{fn " + ans + "}";
        }

        return ans;
    }
}

