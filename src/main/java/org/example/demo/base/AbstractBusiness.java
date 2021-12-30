package org.example.demo.base;

import org.apache.commons.lang3.StringUtils;
import org.example.demo.entity.User;
import org.example.demo.event.EntityCache;
import org.example.demo.event.EntityEvent;
import org.example.demo.event.EntityEventDispatcher;
import org.example.demo.event.EntityEventListener;
import org.phial.mybatisx.api.SFunction;
import org.phial.mybatisx.api.entity.EditableEntity;
import org.phial.mybatisx.api.entity.Entity;
import org.phial.mybatisx.api.entity.NamedEntity;
import org.phial.mybatisx.api.query.Query;
import org.phial.mybatisx.api.query.QueryBuilder;
import org.phial.mybatisx.api.query.SortDirection;
import org.phial.mybatisx.common.Assert;
import org.phial.mybatisx.common.ServiceException;
import org.phial.mybatisx.common.utils.ClassUtils;
import org.phial.mybatisx.dal.dao.BasicDAO;
import org.phial.mybatisx.dal.generator.AnnotationHelper;
import org.phial.mybatisx.dal.generator.AnnotationHolder;
import org.phial.mybatisx.dal.util.Extras;
import org.phial.rest.common.util.Strings;
import org.phial.rest.web.session.SessionUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class AbstractBusiness<T extends Entity>  implements EntityEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractBusiness.class);

    public static final int PAGE_SIZE = 10;

    // java method getter or setter extract
    private static final Pattern GET_PATTERN = Pattern.compile("^get[A-Z].*");
    private static final Pattern IS_PATTERN = Pattern.compile("^is[A-Z].*");

    private Class<T> beanType = null;

    @Resource
    protected BasicDAO service;

    @Resource
    protected SessionManager sessionManager;

    @Resource
    private EntityEventDispatcher dispatcher;

    @Resource
    protected EntityCache cache;

    public static <T extends Entity> String getFieldColumn(SFunction<T, ?> dynamicField) throws Exception {
        // 直接调用writeReplace
        Method method = dynamicField.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(Boolean.TRUE);
        // 调用writeReplace()方法，返回一个SerializedLambda对象
        SerializedLambda serializedLambda = (SerializedLambda) method.invoke(dynamicField);
        // 得到lambda表达式中调用的方法名，如 "User::getSex"，则得到的是"getSex"
        String methodName = serializedLambda.getImplMethodName();
        if (GET_PATTERN.matcher(methodName).matches()) {
            methodName = methodName.substring(3);
        } else if (IS_PATTERN.matcher(methodName).matches()) {
            methodName = methodName.substring(2);
        }
        return firstToLowerCase(methodName);
    }

    private static String firstToLowerCase(String s) {
        if (Character.isLowerCase(s.charAt(0))) return s;
        else return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public BasicDAO dao() {
        return service;
    }

    protected TransactionTemplate transaction() {
        return service.databaseRouter().getDatabaseSession().transaction();
    }

    protected Extras getExtras(T entity, boolean update) {
        return null;
    }

    protected void named(T entity, boolean update) {
        if (entity instanceof NamedEntity) {
            String name = ((NamedEntity) entity).getName();
            if (StringUtils.isNotBlank(name)) {
                String pinyin = Strings.pinyin(name);
                ((NamedEntity) entity).setPinyin(pinyin);
            }

            Extras extras = getExtras(entity, update);
            if (extras != null) {
                ((NamedEntity) entity).setExtras(extras.toJSONString());
            }
        }
    }

    public long count(ParametersBuilder parametersBuilder) {
        QueryBuilder<T> builder = parametersBuilder.getRenderedQueryBuilder();
        if (builder == null) {
            builder = renderSearchEngine(parametersBuilder);
            customQueryBuilder(parametersBuilder, builder);
        }

        return service.count(builder.build());
    }

    private void customQueryBuilder(ParametersBuilder parametersBuilder, QueryBuilder<T> builder) {
        if (parametersBuilder.getQueryCustomizer() != null) {
            parametersBuilder.getQueryCustomizer().custom(builder);
        }
    }

    /**
     * 查询实体列表
     *
     * @return
     */
    public List<T> list(ParametersBuilder parametersBuilder, int page, int pageSize) {
        QueryBuilder<T> builder = renderSearchEngine(parametersBuilder);

        // set page
        if (page < 0) page = 1;
        if (pageSize < 0) pageSize = PAGE_SIZE;
        page = (page - 1) * pageSize;
        builder.limit(page, pageSize);

        String orderField = parametersBuilder.getOrderField();
        if (StringUtils.isBlank(orderField) || !isColumnExists(orderField)) orderField = "id";
        SortDirection sd = parametersBuilder.getOrderDirection();
        if (sd == null) sd = SortDirection.DESC;

        builder.orderBy(orderField, sd);

        customQueryBuilder(parametersBuilder, builder);

        List<T> list = doQuery(builder);
        parametersBuilder.setRenderedQueryBuilder(builder);

        return list;
    }


    protected void renderListAllBuilder(QueryBuilder<T> builder) {
        builder.orderBy(T::getId, SortDirection.DESC);
    }

    public List<T> listAll(ParametersBuilder parametersBuilder) {
        QueryBuilder<T> builder = renderSearchEngine(parametersBuilder);
        renderListAllBuilder(builder);
        return doQuery(builder);
    }

    protected T newInstance(Long id) {
        Class<T> t = getBeanType();
        try {
            T bean = t.getConstructor(Long.class).newInstance(id);
            return bean;
        } catch (Exception e) {
            LOG.error("Can not create instance: " + t, e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 对外提供通过ID查询实体服务
     *
     * @param id
     * @return
     */
    public T get(long id) {
        T bean = newInstance(id);
        return doGet(bean);
    }

//    public T fastGet(Long id) {
//        if (id == null || id <= 0) return null;
//        T ent = cache.get(getBeanType(), id);
//        if (ent == null) {
//            ent = doGet(newInstance(id));
//        }
//        return ent;
//    }

    public T doGet(T bean) {
        return service.getExclude(bean);
    }

    /**
     * 获取实际参数类型
     *
     * @return
     */
    protected Class<T> getBeanType() {
        if (this.beanType != null) return beanType;
        beanType = (Class<T>) ClassUtils.getFirstParameterizedType(this.getClass());
        return beanType;
    }

    /**
     * 对外删除实体服务
     *
     * @param ids
     */
    public void delete(Long ids[]) {
        // 逐出缓存
        Query<T> query1 = QueryBuilder.custom(getBeanType()).andIn("id", ids).build();
        List<T> list = service.query(query1);

        Query<T> query2 = QueryBuilder.custom(getBeanType()).andIn("id", ids).build();
        service.delete(query2);
        if (!list.isEmpty()) {
            Entity es[] = new Entity[list.size()];
            list.toArray(es);
            fireEntityEvent(new EntityEvent(EntityEvent.EventType.DELETE, es)
            );
        }
    }

    /**
     * 处理真正的保存逻辑
     *
     * @param bean
     * @return
     */
    public long doSave(T bean) {
        service.save(bean);
        return (long) bean.getId();
    }

    /**
     * 处理真正的更新逻辑
     *
     * @param bean
     * @return
     */
    protected int doUpdate(T bean) {
        return service.update(bean);
    }

    /**
     * 处理对外的保存请求，标准的模板方法
     * <p>
     * 模板方法：调用{@link #validate(Entity, boolean)}} (PersistableEntity, boolean)} 和 {@link #doSave(Entity)} 方法
     * </p>
     *
     * @param bean
     */
    public T save(T bean) {
        validate(bean, false);
        long id = doSave(bean);
        LOG.info("Save bean done:id={},class={}", id, bean.getClass().getSimpleName());
        Assert.isTrue(id > 0, StatusCode.DAO_SAVE_FAIL);
        return bean;
    }

    /**
     * 处理对外的更新请求，标准的模板方法
     *
     * @param bean
     */
    public void update(T bean) {
        validate(bean, true);

        // 获取旧的实体
        T old = service.getInclude(bean);

        int ret = doUpdate(bean);
        LOG.info("Update bean done:ret={},id={},class={}", ret, bean.getClass().getSimpleName(), bean.getId());
        Assert.isTrue(ret >= 0, StatusCode.DAO_UPDATE_FAIL);

        // 发布更新事件
        fireEntityEvent(new EntityEvent(EntityEvent.EventType.UPDATE, bean, old));
    }

    /**
     * 设置操作人
     *
     * @param bean
     * @param update
     */
    protected void setOperator(Entity bean, boolean update) {
        if (bean instanceof EditableEntity) {
            SessionUser<User> user = sessionManager.getCurrentUser();
            if (user != null) {
                String username = user.getUsername();
                if (update) {
                    ((EditableEntity) bean).setCreator(null);
                } else {
                    ((EditableEntity) bean).setCreator(username);
                }
                ((EditableEntity) bean).setEditor(username);
            }
        }
    }


    public User getCurrentUser() {
        return sessionManager.getCurrentUser().getOriginUser();
    }

    /**
     * 填充搜索引擎参数，默认实现，如果有特殊情况，请自行实现
     */
    protected QueryBuilder<T> renderSearchEngine(ParametersBuilder parametersBuilder) {
        QueryBuilder<T> builder = QueryBuilder.custom(getBeanType());

        if (parametersBuilder == null) return builder;

        Map<String, Object> parameters = parametersBuilder.build();
        int invalid = 0;
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
                if (StringUtils.isBlank((String) value)) value = null;
            }

            if (value != null && isColumnExists(name)) {
                valid = true;
                switch (oper) {
                    case 1:
                        builder.andLike(name, "%" + value + "%");
                        break;
                    case 2:
                        builder.andLessThan(name, value, true);
                        break;
                    case 3:
                        builder.andGreaterThan(name, value, true);
                        break;
                    case 4:
                        builder.andGreaterThan(name, value);
                        break;
                    case 5:
                        builder.andLessThan(name, value);
                        break;
                    case 6:
                        builder.andNotEquivalent(name, value);
                        break;
                    case 7:
                        if (value instanceof Collection) {
                            Object[] objects = ((Collection) value).toArray();
                            builder.andIn(name, objects);
                        } else if (value instanceof Object[]) {
                            builder.andIn(name, (Object[]) value);
                        }
                        break;
                    default:
                        builder.andEquivalent(name, value);
                }
            } else {
                ++invalid;
                if (LOG.isDebugEnabled()) {
                    LOG.warn("Search field is ignored: name={}, value={}", name, value);
                }
            }
            if (parametersBuilder.isChainedDepend()) {
                if (valid && invalid > 0) throw new ServiceException("该查询条件必须遵守最左填充原则");
            }
        }

        String ef[] = parametersBuilder.getExcludeFields();
        if (ef != null && ef.length > 0) {
            builder.excludeFields(ef);
        }

        return builder;
    }

    /**
     * 检测一个字段列是否存在
     *
     * @param name
     * @return
     */
    private boolean isColumnExists(String name) {
        AnnotationHolder holder = AnnotationHelper.getAnnotationHolder(name, getBeanType());
        return holder != null;
    }

    /**
     * 子类可以实现查询逻辑
     *
     * @param builder
     * @return
     */
    public List<T> doQuery(QueryBuilder<T> builder) {
        return doQuery(builder.build());
    }

    /**
     * 子类可以实现查询逻辑
     *
     * @param query
     * @return
     */
    protected List<T> doQuery(Query<T> query) {
        return service.query(query);
    }

    /**
     * 执行实体检查操作
     *
     * @param entity
     */
    protected void validate(T entity, boolean update) {
        Assert.notNull(entity, "实体数据为空");
        doCheck(entity, update);
        named(entity, update);
        setOperator(entity, update);
    }

    /**
     * 执行参数逻辑校验检查工作
     *
     * @param entity
     */
    protected void doCheck(T entity, boolean update) {
    }

    protected void checkNamedEntity(T entity, boolean update) {
        if (entity instanceof NamedEntity) {
            NamedEntity ne = (NamedEntity) entity;
            Assert.notBlank(ne.getName(), "名称不能为空");
        }
    }

    protected AbstractBusiness<T> fireEntityEvent(EntityEvent event) {
        dispatcher.fireEntityEvent(event);
        return this;
    }

    @Override
    public void onEntityChange(EntityEvent event) {
        switch (event.type()) {
            case UPDATE:
            case DELETE:
                evict(event);
                break;
        }
    }

    protected void evict(EntityEvent event) {
        Entity es[] = event.entities();
        if (es != null && es.length > 0) {
            for (Entity e : es) {
                cache.evict(e);
            }
        }
    }

    @Override
    public boolean support(EntityEvent event) {
        Entity[] entities = event.entities();
        if (entities != null && entities.length > 0) {
            return getBeanType() == entities[0].getClass();
        }
        return false;
    }
}
