package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.wrappers.CriterionWrapper;

import java.util.List;

/**
 * Created by ga2unte on 8.9.2014.
 */
public interface HasCriterion<T> {

    default List<CriterionWrapper> getCriterions() {
        return Helper.invokeGettersByReturnType(CriterionWrapper.class, this);
    }
}
