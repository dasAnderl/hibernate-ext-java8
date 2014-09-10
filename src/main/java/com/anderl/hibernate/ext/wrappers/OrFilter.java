package com.anderl.hibernate.ext.wrappers;


import com.anderl.hibernate.ext.AliasUtils;
import com.anderl.hibernate.ext.ColumnControl;
import com.anderl.hibernate.ext.RestrictionsExt;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ga2unte on 12/2/13.
 * <p/>
 * An  {@link OrFilter} contains multiple  {@link Filter}.
 * They will be written to a map and later cocatenated with or.
 * Never use same property for two different {@link Filter}s.
 * To do this use {@link com.anderl.hibernate.ext.RestrictionsExt}in.
 * <p/>
 * Example: You want to have a Criterion which selects entity which id "is not null OR name is null"
 * You would use:
 * HibernateCriterionOrWrapper orWrapper = new HibernateCriterionOrWrapper(
 * new HibernateCriterionWrapper("id", null, HibernateCriterionEnum.isNull),
 * new HibernateCriterionWrapper("name", null, HibernateCriterionEnum.isNotNull)
 * );
 * orWrapper.get();
 * would give a List with one hibernate {@link org.hibernate.criterion.Restrictions}:
 * Restrictions.and(Restrictions.or(Restrictions.isNotNull("id"), Restrictions.isNull("name")))
 * <p/>
 * In xhtml you can access the values of the contained {@link Filter},
 * by calling #{<controller>.<myHibernateCriterionOrWrapper>.getByProperty("id").value}
 */
public class OrFilter implements ColumnControl {

    private static Logger log = LoggerFactory.getLogger(OrFilter.class);

    //Defines how the subcriterias are appended to the query:
    // true -> ... AND (subcriteria)
    // false -> ... OR (subcriteria)
    private boolean outerConcatAnd = true;
    private boolean innerAndConcat;
    private Filter firstWrapper;
    private String id;
    private boolean visible;
    private Map<String, Filter<Object>> wrappersMappedByProperty = new HashMap<>();

    private OrFilter(boolean outerConcatAnd, boolean innerAndConcat, Filter... filters) {
        this.outerConcatAnd = outerConcatAnd;
        this.innerAndConcat = innerAndConcat;
        //Attention: varargs order is reversed
        for (Filter filter : filters) {
            firstWrapper = filter;
            this.id = "multiCriterion" + firstWrapper.getAliasCriterion().getCriterionPath().replace(".", "");
            String property = filter.getAliasCriterion().getCriterionPath();
            if (wrappersMappedByProperty.containsKey(property)) {
                log.error("{} used with same property more than once. use {} instead. You query will not return correct results", this.getClass().getSimpleName(), RestrictionsExt.in);
                this.wrappersMappedByProperty = new HashMap<>();
                continue;
            }
            wrappersMappedByProperty.put(property, filter);
        }
    }

    public static OrFilter andOr(Filter... filters) {
        return new OrFilter(true, false, filters);
    }

    public static OrFilter orOr(Filter... filters) {
        return new OrFilter(false, false, filters);
    }

    public static OrFilter andAnd(Filter... filters) {
        return new OrFilter(true, true, filters);
    }

    public static OrFilter orAnd(Filter... filters) {
        return new OrFilter(false, true, filters);
    }

    public AliasUtils.Criterion getCriterionMapper() {
        return firstWrapper.getAliasCriterion();
    }

    public org.hibernate.criterion.Criterion getCriterion() {

        Collection<Filter<Object>> validFilters = getWrappersMappedByProperty().values().stream().filter(wrapper -> wrapper.isValid()).collect(Collectors.toList());

        List<org.hibernate.criterion.Criterion> validCriterions = validFilters.stream().map(wrapper ->wrapper.getCriterion()).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(validCriterions)) return null;
        org.hibernate.criterion.Criterion[] predicates = validCriterions.toArray(new org.hibernate.criterion.Criterion[validCriterions.size()]);
        org.hibernate.criterion.Criterion junction;
        if (outerConcatAnd && innerAndConcat) {
            junction = Restrictions.and(Restrictions.and(predicates));
        } else if (outerConcatAnd && !innerAndConcat) {
            junction = Restrictions.and(Restrictions.or(predicates));
        } else if (!outerConcatAnd && innerAndConcat) {
            junction = Restrictions.or(Restrictions.and(predicates));
        } else {
            junction = Restrictions.or(Restrictions.or(predicates));
        }
        return junction;
    }

    private void setValueForAllCriterions(Object value) {
        wrappersMappedByProperty.values().stream().forEach( wrapper -> wrapper.setValue(value));
    }

    /**
     * Use this method only if you want to use same value for all criterions in this orWrapper.
     * Otherwise use getByProperty to access value of specific criterion.
     *
     * @param value
     */
    @Override
    public void setValue(Object value) {
        setValueForAllCriterions(value);
    }

    /**
     * Return property of the first {@link Filter}
     *
     * @return
     */
    @Override
    public String getSortingProperty() {
        return firstWrapper.getAliasCriterion().getCriterionPath();
    }

    /**
     * Use this method only if you want to use same value for all criterions in this orWrapper.
     * Otherwise use getByProperty to access value of specific criterion.
     */
    @Override
    public Object getValue() {
        return getWrappersMappedByProperty().values().isEmpty() ? null : getWrappersMappedByProperty().values().iterator().next().getValue();
    }

    @Override
    public String getId() {
        return id;
    }

    public Map<String, Filter<Object>> getWrappersMappedByProperty() {
        return wrappersMappedByProperty;
    }

    @Override
    public boolean isEnabled() {
        return visible;
    }

    @Override
    public void setEnabled(boolean active) {
        this.visible = active;
    }

    @Override
    public String getLabelMsgKey() {
        return firstWrapper.getLabelMsgKey();
    }

    public List<Filter> getHibernateCriterionWrappers() {
        return CollectionUtils.isEmpty(getWrappersMappedByProperty().values()) ? new ArrayList<>()
                : new ArrayList<>(getWrappersMappedByProperty().values());
    }
}
