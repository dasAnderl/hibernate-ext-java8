package com.anderl.hibernate.ext.test.domain;

import com.google.common.collect.Lists;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by dasanderl on 07.09.14.
 */
@Entity
public class TestEntity extends _AbstractEntity {

    private String name;
    private int age;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "testEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubEntity> subEntities = Lists.newArrayList();

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

    public List<SubEntity> getSubEntities() {
        return subEntities;
    }

    public void setSubEntities(List<SubEntity> subEntities) {
        this.subEntities = subEntities;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
