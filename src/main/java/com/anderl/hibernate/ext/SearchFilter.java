package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.helper.Helper;

/**
 * Created by ga2unte on 8.9.2014.
 */
public interface SearchFilter<T> extends HasFilter<T>, HasOrFilter<T>, HasOrder, HasPagingHelper {

    default Class<T> getType() {
        return Helper.getGenericInterfaceType(this.getClass(), 0);
    }
}
