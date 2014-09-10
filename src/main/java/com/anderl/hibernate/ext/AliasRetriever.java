package com.anderl.hibernate.ext;


import com.anderl.hibernate.ext.wrappers.Filter;
import com.anderl.hibernate.ext.wrappers.OrFilter;
import com.anderl.hibernate.ext.wrappers.Order;
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
public class AliasRetriever {

    public static List<AliasUtils.SubAlias> getDistinctAliases(SearchFilter searchFilter) {

        List<AliasUtils.SubAlias> aliasesNotNull = new ArrayList<AliasUtils.SubAlias>();
        aliasesNotNull = addAliasesForWrappers(searchFilter.getCriterions(), aliasesNotNull);
        aliasesNotNull = addAliasesForOrWrappers(searchFilter.getOrCriterions(), aliasesNotNull);

        Order order = searchFilter.getOrderWrapper();
        if (order != null && order.getCriterion().getAlias() != null) {
            aliasesNotNull.addAll(order.getCriterion().getAlias().getSubAliases());
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

    private static List<AliasUtils.SubAlias> addAliasesForOrWrappers(List<OrFilter> orWrappers, List<AliasUtils.SubAlias> aliases) {

        if (orWrappers == null) return Lists.newArrayList();
        List<List<Filter>> wrappersLists = orWrappers.stream().map(orWrapper -> orWrapper.getHibernateCriterionWrappers()).collect(Collectors.toList());

        List<Filter> wrappers = Lists.newArrayList(Iterables.concat(wrappersLists));
        return addAliasesForWrappers(wrappers, aliases);
    }

    public static List<AliasUtils.SubAlias> addAliasesForWrappers(List<Filter> wrappers, List<AliasUtils.SubAlias> aliases) {

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
