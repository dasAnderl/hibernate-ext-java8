package com.anderl.hibernate.ext.wrappers;


import com.anderl.hibernate.ext.AliasUtils;
import org.hibernate.criterion.Order;

/**
 * Created by ga2unte on 12/2/13.
 */
public class OrderWrapper {

    private boolean asc = true;
    private AliasUtils.Criterion criterion;

    public OrderWrapper(AliasUtils.Criterion criterion, boolean asc) {
        this.criterion = criterion;
        this.asc = asc;
    }

    public static OrderWrapper asc(AliasUtils.Criterion criterion) {
        return new OrderWrapper(criterion, true);
    }

    public static OrderWrapper desc(AliasUtils.Criterion criterion) {
        return new OrderWrapper(criterion, false);
    }

    public Order get() {
        if (asc) {
            return Order.asc(criterion.getCriterionPath());
        }
        return Order.desc(criterion.getCriterionPath());
    }

    public void set(AliasUtils.Criterion criterion, boolean asc) {
        this.criterion = criterion;
        this.asc = asc;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    public AliasUtils.Criterion getCriterion() {
        return criterion;
    }
}
