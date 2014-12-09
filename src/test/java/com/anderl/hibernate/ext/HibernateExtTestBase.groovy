package com.anderl.hibernate.ext

import com.anderl.hibernate.ext.test.Application
import com.anderl.hibernate.ext.test.PagingService
import com.anderl.hibernate.ext.test.TestFilter
import com.anderl.hibernate.ext.test.domain.DomainProvider
import com.anderl.hibernate.ext.test.domain.Entity
import com.anderl.hibernate.ext.test.domain.EntityRepository
import com.anderl.hibernate.ext.wrappers.Filter
import com.anderl.hibernate.ext.wrappers.OrFilter
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import javax.persistence.EntityManager

import static com.anderl.hibernate.ext.AliasUtils.Criterion
import static com.anderl.hibernate.ext.test.Aliases.SUBENTITIES
import static org.junit.Assert.assertThat

/**
 * Created by ga2unte on 10.9.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
class HibernateExtTestBase extends groovy.util.GroovyTestCase {

    @Autowired
    EntityManager entityManager;

    @Autowired
    PagingService<Entity> pagingService

    @Autowired
    EntityRepository testEntityRepository

    @Autowired
    PagingService<Entity> testPagingService;

    List<Entity> entities = DomainProvider.getEntities()
    boolean entitiesSaved = false;
    TestFilter filter = new TestFilter()


    @Before
    void before() {
        if (!entitiesSaved) {
            System.out.println("saving new entities")
            testEntityRepository.save(entities)
            entitiesSaved = true
        }
    }

    @Test
    public void testFilter1() {
        def count = pagingService.count(filter)
        assertThat("default TestFilterHolder should only match one entity", count, Matchers.is(1))
    }

    @Test
    public void testFilter2() {
        filter.nameFilter.enabled = false
        filter.ageFilter = new Filter<Integer>(new Criterion("age"), RestrictionsExt.greaterThan, -1)
        filter.subNameFilter.enabled = false
        def count = pagingService.count(filter)
        assertThat("should return all entities", count, Matchers.is(entities.size()))
    }

    @Test
    public void testFilter3() {
        filter.nameFilter.enabled = true
        filter.nameFilter.value = "notExistingName1131"
        filter.ageFilter = new Filter<Integer>(new Criterion("age"), RestrictionsExt.greaterThan, -1)
        filter.subNameFilter.enabled = false
        def count = pagingService.count(filter)
        assertThat("should return no entities", count, Matchers.is(0))
    }

    @Test
    public void testFilter4() {
        filter.nameFilter.enabled = false
        filter.ageFilter.enabled = false
        filter.subNameFilter.enabled = false
        filter.orNameFilter = OrFilter.orOr(
                new Filter<String>(Criterion.get("name", SUBENTITIES), RestrictionsExt.in, [DomainProvider.name1, DomainProvider.name2]),
                new Filter<String>(Criterion.get("age", SUBENTITIES), RestrictionsExt.equal, DomainProvider.age3)
        );
        def count = pagingService.count(filter)
        assertThat("should return all entities", count, Matchers.is(entities.size()))
    }

    @Test
    public void testFilter5() {
        filter.nameFilter.enabled = false
        filter.ageFilter.enabled = false
        filter.subNameFilter.enabled = false
        filter.orNameFilter = OrFilter.orOr(
                new Filter<String>(Criterion.get("name", SUBENTITIES), RestrictionsExt.in, [DomainProvider.name1, DomainProvider.name2]),
        );
        def count = pagingService.count(filter)
        assertThat("should return two entities", count, Matchers.is(2))
    }


}
