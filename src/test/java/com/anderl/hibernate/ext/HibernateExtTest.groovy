package com.anderl.hibernate.ext

import com.anderl.hibernate.ext.test.Application
import com.anderl.hibernate.ext.test.TestPagingService
import com.anderl.hibernate.ext.test.domain.SubEntity
import com.anderl.hibernate.ext.test.domain.TestEntity
import com.anderl.hibernate.ext.test.domain.TestEntityRepository
import com.anderl.hibernate.ext.wrappers.CriterionWrapper
import com.anderl.hibernate.ext.wrappers.OrderWrapper
import org.hibernate.Session
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager

import static com.anderl.hibernate.ext.AliasUtils.Alias.SUBENTITIES
import static com.anderl.hibernate.ext.AliasUtils.Alias.SUBENTITIES
import static com.anderl.hibernate.ext.AliasUtils.Alias.SUBENTITIES
import static com.anderl.hibernate.ext.AliasUtils.Alias.SUBENTITIES

/**
 * Created by ga2unte on 10.9.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
class HibernateExtTest extends groovy.util.GroovyTestCase {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TestEntityRepository testEntityRepository

    @Autowired
    TestPagingService<TestEntity> testPagingService;

    static String name1 = "name1"
    static String name2 = "name2"
    static String name3 = "name3"
    static int age1 = 1
    static int age2 = 2
    static int age3 = 3

    static TestEntity te1 = new TestEntity(name: name1, age: age1)
    static TestEntity te2 = new TestEntity(name: name2, age: age2)
    static TestEntity te3 = new TestEntity(name: name3, age: age3)

    static {
        te1.subEntities.add(new SubEntity(name: name1, age: age1, testEntity: te1))
        te1.subEntities.add(new SubEntity(name: name1, age: age1, testEntity: te1))
        te1.subEntities.add(new SubEntity(name: "a different name", age: age1, testEntity: te1))
        te2.subEntities.add(new SubEntity(name: name2, age: age2, testEntity: te2))
        te3.subEntities.add(new SubEntity(name: name3, age: age3, testEntity: te3))
    }

    public class TestSearchCriteria implements SearchCriteria<TestEntity> {

        OrderWrapper orderWrapper = OrderWrapper.desc(new AliasUtils.Criterion("name", SUBENTITIES));
        PagingHelper pagingHelper = new PagingHelper();

        CriterionWrapper<Integer> criterionNameEqName1 = new CriterionWrapper<>(new AliasUtils.Criterion("age"), RestrictionsExt.eq, name1);
        CriterionWrapper<Integer> criterionSubEntAgeGt1 = new CriterionWrapper<>(new AliasUtils.Criterion("age", SUBENTITIES), RestrictionsExt.greaterThan, 1);
        CriterionWrapper<List<String>> criterionSubEntNameInName1Name2 = new CriterionWrapper<>(new AliasUtils.Criterion("name", SUBENTITIES), RestrictionsExt.in, Arrays.asList(name1, name2));
    }

    @Before
    void before() {
        System.out.println("saving new entities");
        testEntityRepository.save([te1, te2, te3]);
    }


    @Test
    @Transactional
    void test() {

        List<TestEntity> entities = testPagingService.page(new com.anderl.hibernate.ext.test.TestSearchCriteria())
    }
}
