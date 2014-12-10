package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.test.*;
import com.anderl.hibernate.ext.test.PagingService;
import com.anderl.hibernate.ext.test.domain.DomainProvider;
import com.anderl.hibernate.ext.test.domain.Entity;
import com.anderl.hibernate.ext.test.domain.EntityRepository;
import com.anderl.hibernate.ext.wrappers.Filter;
import com.anderl.hibernate.ext.wrappers.OrFilter;
import groovy.util.GroovyTestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.anderl.hibernate.ext.test.Aliases.*;

/**
 * Created by ga2unte on 10.9.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class HibernateExtTestBase extends GroovyTestCase {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PagingService<Entity> pagingService;
    @Autowired
    private EntityRepository testEntityRepository;
    @Autowired
    private PagingService<Entity> testPagingService;
    private List<Entity> entities = DomainProvider.getEntities();
    private boolean entitiesSaved = false;
    private TestFilter filter = new TestFilter();

    @Before
    public void before() {
        if (!entitiesSaved) {
            System.out.println("saving new entities");
            testEntityRepository.save(entities);
            entitiesSaved = true;
        }

    }

    @Test
    public void testFilter1() {
        Integer count = pagingService.count(filter);
        Assert.assertThat("default TestFilterHolder should only match one entity", count, Matchers.is(1));
    }

    @Test
    public void testFilter2() {
        filter.getNameFilter().setEnabled(false);
        filter.setAgeFilter(Filter.get(AliasUtils.Criterion.get("age"), RestrictionsExt.greaterThan, Integer.class, - 1));
        filter.getSubNameFilter().setEnabled(false);
        Integer count = pagingService.count(filter);
        Assert.assertThat("should return all entities", count, Matchers.is(entities.size()));
    }

    @Test
    public void testFilter3() {
        filter.getNameFilter().setEnabled(true);
        filter.getNameFilter().setValue("notExistingName1131");
        filter.setAgeFilter(Filter.get(AliasUtils.Criterion.get("age"), RestrictionsExt.greaterThan, Integer.class, -1));
        filter.getSubNameFilter().setEnabled(false);
        Integer count = pagingService.count(filter);
        Assert.assertThat("should return no entities", count, Matchers.is(0));
    }

    @Test
    public void testFilter4() {
        filter.getNameFilter().setEnabled(false);
        filter.getAgeFilter().setEnabled(false);
        filter.getSubNameFilter().setEnabled(false);
        filter.setOrNameFilter(OrFilter.orOr(Filter.get(AliasUtils.Criterion.get("name", SUBENTITIES), RestrictionsExt.in, List.class, new ArrayList<String>(Arrays.asList(DomainProvider.getName1(), DomainProvider.getName2()))), Filter.get(AliasUtils.Criterion.get("age", SUBENTITIES), RestrictionsExt.equal, Integer.class, DomainProvider.getAge3())));
        Integer count = pagingService.count(filter);
        Assert.assertThat("should return all entities", count, Matchers.is(entities.size()));
    }

    @Test
    public void testFilter5() {
        filter.getNameFilter().setEnabled(false);
        filter.getAgeFilter().setEnabled(false);
        filter.getSubNameFilter().setEnabled(false);
        filter.setOrNameFilter(OrFilter.orOr(Filter.get(AliasUtils.Criterion.get("name", SUBENTITIES), RestrictionsExt.in, List.class, new ArrayList<String>(Arrays.asList(DomainProvider.getName1(), DomainProvider.getName2())))));
        Integer count = pagingService.count(filter);
        Assert.assertThat("should return two entities", count, Matchers.is(2));
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public com.anderl.hibernate.ext.test.PagingService<Entity> getPagingService() {
        return pagingService;
    }

    public void setPagingService(PagingService<Entity> pagingService) {
        this.pagingService = pagingService;
    }

    public EntityRepository getTestEntityRepository() {
        return testEntityRepository;
    }

    public void setTestEntityRepository(EntityRepository testEntityRepository) {
        this.testEntityRepository = testEntityRepository;
    }

    public PagingService<Entity> getTestPagingService() {
        return testPagingService;
    }

    public void setTestPagingService(PagingService<Entity> testPagingService) {
        this.testPagingService = testPagingService;
    }

    public List<Entity> getEntities() {
        return entities;
    }


}
