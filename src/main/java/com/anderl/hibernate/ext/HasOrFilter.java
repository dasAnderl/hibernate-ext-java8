package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.helper.Helper;
import com.anderl.hibernate.ext.wrappers.OrFilter;

import java.util.List;

/**
 * Created by ga2unte on 8.9.2014.
 */
public interface HasOrFilter<T> {

    default List<OrFilter> getOrCriterions() {
        return Helper.invokeGettersByReturnType(OrFilter.class, this);
    }
}
