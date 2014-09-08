package com.anderl.hibernate.ext;


import com.anderl.hibernate.ext.wrappers.CriterionWrapper;
import com.anderl.hibernate.ext.wrappers.OrCriterionWrapper;
import com.anderl.hibernate.ext.wrappers.OrderWrapper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ga2unte on 12/2/13.
 * <p/>
 * To get a list of {@link org.hibernate.criterion.Criterion} for e.g. you searchcontroller:
 * <p/>
 * HibernateCriterionRetriever.getAllCriterionsFor(searchcontroller);
 * <p/>
 * To get a list of {@link org.hibernate.criterion.Order} for e.g. you searchcontroller:
 * <p/>
 * HibernateCriterionRetriever.getAllOrdersFor(searchcontroller);
 */
public class HibernateCriterionRetriever {

    public static List<CriterionWrapper> getWrappersRelevantForQuery(Object object) {
        List<CriterionWrapper> criterionWrappers = Helper.invokeGettersByReturnType(CriterionWrapper.class, object);
        return Lists.newArrayList(
                Collections2.filter(criterionWrappers, new Predicate<CriterionWrapper>() {
                    @Override
                    public boolean apply(CriterionWrapper criterionWrapper) {
                        return criterionWrapper.isValid();
                    }
                })
        );
    }

    public static List<OrCriterionWrapper> getOrWrappersRelevantForQuery(Object object) {
        List<OrCriterionWrapper> orCriterionWrappers = Helper.invokeGettersByReturnType(OrCriterionWrapper.class, object);
        return Lists.newArrayList(
                Collections2.filter(orCriterionWrappers, new Predicate<OrCriterionWrapper>() {
                    @Override
                    public boolean apply(OrCriterionWrapper orCriterionWrapper) {
                        return orCriterionWrapper.isValid();
                    }
                })
        );
    }

    public static List<CriterionWrapper> getWrappers(Object object, final boolean visible) {
        List<CriterionWrapper> criterionWrappers = Helper.invokeGettersByReturnType(CriterionWrapper.class, object);
        return Lists.newArrayList(
                Collections2.filter(criterionWrappers, new Predicate<CriterionWrapper>() {
                    @Override
                    public boolean apply(CriterionWrapper criterionWrapper) {
                        return criterionWrapper.isVisible() == visible;
                    }
                })
        );
    }


    public static List<AliasUtils.SubAlias> getDistinctAliases(HasCriteria hasCriteria) {

        List<AliasUtils.SubAlias> aliasesNotNull = new ArrayList<AliasUtils.SubAlias>();
        aliasesNotNull = addAliasesForWrappers(hasCriteria.getCriterions(), aliasesNotNull);
        aliasesNotNull = addAliasesForOrWrappers(hasCriteria.getOrCriterions(), aliasesNotNull);

        OrderWrapper orderWrapper = hasCriteria.getOrderWrapper();
        if (orderWrapper != null && orderWrapper.getCriterion().getAlias() != null) {
            aliasesNotNull.addAll(orderWrapper.getCriterion().getAlias().getSubAliases());
        }

        List<AliasUtils.SubAlias> distinctAliases = Lists.newArrayList();
        for (AliasUtils.SubAlias subAlias : aliasesNotNull) {
            boolean missing = true;
            for (AliasUtils.SubAlias distinct : distinctAliases) {
                if (distinct.getPath().equals(subAlias.getPath())) {
                    missing = false;
                    break;
                }
            }
            if (missing) distinctAliases.add(subAlias);
        }
        return new ArrayList<>(distinctAliases);
    }

    private static List<AliasUtils.SubAlias> addAliasesForOrWrappers(List<OrCriterionWrapper> orWrappers, List<AliasUtils.SubAlias> aliases) {

        if (orWrappers == null) return Lists.newArrayList();
        List<List<CriterionWrapper>> wrappersLists = orWrappers.stream().map(orWrapper -> orWrapper.getHibernateCriterionWrappers()).collect(Collectors.toList());

        List<CriterionWrapper> wrappers = Lists.newArrayList(Iterables.concat(wrappersLists));
        return addAliasesForWrappers(wrappers, aliases);
    }

    public static List<AliasUtils.SubAlias> addAliasesForWrappers(List<CriterionWrapper> wrappers, List<AliasUtils.SubAlias> aliases) {

        if (wrappers == null) return Lists.newArrayList();
        List<AliasUtils.Criterion> criterions = wrappers.stream().map(wrapper -> wrapper.getAliasCriterion()).collect(Collectors.toList());

        for (AliasUtils.Criterion criterion : criterions) {
            if (criterion.getAlias() != null) {
                aliases.addAll(criterion.getAlias().getSubAliases());
            }
        }
        return aliases;
    }
}
