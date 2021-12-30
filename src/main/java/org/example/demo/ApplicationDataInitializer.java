package org.example.demo;

import org.assertj.core.util.Lists;
import org.example.demo.base.*;
import org.example.demo.business.AttributeBusiness;
import org.example.demo.entity.Menu;
import org.example.demo.entity.Privilege;
import org.example.demo.entity.User;
import org.example.demo.sql.CustomMapper;
import org.example.demo.utils.SysCommonUtils;
import org.phial.mybatisx.api.query.Query;
import org.phial.mybatisx.api.query.QueryBuilder;
import org.phial.mybatisx.common.utils.CommonUtils;
import org.phial.mybatisx.dal.dao.BasicDAO;
import org.phial.rest.common.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统初始化。初始化用户、菜单、权限等信息
 *
 * @author
 * @vendor
 * @since 2019-07-06
 */
@Component("ApplicationDataInitializer")
public class ApplicationDataInitializer implements ApplicationContextAware, BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDataInitializer.class);

    public static final String INITIALIZER_USERNAME = "SYSTEM";
    private static Map<String, PrivilegeMetaData> PRIVILEGE_METADATA = new HashMap<>();

    @Resource
    private BasicDAO dao;

    @Resource
    private SessionManager sessionManager;

    @Resource
    private AttributeBusiness attributeBusiness;

    private ApplicationContext applicationContext;

    private static final String MENUS[][] = new String[][]{
            // #### children-count, name, icon, url, id
            new String[]{"6", "系统管理", "el-icon-s-operation", "", "privilege_sys_manage_code"},
            new String[]{"0", "菜单管理", "el-icon-menu", "/pages/menu/list", "privilege_menu_manage_code"},
            new String[]{"0", "用户管理", "el-icon-user", "/pages/user/list", "privilege_user_manage_code"},
            new String[]{"0", "角色管理", "el-icon-present", "/pages/role/list", "privilege_role_manage_code"},
            new String[]{"0", "权限管理", "el-icon-c-scale-to-original", "/pages/privilege/list", "privilege_manage_code"},
            new String[]{"0", "访问日志", "el-icon-document", "/pages/access-log/list", "privilege_log_manage_code"},
            new String[]{"0", "系统设置", "el-icon-setting", "/pages/settings/add", "privilege_settings_manage_code"},
            // ####
    };

    @PostConstruct
    public void init() {
        try {
            synchronized (this) {
                initDirs();
                initDatabase();
                initSystemUser();
                initMenus();
                initPrivileges();
                attributeBusiness.initSystemSettings(false);
                LOG.info("SYSTEM initialized!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, PrivilegeMetaData> privilegeMetaDataMap() {
        return PRIVILEGE_METADATA;
    }

    private void initDirs() {
        // nothing to do
    }

    private void initDatabase() throws Exception {
        CustomMapper mapper = dao.databaseRouter().getDatabaseSession().getMapper(CustomMapper.class);
        mapper.generateDatabase();
    }

    private void addMethodsToSet(Method[] methods, Set<Method> methodSet) {
        if (methods != null && methods.length > 0) {
            methodSet.addAll(Arrays.asList(methods));
        }
    }

    /**
     * 初始化权限数据
     */
    private void initPrivileges() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(PrivilegedMeta.class);

        Map<String, PrivilegeMetaData> privilegeMetaDataMap = new HashMap<>();

        if (beans != null && !beans.isEmpty()) {
            beans.values().forEach(e -> {
                Class<?> cls = e.getClass();

                if (AopUtils.isAopProxy(e)) {
                    cls = AopUtils.getTargetClass(e);
                }

                Set<Method> methodSet = new HashSet<>();
                Method[] ms = cls.getMethods();
                Method[] dms = cls.getDeclaredMethods();
                addMethodsToSet(ms, methodSet);
                addMethodsToSet(dms, methodSet);
                for (Method m : methodSet) {
                    PrivilegeMetaData pmd = createPrivilegeMetaData(cls, m);
                    if (pmd != null) {
                        privilegeMetaDataMap.put(pmd.methodName, pmd);
                    }
                }
            });

            // check the integrality of dependencies
            if (!privilegeMetaDataMap.isEmpty()) {

                privilegeMetaDataMap.forEach((key, value) -> {
                    Dependency[] ds = value.dependencies;
                    if (ds != null && ds.length > 0) {
                        for (Dependency mn : ds) {
                            PrivilegeMetaData pmd = privilegeMetaDataMap.get(SysCommonUtils.getReferenceMethodName(mn));
                            if (pmd == null) {
                                throw new NullPointerException(
                                        String.format("The dependency not found: defined in [%s], value=%s", value.method, mn)
                                );
                            }
                        }
                    }
                });

                // save privileges, 这里还必须处理间接依赖和循环依赖的问题
                privilegeMetaDataMap.forEach((key, data) -> {
                    if (data.dependencies.length > 0) {
                        Set<String> des = new HashSet<>();
                        determineDependencies(des, data.methodName, privilegeMetaDataMap);
                        des.remove(data.methodName);

                        String dependenciesString = commaSeparated(des);
                        savePrivilege(data, dependenciesString);

                    } else { // 没有依赖直接保存
                        savePrivilege(data, null);
                    }
                });

                PRIVILEGE_METADATA = Collections.unmodifiableMap(privilegeMetaDataMap);
            }
        } else {
            LOG.warn("No privileged bean found!");
        }
    }

    private void savePrivilege(PrivilegeMetaData data, String des) {
        Privilege privilege = new Privilege();
        privilege.setDependencies(des == null ? "" : des);
        privilege.setDescription(data.description);
        privilege.setMethod(data.methodName);
        privilege.setName(data.name);

        Query<Privilege> query = QueryBuilder.custom(Privilege.class)
                .andEquivalent("method", data.methodName)
                .includeFields("id")
                .build();
        Privilege dbp = dao.queryOne(query);
        if (dbp == null) {
            long id = dao.save(privilege);
            LOG.info("Privilege saved: {} <===> {}", id, data.methodName);
        } else {
            privilege.setId(dbp.getId());
            //  dao.update(privilege);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Privilege updated: {} <===> {}", dbp.getId(), data.methodName);
            }
        }
    }

    private String commaSeparated(Set<String> set) {
        int size = set.size();
        int count = 1;
        StringBuffer sb = new StringBuffer();
        for (String s : set) {
            sb.append(s);
            if (count++ < size) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 检测依赖与循环依赖
     *
     * @param des
     * @param mn
     * @param map
     */
    private void determineDependencies(Set<String> des, String mn, Map<String, PrivilegeMetaData> map) {
        des.add(mn);
        PrivilegeMetaData data = map.get(mn);
        if (data.dependencies.length == 0) return;

        for (Dependency de : data.dependencies) {
            String decy = SysCommonUtils.getReferenceMethodName(de);
            if (des.contains(decy)) continue;
            determineDependencies(des, decy, map);
        }
    }


    private PrivilegeMetaData createPrivilegeMetaData(Class<?> cls, Method m) {
        Privileged privileged = m.getAnnotation(Privileged.class);
        if (privileged != null) {
            PrivilegedMeta meta = cls.getAnnotation(PrivilegedMeta.class);
            MetaProperty[] metaProperties = meta.value();

            PrivilegeMetaData pmd = new PrivilegeMetaData(cls, m,
                    CommonUtils.getReferenceMethodName(cls, m),
                    privileged.value(),
                    "System created",
                    privileged.dependencies()
            );
            replacePlaceholder(pmd, metaProperties);
            return pmd;
        }
        return null;
    }

    private void replacePlaceholder(PrivilegeMetaData pmd, MetaProperty[] metaProperties) {
        Map<String, String> map = new HashMap<>();
        for (MetaProperty mp : metaProperties) {
            map.put(mp.name(), mp.value());
        }
        pmd.name = doReplacePlaceholder(pmd.name, map, pmd);
    }

    private String doReplacePlaceholder(String src, Map<String, String> dict, PrivilegeMetaData data) {
        Pattern pattern = Pattern.compile("\\{([0-9,a-z,A-Z]+)\\}");
        Matcher matcher = pattern.matcher(src);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String groupName = matcher.group(1);
            String value = dict.get(groupName);
            if (value != null) {
                matcher.appendReplacement(sb, value);
            } else {
                if ("thisClass".equals(groupName)) {
                    matcher.appendReplacement(sb, data.cls.getName());
                }
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static class PrivilegeMetaData {
        private String methodName;
        private String name;
        private String description;
        private Dependency[] dependencies;
        private Class<?> cls;
        private Method method;

        public PrivilegeMetaData(Class<?> cls, Method method,
                                 String methodName, String name, String description, Dependency[] dependencies) {
            this.cls = cls;
            this.method = method;
            this.methodName = methodName;
            this.name = name;
            this.description = description;
            this.dependencies = dependencies;
        }

        public PrivilegeMetaData(String methodName, Dependency[] dependencies) {
            this.methodName = methodName;
            this.dependencies = dependencies;
        }

        /**
         * 获取 methodName
         *
         * @return methodName
         */
        public String getMethodName() {
            return methodName;
        }

        /**
         * 获取 name
         *
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * 获取 description
         *
         * @return description
         */
        public String getDescription() {
            return description;
        }

        /**
         * 获取 dependencies
         *
         * @return dependencies
         */
        public Dependency[] getDependencies() {
            return dependencies;
        }

        /**
         * 获取 cls
         *
         * @return cls
         */
        public Class<?> getCls() {
            return cls;
        }

        /**
         * 获取 method
         *
         * @return method
         */
        public Method getMethod() {
            return method;
        }
    }

    private void initSystemUser() {
        // create default user
        User user = dao.queryOne(QueryBuilder.custom(User.class)
                .andEquivalent(User::getUsername, "admin")
                .build());

        if (user == null) {
            user = new User();
            user.setAdministrator(true);
            user.setUsername("admin");
            user.setDescription("System init user");
            String password = generatePassword();
            user.setSecretKey(Strings.secretKey(32));
            user.setPassword(sessionManager.encryptPassword(password));
            user.setCreator(INITIALIZER_USERNAME);
            user.setEditor(INITIALIZER_USERNAME);
            user.setEnabled(true);

            int ret = dao.save(user);
            LOG.info("System init user created({}), init password={}", ret, password);
        }
    }

    private String generatePassword() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        int start = Double.valueOf(Math.random() * (uuid.length() - 10)).intValue();
        return uuid.substring(start, start + 10);
    }

    private AtomicLong idGen = new AtomicLong(10000);

    /**
     * 初始化菜单
     */
    private void initMenus() {
        int children = 0;
        long pid = 0;

        for (String menuitem[] : MENUS) {
            long parentId = 0;
            long id = idGen.incrementAndGet();

            if (--children >= 0) { // 子节点
                parentId = pid;
            } else {  // 处理完了子节点
                children = Integer.parseInt(menuitem[0]);
                if (children > 0) {
                    pid = id;
                }
            }
            Menu menu = dao.getInclude(new Menu(id), "id");
            if (menu == null) {
                createMenu(id, parentId, menuitem[1], menuitem[2], menuitem[3], menuitem[4]);
            } else {
                order.incrementAndGet();
            }
        }
    }

    private AtomicInteger order = new AtomicInteger(10000);

    /**
     * @param id   菜单ID
     * @param pid  父ID
     * @param name 菜单名称
     * @param icon 菜单图标 参见 https://element.eleme.cn/#/zh-CN/component/icon
     * @param url  菜单URL
     */
    private void createMenu(long id, long pid, String name, String icon, String url, String menuCode) {
        Menu m = new Menu();
        m.setId(id);
        m.setParentId(pid);
        if (pid == 0) {
            m.setMenuIdPath(SysCommonUtils.encodePath(Lists.list(pid, id)));
        } else {
            m.setMenuIdPath(SysCommonUtils.encodePath(Lists.list(0L, pid, id)));
        }
        m.setMenuCode(menuCode);
        m.setName(name);
        m.setIcon(icon);
        m.setDescription("Generated Menu");
        m.setHref(url);
        m.setOrder((double) order.incrementAndGet());
        m.setType(Menu.MenuType.LINK);
        m.setCreator(INITIALIZER_USERNAME);
        m.setEditor(INITIALIZER_USERNAME);
        long sid = dao.save(m);
        LOG.info("Menu created: id={}, name={}", sid, name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
