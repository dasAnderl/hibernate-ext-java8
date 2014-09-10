package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.helper.Helper;
import com.anderl.hibernate.ext.test.domain.SubEntity;
import com.anderl.hibernate.ext.test.domain.TestEntity;
import com.anderl.hibernate.ext.wrappers.Filter;
import com.anderl.hibernate.ext.wrappers.OrFilter;
import org.hamcrest.Matchers;
import org.hibernate.criterion.Criterion;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Created by dasanderl on 11.09.14.
 */
public class HelperTest {

    public class ClassWithSetField {
        Set<TestEntity> testEntities;
    }

    @Test
    public void testFieldExistsRecursive() throws Exception {
        Assert.assertTrue(Helper.fieldExistsRecursive(OrFilter.class, "firstWrapper.criterion.property"));
        Assert.assertTrue(Helper.fieldExistsRecursive(TestEntity.class, "subEntities.age"));
        Assert.assertTrue(Helper.fieldExistsRecursive(ClassWithSetField.class, "testEntities.subEntities.age"));
        try {
            Helper.fieldExistsRecursive(ClassWithSetField.class, "testEntities.subEntities.thisFieldDoesNotExist");
            throw new Exception("method call before must throw "+IllegalArgumentException.class.getSimpleName());
        }   catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString(SubEntity.class.getSimpleName()));
            Assert.assertThat(e.getMessage(), Matchers.containsString(ClassWithSetField.class.getSimpleName()));
            Assert.assertThat(e.getMessage(), Matchers.containsString("testEntities.subEntities.thisFieldDoesNotExist"));
            Assert.assertThat(e.getMessage(), Matchers.containsString("thisFieldDoesNotExist "));
        }
    }

    @Test
    public void testGetGenericInterfaceType() throws Exception {
        Assert.assertEquals(Helper.getGenericInterfaceType(HibernateInTest.TestSearchFilter.class, 0), TestEntity.class);
    }

    public class Dummy {
        String s1 = "1";
        String s2 = "2";

        public String getS1() {
            return s1;
        }

        public String getS2() {
            return s2;
        }
    }

    @Test
    public void testInvokeGettersByReturnType() throws Exception {
        Assert.assertThat(Helper.invokeGettersByReturnType(String.class, new Dummy()).get(0), Matchers.is("1"));
        Assert.assertThat(Helper.invokeGettersByReturnType(String.class, new Dummy()).get(1), Matchers.is("2"));
    }
}
