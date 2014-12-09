package com.anderl.hibernate.ext.test

import com.anderl.hibernate.ext.PagingHelper
import com.anderl.hibernate.ext.RestrictionsExt
import com.anderl.hibernate.ext.SearchFilter
import com.anderl.hibernate.ext.test.domain.DomainProvider
import com.anderl.hibernate.ext.test.domain.Entity
import com.anderl.hibernate.ext.wrappers.Filter
import com.anderl.hibernate.ext.wrappers.OrFilter
import com.anderl.hibernate.ext.wrappers.Order

import static com.anderl.hibernate.ext.AliasUtils.Criterion

/**
 * Created by dasanderl on 09.12.14.
 */
class TestFilter implements SearchFilter<Entity> {

    @Override
    Order getOrder() {
        return new Order(new Criterion("name"), true)
    }

    @Override
    PagingHelper getPagingHelper() {
        return new PagingHelper()
    }

    Filter<String> nameFilter = new Filter(new Criterion("name"), RestrictionsExt.equal, DomainProvider.entities[0].name)
    Filter<Integer> ageFilter = new Filter(new Criterion("age"), RestrictionsExt.equal, DomainProvider.entities[0].age)
    Filter<String> subNameFilter = new Filter(new Criterion("name", Aliases.SUBENTITIES), RestrictionsExt.equal, DomainProvider.entities[0].subEntities[0].name)

    OrFilter orNameFilter;

    Filter<String> getNameFilter() {
        return nameFilter
    }

    void setNameFilter(Filter<String> nameFilter) {
        this.nameFilter = nameFilter
    }

    Filter<Integer> getAgeFilter() {
        return ageFilter
    }

    void setAgeFilter(Filter<Integer> ageFilter) {
        this.ageFilter = ageFilter
    }

    Filter<String> getSubNameFilter() {
        return subNameFilter
    }

    void setSubNameFilter(Filter<String> subNameFilter) {
        this.subNameFilter = subNameFilter
    }

    OrFilter getOrNameFilter() {
        return orNameFilter
    }

    void setOrNameFilter(OrFilter orNameFilter) {
        this.orNameFilter = orNameFilter
    }
}