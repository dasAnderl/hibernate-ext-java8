//package com.anderl.hibernate.ext
//
//import com.anderl.hibernate.ext.test.Aliases
//import com.anderl.hibernate.ext.test.domain.Entity
//import com.anderl.hibernate.ext.wrappers.Filter
//import com.anderl.hibernate.ext.wrappers.Order
//import org.hamcrest.Matchers
//import org.junit.Assert
//import org.junit.Test
//import org.springframework.transaction.annotation.Transactional
//
///**
// * Created by ga2unte on 10.9.2014.
// */
//public class HibernateInTest extends HibernateExtTestBase {
//
//    public class TestSearchFilter implements SearchFilter<Entity> {
//
//        Order orderWrapper = Order.desc(new AliasUtils.Criterion("name", Aliases.SUBENTITIES))
//        PagingHelper pagingHelper = new PagingHelper()
//
//        Filter<List<String>> criterionSubEntNameInName1Name2 = new Filter<>(new AliasUtils.Criterion("name", Aliases.SUBENTITIES), RestrictionsExt.in, Arrays.asList(name1, name2))
//    }
//
//    @Test
//    @Transactional
//    void testInReturningTe2Te1() {
//        List<Entity> entities = testPagingService.page(new TestSearchFilter())
//        Assert.assertThat("wrong nr of results", entities.size(), Matchers.is(2))
//        Assert.assertThat("first result should be te2", entities.get(0), Matchers.is(te2))
//        Assert.assertThat("second result should be te1", entities.get(1), Matchers.is(te1))
//    }
//
//    @Test
//    @Transactional
//    void testInReturningNth() {
//        def criteria = new TestSearchFilter()
//        criteria.criterionSubEntNameInName1Name2 = new Filter<>(new AliasUtils.Criterion("name", Aliases.SUBENTITIES), RestrictionsExt.in, Arrays.asList("noNameLikeInDb"))
//        List<Entity> entities = testPagingService.page(criteria)
//        Assert.assertThat("wrong nr of results", entities.size(), Matchers.is(0))
//    }
//
//    @Test
//    @Transactional
//    void testInReturningTe3() {
//        def criteria = new TestSearchFilter()
//        criteria.criterionSubEntNameInName1Name2 = new Filter<>(new AliasUtils.Criterion("name", Aliases.SUBENTITIES), RestrictionsExt.in, Arrays.asList(name3))
//        List<Entity> entities = testPagingService.page(criteria)
//        Assert.assertThat("wrong nr of results", entities.size(), Matchers.is(1))
//        Assert.assertThat("wrong age", entities.get(0).getAge(), Matchers.is(te3.age))
//        Assert.assertThat("worng name", entities.get(0).getName(), Matchers.is(te3.name))
//    }
//}
