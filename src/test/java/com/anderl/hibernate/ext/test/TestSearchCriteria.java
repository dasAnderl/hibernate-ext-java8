package com.anderl.hibernate.ext.test;

import com.anderl.hibernate.ext.AliasUtils;
import com.anderl.hibernate.ext.PagingHelper;
import com.anderl.hibernate.ext.RestrictionsExt;
import com.anderl.hibernate.ext.SearchCriteria;
import com.anderl.hibernate.ext.test.domain.TestEntity;
import com.anderl.hibernate.ext.wrappers.CriterionWrapper;
import com.anderl.hibernate.ext.wrappers.OrderWrapper;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

import static com.anderl.hibernate.ext.AliasUtils.Alias.*;

/**
 * Created by ga2unte on 10.9.2014.
 */
public class TestSearchCriteria implements SearchCriteria<TestEntity> {

    OrderWrapper orderWrapper = OrderWrapper.desc(new AliasUtils.Criterion("name", SUBENTITIES));
    PagingHelper pagingHelper = new PagingHelper();



    CriterionWrapper<Integer> criterionAgeEq2 = new CriterionWrapper<>(new AliasUtils.Criterion("age", SUBENTITIES), RestrictionsExt.greaterThan, 2);
    CriterionWrapper<Integer> criterionSubEntAgeGt2 = new CriterionWrapper<>(new AliasUtils.Criterion("age", SUBENTITIES), RestrictionsExt.greaterThan, 2);
    CriterionWrapper<List<String>> criterionSubEntNameInName1Name2 = new CriterionWrapper<>(new AliasUtils.Criterion("name", SUBENTITIES), RestrictionsExt.in, Arrays.asList("name1", "name2"));

    public OrderWrapper getOrderWrapper() {
        return orderWrapper;
    }

    public void setOrderWrapper(OrderWrapper orderWrapper) {
        this.orderWrapper = orderWrapper;
    }

    public PagingHelper getPagingHelper() {
        return pagingHelper;
    }

    public void setPagingHelper(PagingHelper pagingHelper) {
        this.pagingHelper = pagingHelper;
    }

    public CriterionWrapper<Integer> getCriterionAgeEq2() {
        return criterionAgeEq2;
    }

    public void setCriterionAgeEq2(CriterionWrapper<Integer> criterionAgeEq2) {
        this.criterionAgeEq2 = criterionAgeEq2;
    }

    public CriterionWrapper<Integer> getCriterionSubEntAgeGt2() {
        return criterionSubEntAgeGt2;
    }

    public void setCriterionSubEntAgeGt2(CriterionWrapper<Integer> criterionSubEntAgeGt2) {
        this.criterionSubEntAgeGt2 = criterionSubEntAgeGt2;
    }

    public CriterionWrapper<List<String>> getCriterionSubEntNameInName1Name2() {
        return criterionSubEntNameInName1Name2;
    }

    public void setCriterionSubEntNameInName1Name2(CriterionWrapper<List<String>> criterionSubEntNameInName1Name2) {
        this.criterionSubEntNameInName1Name2 = criterionSubEntNameInName1Name2;
    }
}
