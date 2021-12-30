package org.example.demo.business;

import org.example.demo.base.AbstractBusiness;
import org.example.demo.entity.Menu;
import org.example.demo.entity.Privilege;
import org.phial.mybatisx.api.query.QueryBuilder;
import org.phial.mybatisx.common.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 权限管理
 *
 * @author mayanjun
 * @vendor JDD (https://www.jddglobal.com)
 * @since 2019-07-06
 */
@Component
public class PrivilegeBusiness extends AbstractBusiness<Privilege> {

    private static final Logger LOG = LoggerFactory.getLogger(PrivilegeBusiness.class);

    @Autowired
    private MenuBusiness menuBusiness;

    @Override
    protected void doCheck(Privilege entity, boolean update) {
        Assert.notBlank(entity.getName(), "角色名称不能为空");
    }

    @Override
    protected void renderListAllBuilder(QueryBuilder<Privilege> builder) {
        builder.excludeFields("method", "dependencies");
    }

    @Override
    public Privilege save(Privilege privilege) {
        LOG.info("PrivilegeBusiness.save,privilege={}", privilege);
        Assert.notNull(privilege.getMenuId(), "对应菜单id不能为bull");
        Menu menu = new Menu();
        menu.setId(privilege.getMenuId());
        menu = menuBusiness.doGet(menu);
        Assert.notNull(menu, "菜单不存在");
        privilege.setMenuIdPath(menu.getMenuIdPath());
        privilege.setCreateTime(new Date());
        privilege.setUpdateTime(new Date());
        return super.save(privilege);
    }

    @Override
    public void update(Privilege privilege) {
        LOG.info("PrivilegeBusiness.update,privilege={}", privilege);
        Menu menu = new Menu();
        menu.setId(privilege.getMenuId());
        menu = menuBusiness.doGet(menu);
        privilege.setMenuIdPath(menu.getMenuIdPath());
        privilege.setUpdateTime(new Date());
        super.update(privilege);
        return;
    }
}
