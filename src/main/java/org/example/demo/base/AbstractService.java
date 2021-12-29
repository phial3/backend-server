package org.example.demo.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.demo.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public abstract class AbstractService<T extends Entity> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractService.class);

    private Class<T> beanType = null;

    protected abstract BaseMapper<T> mapper();

    protected void named(T entity, boolean update) {
        if (entity instanceof NamedEntity) {
            String name = ((NamedEntity) entity).getName();
        }
    }

    protected void doCheck(T entity, boolean update) {
    }

    protected void setOperator(T bean, boolean update) {
    }

    protected void validate(T entity, boolean update) {
        Assert.notNull(entity, "entity must not null.");
        doCheck(entity, update);
        named(entity, update);
        setOperator(entity, update);
    }

    protected Class<T> getBeanType() {
        if (this.beanType != null) {
            return beanType;
        }
        beanType = (Class<T>) ClassUtils.getFirstParameterizedType(this.getClass());
        return beanType;
    }

    protected T newInstance(Long id) {
        Class<T> t = getBeanType();
        try {
            T bean = t.getDeclaredConstructor(Long.class).newInstance(id);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected QueryWrapper<T> renderSearchEngine(ParametersBuilder<T> parametersBuilder) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (parametersBuilder == null) {
            return wrapper;
        }

        int invalid = 0;
        Map<String, Object> parameters = parametersBuilder.build();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            boolean valid = false;

            int oper = 0;
            if (name.startsWith("__LIKE__")) {
                oper = 1;
                name = name.substring(8);
            } else if (name.startsWith("__<=__")) {
                oper = 2;
                name = name.substring(6);
            } else if (name.startsWith("__>=__")) {
                oper = 3;
                name = name.substring(6);
            } else if (name.startsWith("__>__")) {
                oper = 4;
                name = name.substring(5);
            } else if (name.startsWith("__<__")) {
                oper = 5;
                name = name.substring(5);
            } else if (name.startsWith("__!=__")) {
                oper = 6;
                name = name.substring(6);
            } else if (name.startsWith("__IN__")) {
                oper = 7;
                name = name.substring(6);
            }

            if (value instanceof String) {
                if (!StringUtils.hasText((String) value)) value = null;
            }

            if (value != null && isColumnExists(name)) {
                valid = true;
                switch (oper) {
                    case 1:
                        wrapper.like(name, "%" + value + "%"); // like
                        break;
                    case 2:
                        wrapper.le(name, value); // <=
                        break;
                    case 3:
                        wrapper.ge(name, value); // >=
                        break;
                    case 4:
                        wrapper.gt(name, value); // >
                        break;
                    case 5:
                        wrapper.lt(name, value); // <
                        break;
                    case 6:
                        wrapper.ne(name, value); // =
                        break;
                    case 7:
                        if (value instanceof Collection) {
                            Object[] objects = ((Collection) value).toArray();
                            wrapper.in(name, objects);
                        } else if (value instanceof Object[]) {
                            wrapper.in(name, (Object[]) value);
                        }
                        break;
                    default:
                        wrapper.eq(name, value);
                }
            } else {
                ++invalid;
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Search field is ignored: name={}, value={}", name, value);
                }
            }
            if (parametersBuilder.isChainedDepend()) {
                if (valid && invalid > 0) throw new RuntimeException("该查询条件必须遵守最左填充原则");
            }
        }
        return wrapper;
    }

    private boolean isColumnExists(String column) {
        Field field = ClassUtils.getField(getBeanType(), column);

        return field != null;
    }

    public long count(ParametersBuilder parametersBuilder) {
        QueryWrapper<T> builder = parametersBuilder.getQueryWrapper();
        if (builder == null) {
            builder = renderSearchEngine(parametersBuilder);
        }
        return mapper().selectCount(builder);
    }

    public T get(long id) {
        T bean = newInstance(id);
        return doGet(bean);
    }

    protected T doGet(T bean) {
        return mapper().selectById(bean);
    }

    public T save(T bean) {
        validate(bean, false);
        long id = doSave(bean);
        Assert.isTrue(id > 0, "内部错误");
        return bean;
    }

    protected long doSave(T bean) {
        mapper().insert(bean);
        return (long) bean.getId();
    }

    public void update(T bean) {
        validate(bean, true);

        // 获取旧的实体
        T old = mapper().selectById(bean.getId());
        int ret = doUpdate(bean);

        LOG.info("Update bean done:ret={}, id={}, class={}", ret, bean.getId(), bean.getClass().getSimpleName());

        Assert.isTrue(ret >= 0, "内部错误");
    }

    protected int doUpdate(T bean) {
        return mapper().updateById(bean);
    }

    public void delete(Long ids[]) {
        for (Long id : ids) {
            mapper().deleteById(id);
        }
    }

    public List<T> list(ParametersBuilder<T> builder, int pageNo, int pageSize) {
        QueryWrapper<T> wrapper = renderSearchEngine(builder);

        wrapper.orderBy(false, SortDirection.isAsc(builder.getSortDirection()), builder.getOrderField())
                .last(true, "LIMIT " + PageResult.limit(pageNo, pageSize));

        List<T> list = doQuery(wrapper);
        builder.setQueryWrapper(wrapper);

        return list;
    }

    public List<T> listAll(ParametersBuilder<T> parametersBuilder) {
        QueryWrapper<T> builder = renderSearchEngine(parametersBuilder);
        builder.orderByDesc("id");
        return doQuery(builder);
    }

    public List<T> doQuery(QueryWrapper<T> query) {
        return mapper().selectList(query);
    }

    public List<T> doQuery(LambdaQueryWrapper<T> query) {
        return mapper().selectList(query);
    }
}
