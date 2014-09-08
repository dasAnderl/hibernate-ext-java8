package com.anderl.hibernate.ext.wrappers;


import com.anderl.hibernate.ext.AliasUtils;
import com.anderl.hibernate.ext.ColumnControl;
import com.anderl.hibernate.ext.RestrictionsExt;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Created by ga2unte on 12/2/13.
 */
public class CriterionWrapper<T> implements ColumnControl<T> {

    private T value = null;
    private RestrictionsExt restrictionsExt;
    private boolean visible;
    private String labelMsgKey;
    private String id;
    private AliasUtils.Criterion criterion;

    private String getId(AliasUtils.Criterion criterion) {
        return criterion.getFieldPath().replace(".", "");
    }

    public CriterionWrapper(AliasUtils.Criterion criterion, RestrictionsExt restrictionsExt, boolean visible, String labelMsgKey) {
        this.criterion = criterion;
        this.restrictionsExt = restrictionsExt;
        this.visible = visible;
        this.labelMsgKey = labelMsgKey;
        id = getId(criterion);
    }

    public CriterionWrapper(AliasUtils.Criterion criterion, RestrictionsExt restrictionsExt, T value, boolean visible, String labelMsgKey) {
        this(criterion, restrictionsExt, visible, labelMsgKey);
        this.value = value;
    }

    public boolean isValid() {

        if (StringUtils.isEmpty(criterion.getCriterionPath())) return false;
        if (restrictionsExt.isNullValueAllowed() && value == null) return true;
        if (StringUtils.isEmpty(value)) return false;
        //used for multicriterias e.g. aopId and Id -> exclude invalid criteria because an aopid is not valid as type for id
        if (isIdProperty()) {
            try {
                value = (T) new Long(value.toString());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        if (restrictionsExt.isMultiValue()) {
            if (value instanceof Collection) {
                return !CollectionUtils.isEmpty((Collection) value);
            }
            if (value.getClass().isArray()) {
                return ((Object[]) value).length > 0;
            }
        }
        return true;
    }

    private boolean isIdProperty() {
        if (criterion.getCriterionPath().equals("id")
                || criterion.getCriterionPath().endsWith(".id")
                || criterion.getCriterionPath().equals("changedEntityId")
                || criterion.getCriterionPath().equals("clientId")
                || criterion.getCriterionPath().equals("assessmentId")
                ) {
            return true;
        }
        return false;
    }

    public org.hibernate.criterion.Criterion getCriterion() {
        //TODO ga2unte: remove this check, just provide AbstractStaticEntity.type here
        if (value != null && value.getClass().getSuperclass() != null && value.getClass().getSuperclass().getSimpleName().contains("AbstractStaticEntity")) {
            final Field typeField = ReflectionUtils.findField(value.getClass(), "type");
            return restrictionsExt.get(criterion.getCriterionPath(), ReflectionUtils.getField(typeField, "type"));
        }
        return restrictionsExt.get(criterion.getCriterionPath(), getValue());
    }

    @Override
    public String getSortingProperty() {
        return criterion.getCriterionPath();
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean active) {
        this.visible = active;
    }

    @Override
    public String getLabelMsgKey() {
        return labelMsgKey;
    }

    @Override
    public String getId() {

        return id;
    }

    public AliasUtils.Criterion getAliasCriterion() {
        return criterion;
    }
}
