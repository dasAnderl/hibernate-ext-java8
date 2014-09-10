package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.helper.Helper;
import com.anderl.hibernate.ext.wrappers.OrderWrapper;

/**
 * Created by ga2unte on 10.9.2014.
 */
public interface HasOrder {
    public OrderWrapper getOrderWrapper();
}
