package com.anderl.hibernate.ext;

import com.anderl.hibernate.ext.helper.Helper;
import com.anderl.hibernate.ext.test.domain.SubEntity;
import com.anderl.hibernate.ext.test.domain.Entity;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Created by dasanderl on 11.09.14.
 */
public class HelperTest {

    private final class ReflectionTestClass {
        Entity entity;
        Set<Entity> testEntities;
    }

    @Test
    public void testFieldExistsRecursive() throws Exception {
        assertTrue(Helper.fieldExistsRecursive(ReflectionTestClass.class, "entity.subEntities.name"));
        assertTrue(Helper.fieldExistsRecursive(ReflectionTestClass.class, "testEntities.subEntities.age"));
        try {
            Helper.fieldExistsRecursive(ReflectionTestClass.class, "testEntities.subEntities.thisFieldDoesNotExist");
            throw new Exception("method call before must throw "+IllegalArgumentException.class.getSimpleName());
        }   catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString(SubEntity.class.getSimpleName()));
            assertThat(e.getMessage(), containsString(ReflectionTestClass.class.getSimpleName()));
            assertThat(e.getMessage(), containsString("testEntities.subEntities.thisFieldDoesNotExist"));
            assertThat(e.getMessage(), containsString("thisFieldDoesNotExist "));
        }
    }

    @Test
    public void testGetGenericInterfaceType() throws Exception {
        assertEquals(Helper.getGenericInterfaceType(HibernateInTest.TestSearchFilter.class, 0), Entity.class);
    }

    public final class Dummy {
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
        List<String> actual = Helper.invokeGettersByReturnType(String.class, new Dummy());
        assertThat(actual, contains("1", "2"));
        assertThat(actual.size(), is(2));
    }
}
