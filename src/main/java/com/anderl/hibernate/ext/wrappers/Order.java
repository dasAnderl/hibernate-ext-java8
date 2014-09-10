package com.anderl.hibernate.ext.wrappers;


import com.anderl.hibernate.ext.AliasUtils;

/**
 * Created by ga2unte on 12/2/13.
 */
public class Order {

    private boolean asc = true;
    private AliasUtils.Criterion criterion;

    private Order(AliasUtils.Criterion criterion, boolean asc) {
        this.criterion = criterion;
        this.asc = asc;
    }

    public static Order asc(AliasUtils.Criterion criterion) {
        return new Order(criterion, true);
    }

    public static Order desc(AliasUtils.Criterion criterion) {
        return new Order(criterion, false);
    }

    public org.hibernate.criterion.Order get() {
        if (asc) {
            return org.hibernate.criterion.Order.asc(criterion.getCriterionPath());
        }
        return org.hibernate.criterion.Order.desc(criterion.getCriterionPath());
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
