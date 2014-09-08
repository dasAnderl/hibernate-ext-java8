package com.anderl.hibernate.ext;


import org.hibernate.sql.JoinType;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ga2unte on 12/2/13.
 */
public class AliasUtils {

    public enum Alias {

        PRODUCT_BOOKINGENTITY_ACCOUNTSETUP_CLIENT_CREATOR("product.bookingEntity.accountSetup.client.creator", JoinType.LEFT_OUTER_JOIN);

        private final String fieldPath;
        private final JoinType joinType;

        Alias(String fieldPath, JoinType joinType) {
            this.fieldPath = fieldPath;
            this.joinType = joinType;
        }

        public String getFieldPath() {
            return fieldPath;
        }

        public JoinType getJoinType() {
            return joinType;
        }

        /**
         * For each nested property we need an (sub)alias.
         * E.g. "foo.bar" results in Alias for foo and one alias for bar (which bases on the foo alias)
         * @return
         */
        public List<SubAlias> getSubAliases() {
            List<SubAlias> subAliases = new ArrayList<>();
            String aliasPath = "";
            for (String s : getFieldPath().split("\\.")) {
                if (!StringUtils.isEmpty(aliasPath)) {
                    aliasPath = aliasPath + ".";
                }
                aliasPath = aliasPath + s;
                subAliases.add(new SubAlias(aliasPath, s, this.getJoinType()));
            }
            return subAliases;

        }
    }

    public static class SubAlias {

        private final String path;
        private final String name;
        private final JoinType joinType;

        public SubAlias(String path, String name, JoinType joinType) {
            this.path = path;
            this.name = name;
            this.joinType = joinType;
        }

        public String getPath() {
            return path;
        }

        public String getName() {
            return name;
        }

        public JoinType getJoinType() {
            return joinType;
        }
    }

    public static class Criterion {
        private final Alias alias;
        private final String property;

        public Criterion(String property, Alias alias) {
            this.property = property;
            this.alias = alias;
        }

        public Criterion(String property) {
            this.property = property;
            alias = null;
        }

        public Alias getAlias() {
            return alias;
        }

        public String getCriterionPath() {
            if (alias == null) return property;
            String fieldPath = alias.getFieldPath();
            fieldPath = fieldPath.substring(fieldPath.lastIndexOf(".") + 1);
            return fieldPath + "." + property;
        }

        public String getFieldPath() {
            return alias == null ? property : alias.getFieldPath() + "." + property;
        }


    }
}
