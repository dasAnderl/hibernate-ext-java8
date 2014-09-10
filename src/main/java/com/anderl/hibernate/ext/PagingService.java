package com.anderl.hibernate.ext;


import com.anderl.hibernate.ext.helper.LogTimer;
import com.anderl.hibernate.ext.wrappers.OrderWrapper;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ga2unte on 12/20/13.
 */
public class PagingService<T> {

    @Autowired
    private EntityManager entityManager;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    /**
     * Getting the correct number of results when joining multiple tables and using sort orders,
     * is very complex in hibernate.
     * To return distinct accountsetups, we first get the distinct accountsetup ids matching our
     * criteria. On that id list we return the accountsetups.
     * BE VERY CAREFUL WHEN CHANINGING THIS.
     *
     * @param firstResultIndex
     * @param maxResults
     * @param searchCriteria
     * @return
     */
    @Transactional(readOnly = true)
    public <T> List<T> page(SearchCriteria searchCriteria) {
        final int firstResultIndex = searchCriteria.getPagingHelper().firstResultIndex;
        final int maxResults = searchCriteria.getPagingHelper().getPageSize();
        LogTimer logTimer = new LogTimer().enter("page start firstResultIndex: {} maxResults {}", firstResultIndex, maxResults);
        Criteria criteria = getCriteriaWithAliases(searchCriteria);
        HibernateWrapperToCriteriaMapper.addCriterionWrappers(criteria, searchCriteria);
        OrderWrapper orderWrapper = searchCriteria.getOrderWrapper();
        CriteriaHelper.addDistinctIdAndOrderProjections(criteria, orderWrapper);
        boolean hasOrder = orderWrapper != null;
        //First retrieve distinct list of ids
        List result = criteria.setFirstResult(firstResultIndex).setMaxResults(maxResults).list();
        List<Object> entityIds = CriteriaHelper.getIdsFromResultSet(hasOrder, result);
        if (CollectionUtils.isEmpty(entityIds)) {
            logTimer.exit("returning 0 entities");
            return new ArrayList<T>();
        }
        //Then return entities with ids in list
        criteria = getCriteriaWithAliases(searchCriteria).add(Restrictions.in("id", entityIds));
        if (hasOrder) {
            criteria.addOrder(orderWrapper.get());
        }
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        List<T> entities = criteria.list();
        logTimer.exit("returning {} entities", entities.size());
        return entities;
    }

    private Criteria getCriteriaWithAliases(SearchCriteria searchCriteria) {

        Criteria criteria = getSession().createCriteria(searchCriteria.getType());
        List<AliasUtils.SubAlias> aliasesForQuery = HibernateCriterionRetriever.getDistinctAliases(searchCriteria);

        for (AliasUtils.SubAlias alias : aliasesForQuery) {
            criteria.createAlias(alias.getPath(), alias.getName(), alias.getJoinType());
        }
        return criteria;
    }

    @Transactional(readOnly = true)
    public <T> int count(SearchCriteria searchCriteria) {
        LogTimer logTimer = new LogTimer().enter("page count start for entity {}", searchCriteria.getType().getSimpleName());
        Criteria criteria = getCriteriaWithAliases(searchCriteria);
        CriteriaHelper.addCountDistinctIdProjections(criteria);
        HibernateWrapperToCriteriaMapper.addCriterionWrappers(criteria, searchCriteria);
        Long count = (Long) criteria.uniqueResult();
        int intCount = count.intValue();
        logTimer.exit("search count is {}", count);
        return intCount;
    }
}

