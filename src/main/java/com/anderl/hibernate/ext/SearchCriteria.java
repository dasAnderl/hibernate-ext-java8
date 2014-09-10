package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.helper.Helper;
import com.anderl.hibernate.ext.wrappers.OrderWrapper;

/**
 * Created by ga2unte on 8.9.2014.
 */
public interface SearchCriteria<T> extends HasCriterion<T>, HasOrCriterion<T>, HasOrder, HasPagingHelper {

    default Class<T> getType() {
        return Helper.getGenericInterfaceType(this.getClass());
    }
}
