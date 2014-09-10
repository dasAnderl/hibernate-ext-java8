package com.anderl.hibernate.ext

import com.anderl.hibernate.ext.domain.TestEntity
import com.anderl.hibernate.ext.domain.TestEntityRepository
import org.hibernate.Session
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager

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

    String name1 = "name1"
    String name2 = "name2"
    String name3 = "name3"
    int age1 = 1
    int age2 = 2
    int age3 = 3

    TestEntity te1 = new TestEntity(name: name1, age: age1)
    TestEntity te2 = new TestEntity(name: name2, age: age2)
    TestEntity te3 = new TestEntity(name: name3, age: age3)

    @Before
    void before() {
        System.out.println("saving new entities");
        testEntityRepository.save([te1, te2, te3]);
    }


    @Test
    @Transactional
    void test() {

        Session session = entityManager.unwrap(Session.class);
    }
}
