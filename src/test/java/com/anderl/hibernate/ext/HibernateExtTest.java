package com.anderl.hibernate.ext;

import groovy.lang.DelegatesTo;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * Created by ga2unte on 10.9.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class HibernateExtTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @Transactional
    public void test() {

        Session session = entityManager.unwrap(Session.class);
    }
}
