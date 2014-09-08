package com.anderl.hibernate.ext;


import com.anderl.hibernate.ext.helper.LogTimer;
import com.anderl.hibernate.ext.wrappers.OrderWrapper;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ga2unte on 12/20/13.
 */
@Service
public class PagingService {

    //TODO FIXME
    private Session session = null;

    /**
     * Getting the correct number of results when joining multiple tables and using sort orders,
     * is very complex in hibernate.
     * To return distinct accountsetups, we first get the distinct accountsetup ids matching our
     * criteria. On that id list we return the accountsetups.
     * BE VERY CAREFUL WHEN CHANINGING THIS.
     *
     * @param firstResultIndex
     * @param maxResults
     * @param hasCriteria
     * @return
     */
    @Transactional(readOnly = true)
    public <T> List<T> page(int firstResultIndex, int maxResults, HasCriteria hasCriteria) {
        LogTimer logTimer = new LogTimer().enter("page start firstResultIndex: {} maxResults {}", firstResultIndex, maxResults);
        Criteria criteria = getCriteriaWithAliases(hasCriteria);
        HibernateWrapperToCriteriaMapper.addCriterionWrappers(criteria, hasCriteria);
        OrderWrapper orderWrapper = hasCriteria.getOrderWrapper();
        CriteriaHelper.addDistinctIdAndOrderProjections(criteria, orderWrapper);
        boolean hasOrder = orderWrapper != null;
        //First retrieve distinct list of ids
        List result = criteria.setFirstResult(firstResultIndex).setMaxResults(maxResults).list();
        List<Object> entityIds = CriteriaHelper.getIdsFromResultSet(hasOrder, result);
        if (CollectionUtils.isEmpty(entityIds)) {
            logTimer.exit();
            return new ArrayList<T>();
        }
        //Then return entities with ids in list
        criteria = getCriteriaWithAliases(hasCriteria).add(Restrictions.in("id", entityIds));
        if (hasOrder) {
            criteria.addOrder(orderWrapper.get());
        }
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        List<T> entities = criteria.list();
        logTimer.exit("returning {} entities", entities.size());
        return entities;
    }

    private Criteria getCriteriaWithAliases(HasCriteria hasCriteria) {

        Criteria criteria = session.createCriteria(hasCriteria.getType());
        List<AliasUtils.SubAlias> aliasesForQuery = HibernateCriterionRetriever.getDistinctAliases(hasCriteria);

        for (AliasUtils.SubAlias alias : aliasesForQuery) {
            criteria.createAlias(alias.getPath(), alias.getName(), alias.getJoinType());
        }
        return criteria;
    }

    @Transactional(readOnly = true)
    public <T> int count(HasCriteria hasCriteria) {
        LogTimer logTimer = new LogTimer().enter("page count start for entity {}", hasCriteria.getType().getSimpleName());
        Criteria criteria = getCriteriaWithAliases(hasCriteria);
        CriteriaHelper.addCountDistinctIdProjections(criteria);
        HibernateWrapperToCriteriaMapper.addCriterionWrappers(criteria, hasCriteria);
        Long count = (Long) criteria.uniqueResult();
        int intCount = count.intValue();
        logTimer.exit("search count is {}", count);
        return intCount;
    }
}

