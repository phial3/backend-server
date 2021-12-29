package org.example.demo.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @description:
 * @project: backend-server
 * @author: gaoyanfei3
 * @datetime: 2021/12/29 07:23 Wednesday
 */
public class ParametersBuilder<T extends Entity> {

    private Map<String, Object> parameters;

    private String orderField = "id";

    private SortDirection sortDirection = SortDirection.DESC;

    /**
     * 存储渲染过的Builder
     */
    private QueryWrapper<T> queryWrapper;

    private boolean chainedDepend;

    private ParametersBuilder() {
        this.parameters = new LinkedHashMap<>();
    }

    private ParametersBuilder(Map<String, Object> map) {
        this.parameters = new LinkedHashMap<>(map);
    }

    private ParametersBuilder(String orderField, SortDirection orderDirection) {
        this();
        this.orderField = orderField;
        this.sortDirection = orderDirection;
        this.chainedDepend = false;
    }

    //// export
    public static ParametersBuilder custom(String orderField, SortDirection orderDirection) {
        return new ParametersBuilder(orderField, orderDirection);
    }

    public static ParametersBuilder custom() {
        return new ParametersBuilder();
    }

    public static ParametersBuilder toMap(Map<String, Object> params) {
        return new ParametersBuilder(params);
    }

    //
    public Map<String, Object> build() {
        return parameters;
    }

    public ParametersBuilder<T> add(String name, Object value) {
        this.parameters.put(name, value);
        return this;
    }

    public ParametersBuilder<T> remove(String name) {
        this.parameters.remove(name);
        return this;
    }

    public boolean isChainedDepend() {
        return chainedDepend;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    public QueryWrapper<T> getQueryWrapper() {
        return queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper<T> queryWrapper) {
        this.queryWrapper = queryWrapper;
    }
}
