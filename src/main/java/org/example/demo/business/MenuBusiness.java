package org.example.demo.business;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.example.demo.base.AbstractBusiness;
import org.example.demo.base.ParametersBuilder;
import org.example.demo.base.StatusCode;
import org.example.demo.entity.Menu;
import org.example.demo.utils.SysCommonUtils;
import org.phial.mybatisx.api.query.QueryBuilder;
import org.phial.mybatisx.common.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 菜单管理
 *
 * @author phial
 * @vendor JDD (https://www.jddglobal.com)
 * @since 2019-07-06
 */
@Component
public class MenuBusiness extends AbstractBusiness<Menu> {

    private static final String privilege_prefix = "privilege_";
    private static final String menu_prefix = "menu_";
    private static final Logger LOG = LoggerFactory.getLogger(MenuBusiness.class);
    private static final Cache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(12, TimeUnit.HOURS)
            .build();

    //    @Autowired
    //    private RedisCacheClient redisCacheClient;

    @Override
    protected void doCheck(Menu entity, boolean update) {
        Assert.notBlank(entity.getName(), "菜单名称不能为空");
    }

    @Override
    public Menu save(Menu menu) {
        menu = super.save(menu);
        //更新menuIdPath
        this.update(menu);
        return menu;
    }

    @Override
    public void update(Menu menu) {
        validate(menu, true);
        if (menu.getParentId() == 0) {
            menu.setMenuIdPath(SysCommonUtils.encodePath(Lists.newArrayList(0L, menu.getId())));
        } else {
            LOG.info("MenuBusiness.save,menu={}", menu);
            Menu pmenu = new Menu();
            pmenu.setId(menu.getParentId());
            pmenu = this.doGet(pmenu);
            Assert.notNull(pmenu, "菜单不存在");

            menu.setMenuIdPath(pmenu.getMenuIdPath() + ",[" + menu.getId() + "]");
        }
        int ret = doUpdate(menu);
        LOG.info("Update bean done:ret={},id={},class={}", ret, menu.getClass().getSimpleName(), menu.getId());
        Assert.isTrue(ret > 0, StatusCode.DAO_UPDATE_FAIL);
    }

    @Override
    public List<Menu> listAll(ParametersBuilder parametersBuilder) {
        List<Menu> list = super.listAll(parametersBuilder);
        return Menu.hierarchicalMenus(list);
    }


    @Override
    public void delete(Long[] ids) {
        transaction().execute(transactionStatus -> {
            super.delete(ids);
            for (Long id : ids) {
                service.delete(QueryBuilder.custom(Menu.class)
                        .andEquivalent("parentId", id)
                        .build()
                );
            }
            return true;
        });
    }

    public Menu getByCode(String code) {
        QueryBuilder<Menu> queryBuilder = QueryBuilder.custom(Menu.class);
        queryBuilder.andEquivalent("menuCode", code);
        List<Menu> menus = doQuery(queryBuilder);
        return CollectionUtils.isEmpty(menus) ? null : menus.get(0);
    }

}
