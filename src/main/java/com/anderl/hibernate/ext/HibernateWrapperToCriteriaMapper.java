package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.wrappers.Filter;
import com.anderl.hibernate.ext.wrappers.OrFilter;
import com.anderl.hibernate.ext.wrappers.Order;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ga2unte on 11/22/13.
 */
public class HibernateWrapperToCriteriaMapper {

    public static Criteria addCriterionWrappers(Criteria criteria,
                                                List<Filter> filters,
                                                List<OrFilter> criterionOrWrappers,
                                                Order order) {
        Criterion andCriterion = null;
        if (!CollectionUtils.isEmpty(filters)) {

            List<Criterion> criterions = filters.stream().map(wrapper -> wrapper.getCriterion()).collect(Collectors.toList());

            andCriterion = Restrictions.and(criterions.toArray(new Criterion[criterions.size()]));
        }

        if (CollectionUtils.isEmpty(criterionOrWrappers) && andCriterion != null) {
            criteria.add(andCriterion);
        } else {

            List<Criterion> orCriterions = criterionOrWrappers.stream().map(orWrapper -> orWrapper.getCriterion()).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(orCriterions)) {
                if (andCriterion != null) {
                    orCriterions.add(0, andCriterion);
                }
                Criterion orCriterion = Restrictions.and(orCriterions.toArray(new Criterion[orCriterions.size()]));
                criteria.add(orCriterion);
            }
        }
        // We only add orders, if they are present and this is not a count query
        if (order != null) {
            criteria.addOrder(order.get());
        }
        return criteria;
    }

    public static Criteria addCriterionWrappers(Criteria criteria,
                                                SearchFilter searchFilter) {
        return addCriterionWrappers(criteria, searchFilter.getCriterions(), searchFilter.getOrCriterions(), searchFilter.getOrderWrapper());
    }
}
