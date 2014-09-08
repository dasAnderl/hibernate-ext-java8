package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.wrappers.OrCriterionWrapper;

import java.util.List;

/**
 * Created by ga2unte on 8.9.2014.
 */
public interface HasOrCriterion<T> {

    default List<OrCriterionWrapper> getOrCriterions() {
        return Helper.invokeGettersByReturnType(OrCriterionWrapper.class, this);
    }
}
