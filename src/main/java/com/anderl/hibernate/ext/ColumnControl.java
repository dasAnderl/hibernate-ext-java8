package com.anderl.hibernate.ext;

/**
 * Created by ga2unte on 1/17/14.
 */
public interface ColumnControl<T> {

    public String getId();

    public T getValue();

    public void setValue(T value);

    public boolean isEnabled();

    public void setEnabled(boolean active);

    public String getSortingProperty();

    public String getLabelMsgKey();
}
