package com.anderl.hibernate.ext.wrappers;


import com.anderl.hibernate.ext.AliasUtils;
import com.anderl.hibernate.ext.ColumnControl;
import com.anderl.hibernate.ext.RestrictionsExt;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by ga2unte on 12/2/13.
 * <p/>
 * An  {@link OrCriterionWrapper} contains multiple  {@link CriterionWrapper}.
 * They will be written to a map and later cocatenated with or.
 * Never use same property for two different {@link CriterionWrapper}s.
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
 * In xhtml you can access the values of the contained {@link CriterionWrapper},
 * by calling #{<controller>.<myHibernateCriterionOrWrapper>.getByProperty("id").value}
 */
public class OrCriterionWrapper implements ColumnControl {

    private static Logger log = LoggerFactory.getLogger(OrCriterionWrapper.class);

    //Defines how the subcriterias are appended to the query:
    // true -> ... AND (subcriteria)
    // false -> ... OR (subcriteria)
    private boolean outerConcatAnd = true;
    private boolean innerAndConcat;
    private CriterionWrapper firstWrapper;
    private String id;
    private boolean visible;
    private Map<String, CriterionWrapper<Object>> wrappersMappedByProperty = new HashMap<String, CriterionWrapper<Object>>();

    private OrCriterionWrapper(boolean outerConcatAnd, boolean innerAndConcat, CriterionWrapper... criterionWrappers) {
        this.outerConcatAnd = outerConcatAnd;
        this.innerAndConcat = innerAndConcat;
        //Attention: varargs order is reversed
        for (CriterionWrapper criterionWrapper : criterionWrappers) {
            firstWrapper = criterionWrapper;
            this.id = "multiCriterion" + firstWrapper.getAliasCriterion().getCriterionPath().replace(".", "");
            String property = criterionWrapper.getAliasCriterion().getCriterionPath();
            if (wrappersMappedByProperty.containsKey(property)) {
                log.error("{} used with same property more than once. use {} instead. You query will not return correct results", this.getClass().getSimpleName(), RestrictionsExt.in);
                this.wrappersMappedByProperty = new HashMap<String, CriterionWrapper<Object>>();
                continue;
            }
            wrappersMappedByProperty.put(property, criterionWrapper);
        }
    }

    public static OrCriterionWrapper andOr(CriterionWrapper... criterionWrappers) {
        return new OrCriterionWrapper(true, false, criterionWrappers);
    }

    public static OrCriterionWrapper orOr(CriterionWrapper... criterionWrappers) {
        return new OrCriterionWrapper(false, false, criterionWrappers);
    }

    public static OrCriterionWrapper andAnd(CriterionWrapper... criterionWrappers) {
        return new OrCriterionWrapper(true, true, criterionWrappers);
    }

    public static OrCriterionWrapper orAnd(CriterionWrapper... criterionWrappers) {
        return new OrCriterionWrapper(false, true, criterionWrappers);
    }

    public AliasUtils.Criterion getCriterionMapper() {
        return firstWrapper.getAliasCriterion();
    }

    public org.hibernate.criterion.Criterion getCriterion() {

        Predicate<CriterionWrapper> isValid = new Predicate<CriterionWrapper>() {
            @Override
            public boolean apply(CriterionWrapper hibernateCriterionWrapper) {
                return hibernateCriterionWrapper.isValid();
            }
        };
        Collection<CriterionWrapper<Object>> validCriterionWrappers = Collections2.filter(getWrappersMappedByProperty().values(), isValid);

        Function<CriterionWrapper, org.hibernate.criterion.Criterion> getCriterions = new Function<CriterionWrapper, org.hibernate.criterion.Criterion>() {
            @Override
            public org.hibernate.criterion.Criterion apply(CriterionWrapper hibernateCriterionOrWrapper) {
                return hibernateCriterionOrWrapper.getCriterion();
            }
        };
        List<CriterionWrapper<Object>> validCriterionWrappersList = Lists.newArrayList(validCriterionWrappers);
        List<org.hibernate.criterion.Criterion> validCriterions = Lists.transform(validCriterionWrappersList, getCriterions);

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

        for (CriterionWrapper criterionWrapper : wrappersMappedByProperty.values()) {
            criterionWrapper.setValue(value);
        }
    }

    public boolean isValid() {
        return getCriterion() != null;
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
     * Return property of the first {@link CriterionWrapper}
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

    public Map<String, CriterionWrapper<Object>> getWrappersMappedByProperty() {
        return wrappersMappedByProperty;
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
        return firstWrapper.getLabelMsgKey();
    }

    public List<CriterionWrapper> getHibernateCriterionWrappers() {
        return CollectionUtils.isEmpty(getWrappersMappedByProperty().values()) ? new ArrayList<CriterionWrapper>()
                : new ArrayList<CriterionWrapper>(getWrappersMappedByProperty().values());
    }
}
