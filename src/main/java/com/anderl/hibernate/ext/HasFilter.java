package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.helper.Helper;
import com.anderl.hibernate.ext.wrappers.Filter;

import java.util.List;

/**
 * Created by ga2unte on 8.9.2014.
 */
public interface HasFilter<T> {

    default List<Filter> getCriterions() {
        return Helper.invokeGettersByReturnType(Filter.class, this);
    }
}
