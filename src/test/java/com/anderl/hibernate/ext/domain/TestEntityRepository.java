package com.anderl.hibernate.ext.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by dasanderl on 07.09.14.
 */
public interface TestEntityRepository extends CrudRepository<TestEntity, Long> {

    List<TestEntity> findByName(String name);

    List<TestEntity> findByAge(int age);

    List<TestEntity> findByNameAndAge(String name, int age);

    List<TestEntity> findByNameOrAge(String name, int age);


}
