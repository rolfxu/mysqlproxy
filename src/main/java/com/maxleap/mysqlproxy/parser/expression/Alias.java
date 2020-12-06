package com.maxleap.mysqlproxy.parser.expression;


import java.util.List;
import java.util.Objects;

public class Alias {

    private String name;
    private boolean useAs = true;
    private List<AliasColumn> aliasColumns;
    public Alias() {
    }
    public Alias(String name) {
        this.name = name;
    }

    public Alias(String name, boolean useAs) {
        this.name = name;
        this.useAs = useAs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUseAs() {
        return useAs;
    }

    public void setUseAs(boolean useAs) {
        this.useAs = useAs;
    }

    public List<AliasColumn> getAliasColumns() {
        return aliasColumns;
    }

    public void setAliasColumns(List<AliasColumn> aliasColumns) {
        this.aliasColumns = aliasColumns;
    }

    @Override
    public String toString() {
        String alias = (useAs ? " AS " : " ") + name;

        if (aliasColumns != null && !aliasColumns.isEmpty()) {
            String ac = "";
            for (AliasColumn col : aliasColumns) {
                if (ac.length() > 0) {
                    ac += ", ";
                }
                ac += col.name;
            }
            alias += "(" + ac + ")";
        }

        return alias;
    }

    public static class AliasColumn {

        public final String name;

        public AliasColumn(String name) {
            Objects.requireNonNull(name);
            this.name = name;
        }
       
    }
}

