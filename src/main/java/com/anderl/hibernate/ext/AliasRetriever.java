package com.anderl.hibernate.ext;


import com.anderl.hibernate.ext.wrappers.Filter;
import com.anderl.hibernate.ext.wrappers.OrFilter;
import com.anderl.hibernate.ext.wrappers.Order;

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

        Order order = searchFilter.getOrder();
        if (order != null && order.getCriterion().getAlias() != null) {
            aliasesNotNull.addAll(order.getCriterion().getAlias().getSubAliases());
        }

        List<AliasUtils.SubAlias> distinctAliases = new ArrayList<>();
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

        if (orWrappers == null) return new ArrayList<>();
        List<List<Filter>> wrappersLists = orWrappers.stream().map(orWrapper -> orWrapper.getHibernateCriterionWrappers()).collect(Collectors.toList());

        final List<Filter> filters = wrappersLists.stream().flatMap(innerList -> innerList.stream()).collect(Collectors.toList());

        return addAliasesForWrappers(filters, aliases);
    }

    public static List<AliasUtils.SubAlias> addAliasesForWrappers(List<Filter> wrappers, List<AliasUtils.SubAlias> aliases) {

        if (wrappers == null) return new ArrayList<>();
        List<AliasUtils.Criterion> criterions = wrappers.stream().map(wrapper -> wrapper.getAliasCriterion()).collect(Collectors.toList());

        for (AliasUtils.Criterion criterion : criterions) {
            if (criterion.getAlias() != null) {
                aliases.addAll(criterion.getAlias().getSubAliases());
            }
        }
        return aliases;
    }
}
