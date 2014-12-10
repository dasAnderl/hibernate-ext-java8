package com.anderl.hibernate.ext.wrappers;


import com.anderl.hibernate.ext.ColumnControl;
import com.anderl.hibernate.ext.RestrictionsExt;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;

import static com.anderl.hibernate.ext.AliasUtils.Criterion;

/**
 * Created by ga2unte on 12/2/13.
 */
public class Filter<T> implements ColumnControl<T> {

    private final Criterion criterion;
    private final RestrictionsExt restrictionsExt;
    private final String id;
    private final Class<T> type;
    private T value;
    private boolean enabled = true;
    private String labelMsgKey;

    public static <T> Filter<T> get(Criterion criterion, RestrictionsExt restrictionsExt, Class<T> type) {
        return new Filter<>(criterion, restrictionsExt, type);
    }

    public static <T> Filter<T> get(Criterion criterion, RestrictionsExt restrictionsExt, Class<T> type, T value) {
        return new Filter<>(criterion, restrictionsExt, type, value);
    }

    private Filter(Criterion criterion, RestrictionsExt restrictionsExt, String labelMsgKey, Class<T> type, T value) {
        this.criterion = criterion;
        this.restrictionsExt = restrictionsExt;
        this.value = value;
        this.labelMsgKey = labelMsgKey;
        this.type = type;
        id = getId(criterion);
    }

    private Filter(Criterion criterion, RestrictionsExt restrictionsExt, Class<T> type, T value) {
        this(criterion, restrictionsExt, "", type, value);
    }

    private Filter(Criterion criterion, RestrictionsExt restrictionsExt, Class<T> type) {
        this(criterion, restrictionsExt, "", type, null);
    }

    private String getId(Criterion criterion) {
        return criterion.getFieldPath().replace(".", "");
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
        return (T)value;
    }

    @Override
    public void setValue(T value) {
        this.value = (T)value;

        try {
            this.value = type.cast(value);
        }
        catch (Exception e) {
            System.out.println("bhjbdbjbfjbfjwkbefjwebfj");
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean active) {
        this.enabled = active;
    }

    @Override
    public String getLabelMsgKey() {
        return labelMsgKey;
    }

    @Override
    public String getId() {

        return id;
    }

    public Criterion getAliasCriterion() {
        return criterion;
    }

    public Class<T> getType() {
        return type;
    }
}
