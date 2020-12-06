package com.maxleap.mysqlproxy.parser.statement;

import java.util.Iterator;
import java.util.List;

import com.maxleap.mysqlproxy.parser.expression.Expression;

public class PlainSelect implements Expression {
	
	private Distinct distinct = null;
	private List<SelectItem> selectItems;
	private FromItem fromItem;
	private List<Join> joins;
	private Expression where;
	private GroupByElements groupBy;
	private List<OrderByElement> orderByElements;
	private Expression having;
	private Limit limit;
	
	private boolean forUpdate=false;
	public Distinct getDistinct() {
		return distinct;
	}
	public void setDistinct(Distinct distinct) {
		this.distinct = distinct;
	}
	public List<SelectItem> getSelectItems() {
		return selectItems;
	}
	public void setSelectItems(List<SelectItem> selectItems) {
		this.selectItems = selectItems;
	}
	public FromItem getFromItem() {
		return fromItem;
	}
	public void setFromItem(FromItem fromItem) {
		this.fromItem = fromItem;
	}
	public List<Join> getJoins() {
		return joins;
	}
	public void setJoins(List<Join> joins) {
		this.joins = joins;
	}
	public Expression getWhere() {
		return where;
	}
	public void setWhere(Expression where) {
		this.where = where;
	}
	 
	public GroupByElements getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(GroupByElements groupBy) {
		this.groupBy = groupBy;
	}
	public List<OrderByElement> getOrderByElements() {
		return orderByElements;
	}
	public void setOrderByElements(List<OrderByElement> orderByElements) {
		this.orderByElements = orderByElements;
	}
	public Expression getHaving() {
		return having;
	}
	public void setHaving(Expression having) {
		this.having = having;
	}
	public Limit getLimit() {
		return limit;
	}
	public void setLimit(Limit limit) {
		this.limit = limit;
	}
	
	 public boolean isForUpdate() {
		return forUpdate;
	}
	public void setForUpdate(boolean forUpdate) {
		this.forUpdate = forUpdate;
	}
	@Override
	    public String toString() {
	        StringBuilder sql = new StringBuilder();
	         
	        sql.append("SELECT ");

	        if (distinct != null) {
	            sql.append(distinct).append(" ");
	        }
	         if( selectItems!=null ) {
	        	 for( Iterator<SelectItem> it = selectItems.iterator();it.hasNext();) {
	        		 SelectItem item = it.next();
	        		 sql.append(item);	 
	        		 if(it.hasNext()) {
	        			 sql.append(", ");
	        		 }
		         }
	         }
	         

	        

	        if (fromItem != null) {
	            sql.append(" FROM ").append(fromItem);
	            if (joins != null) {
	                Iterator<Join> it = joins.iterator();
	                while (it.hasNext()) {
	                    Join join = it.next();
	                    if (join.isSimple()) {
	                        sql.append(", ").append(join);
	                    } else {
	                        sql.append(" ").append(join);
	                    }
	                }
	            }

	            
	            if (where != null) {
	                sql.append(" WHERE ").append(where);
	            }
	             
	            if (groupBy != null) {
	                sql.append(" ").append(groupBy.toString());
	            }
	            if (having != null) {
	                sql.append(" HAVING ").append(having);
	            }
	             
	           
	             
	            if (isForUpdate()) {
	                sql.append(" FOR UPDATE");
	            }
	            if (limit != null) {
	                sql.append(limit);
	            }
	            
	        } else {
	            //without from
	            if (where != null) {
	                sql.append(" WHERE ").append(where);
	            }
	            if (limit != null) {
	                sql.append(limit);
	            }
	        }
	         
	        return sql.toString();
	    }
}
