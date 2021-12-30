package org.example.demo.base;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.phial.mybatisx.api.query.QueryBuilder;
import org.phial.mybatisx.api.query.QueryCustomizer;
import org.phial.mybatisx.api.query.SortDirection;

import java.util.Map;

/**
 * @author phial
 * @vendor phial.org
 * @generator consolegen 1.0
 * @manufacturer https://phial.org
 * @since 2019-10-10
 */
public class ParametersBuilder {

    private Map<String, Object> parameters;
    private Map<String, Object> extra;

    private String orderField = "id";

    private SortDirection orderDirection = SortDirection.DESC;

    private String excludeFields[];

    /**
     * 用来自定义查询
     */
    private QueryCustomizer queryCustomizer;

    /**
     * 存储渲染过的Builder
     */
    private QueryBuilder renderedQueryBuilder;


    /**
     * 是否开启链式依赖：如果开启链式依赖的话，查询条件遵守最左依赖原则，目的是为了优化联合索引的性能
     */
    private boolean chainedDepend;

    private ParametersBuilder() {
        this.parameters = new LinkedMap();
        this.extra = new HashedMap();
        this.chainedDepend = false;
    }

    private ParametersBuilder(String orderField, SortDirection orderDirection) {
        this();
        this.orderField = orderField;
        this.orderDirection = orderDirection;
    }

    public static ParametersBuilder custom(String orderField, SortDirection orderDirection) {
        return new ParametersBuilder(orderField, orderDirection);
    }

    public static ParametersBuilder custom() {
        return new ParametersBuilder();
    }

    public ParametersBuilder add(String name, Object value) {
        this.parameters.put(name, value);
        return this;
    }

    public ParametersBuilder remove(String name) {
        this.parameters.remove(name);
        return this;
    }

    public boolean hasAndNotEq(String name, Object o) {
        return o!=null && this.parameters.containsKey(name) && !this.parameters.get(name).equals(o);
    }

    public ParametersBuilder enabledChainedDepend() {
        this.chainedDepend = true;
        return this;
    }

    public ParametersBuilder disabledChainedDepend() {
        this.chainedDepend = false;
        return this;
    }

    public ParametersBuilder extra(String name, Object data) {
        this.extra.put(name, data);
        return this;
    }

    public Map<String, Object> extras() {
        return this.extra;
    }

    public Object extra(String name) {
        return this.extra.get(name);
    }

    public boolean isChainedDepend() {
        return chainedDepend;
    }

    public Map<String, Object> build() {
        return parameters;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public SortDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(SortDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    public ParametersBuilder excludes(String... excludeFields) {
        this.excludeFields = excludeFields;
        return this;
    }

    public String[] getExcludeFields() {
        return this.excludeFields;
    }

    /**
     * 获取 queryCustomizer
     *
     * @return queryCustomizer
     */
    public QueryCustomizer getQueryCustomizer() {
        return queryCustomizer;
    }

    /**
     * 设置 queryCustomizer
     *
     * @param queryCustomizer queryCustomizer 值
     */
    public void setQueryCustomizer(QueryCustomizer queryCustomizer) {
        this.queryCustomizer = queryCustomizer;
    }

    /**
     * 获取 renderedQueryBuilder
     *
     * @return renderedQueryBuilder
     */
    public QueryBuilder getRenderedQueryBuilder() {
        return renderedQueryBuilder;
    }

    /**
     * 设置 renderedQueryBuilder
     *
     * @param renderedQueryBuilder renderedQueryBuilder 值
     */
    public void setRenderedQueryBuilder(QueryBuilder renderedQueryBuilder) {
        this.renderedQueryBuilder = renderedQueryBuilder;
    }
}
