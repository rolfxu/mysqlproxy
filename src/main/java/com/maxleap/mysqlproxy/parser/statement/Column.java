package com.maxleap.mysqlproxy.parser.statement;

import java.util.List;

import com.maxleap.mysqlproxy.parser.expression.Expression;
import com.maxleap.mysqlproxy.parser.schema.Table;

public class Column     implements Expression {

    private Table table;
    private String columnName;

    public Column() {
    }

    public Column(Table table, String columnName) {
        setTable(table);
        setColumnName(columnName);
    }

    

    public Column(String columnName) {
        this(null, columnName);
    }

    /**
     * Retrieve the information regarding the {@code Table} this {@code Column} does
     * belong to, if any can be inferred.
     * <p>
     * The inference is based only on local information, and not on the whole SQL command.
     * For example, consider the following query:
     * <blockquote><pre>
     *  SELECT x FROM Foo
     * </pre></blockquote>
     * Given the {@code Column} called {@code x}, this method would return {@code null},
     * and not the info about the table {@code Foo}.
     * On the other hand, consider:
     * <blockquote><pre>
     *  SELECT t.x FROM Foo t
     * </pre></blockquote>
     * Here, we will get a {@code Table} object for a table called {@code t}.
     * But because the inference is local, such object will not know that {@code t} is
     * just an alias for {@code Foo}.
     *
     * @return an instance of {@link net.sf.jsqlparser.schema.Table} representing the
     *          table this column does belong to, if it can be inferred. Can be {@code null}.
     */
    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String string) {
        columnName = string;
    }

    

    public String getName(boolean aliases) {
        StringBuilder fqn = new StringBuilder();

        if (table != null) {
            if (table.getAlias() != null && aliases) {
                fqn.append(table.getAlias().getName());
            } 
        }
        if (fqn.length() > 0) {
            fqn.append('.');
        }
        if (columnName != null) {
            fqn.append(columnName);
        }
        return fqn.toString();
    }

 

    @Override
    public String toString() {
        return getName(true);
    }
}

