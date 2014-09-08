package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.wrappers.OrderWrapper;

/**
 * Created by ga2unte on 8.9.2014.
 */
public interface HasCriteria<T> extends HasCriterion<T>, HasOrCriterion<T> {

    default Class<T> getType() {
        return Helper.getGenericInterfaceType(this.getClass());
    }

    public OrderWrapper getOrderWrapper();
}
