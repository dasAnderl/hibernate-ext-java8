package com.anderl.hibernate.ext.test;

import com.anderl.hibernate.ext.AliasUtils;
import org.hibernate.sql.JoinType;

/**
 * Created by dasanderl on 10.09.14.
 */
public enum Aliases implements AliasUtils.Alias {

    SUBENTITIES("subEntities", JoinType.LEFT_OUTER_JOIN);

    private final String fieldPath;
    private final JoinType joinType;

    Aliases(String fieldPath, JoinType joinType) {
        this.fieldPath = fieldPath;
        this.joinType = joinType;
    }

    @Override
    public String getFieldPath() {
        return fieldPath;
    }

    @Override
    public JoinType getJoinType() {
        return joinType;
    }
}

