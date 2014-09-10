package com.anderl.hibernate.ext.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by dasanderl on 07.09.14.
 */
@Entity
public class SubEntity extends _AbstractEntity {

    private String name;
    private int age;
    @ManyToOne
    private TestEntity testEntity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public TestEntity getTestEntity() {
        return testEntity;
    }

    public void setTestEntity(TestEntity testEntity) {
        this.testEntity = testEntity;
    }

    @Override
    public String toString() {
        return "SubEntity{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
