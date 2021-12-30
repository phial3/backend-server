package org.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.phial.mybatisx.api.annotation.Column;
import org.phial.mybatisx.api.annotation.Index;
import org.phial.mybatisx.api.annotation.IndexColumn;
import org.phial.mybatisx.api.annotation.Table;
import org.phial.mybatisx.api.entity.LongEditableEntity;
import org.phial.mybatisx.api.enums.DataType;
import org.phial.mybatisx.api.enums.IndexType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单
 * @since 2021/4/8
 * @author mayanjun
 */
@Table(value = "t_menu",
        indexes = {
                @Index(value = "idx_name", columns = @IndexColumn("name")),
                @Index(value = "idx_menu_code", columns = @IndexColumn("menuCode"),type = IndexType.UNIQUE)
        },
        comment = "菜单")
public class Menu extends LongEditableEntity implements Comparable {

    @Column(length = "32")
    private String name;

    @Column(length = "32")
    private MenuType type;

    @Column(length = "32")
    private String target;

    @Column(length = "200")
    private String href;

    @Column(length = "256")
    private String icon;

    @Column(type = DataType.BIGINT)
    private Long parentId;

    @Column(comment = "菜单顺序", type = DataType.DOUBLE, length = "10,5")
    private Double order;

    @Column(comment = "备注", type = DataType.VARCHAR, length = "500")
    private String description;

    @Column(comment = "状态 1已上线，0待上线", type = DataType.TINYINT,defaultValue = "1")
    private Integer status;

    @Column(comment = "菜单ID路径", type = DataType.VARCHAR,length = "128")
    private String menuIdPath;

    @Column(comment = "菜单code", type = DataType.VARCHAR,length = "128")
    private String menuCode;

    @Column(type = DataType.TINYINT, comment = "所属系统，1管理端，2门店端，3共享权限")
    @JsonIgnore
    private Integer system;

    @JsonProperty("system")
    private String systemStr;

    @Column(comment = "打开方式", type = DataType.VARCHAR,length = "32")
    private String openType;

    private String operateCode;

    private SortedSet<Menu> children;


    public Menu() {
    }

    public Menu(Long id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuType getType() {
        return type;
    }

    public void setType(MenuType type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Override
    public int compareTo(Object o) {
        Menu menu = (Menu) o;
        double thisOrder = this.order == null ? 0 : this.order;
        double thatOrder = menu.getOrder() == null ? 0 : menu.getOrder();
        return thisOrder < thatOrder ? -1 : 1;
    }

    public SortedSet<Menu> getChildren() {
        return children;
    }

    public void setChildren(SortedSet<Menu> children) {
        this.children = children;
    }

    public static enum MenuType {
        LINK,
        SEPARATOR,
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMenuIdPath() {
        return menuIdPath;
    }

    public void setMenuIdPath(String menuIdPath) {
        this.menuIdPath = menuIdPath;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public Double getOrder() {
        return order;
    }

    public void setOrder(Double order) {
        this.order = order;
    }


    public String getOperateCode() {
        return operateCode;
    }

    public void setOperateCode(String operateCode) {
        this.operateCode = operateCode;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    /**
     * 将一维菜单列表结构化处理
     * @param list 菜单列表
     * @return 结构化的菜单列表
     */
    public static List<Menu> hierarchicalMenus(Collection<Menu> list) {
        Map<Long, Menu> menuMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            Iterator<Menu> it = list.iterator();
            while (it.hasNext()) {
                Menu e = it.next();
                if (StringUtils.isBlank(e.getIcon())) e.setIcon("el-icon-menu");
                Long pid = e.getParentId();
                if (pid == null || pid == 0) {
                    menuMap.put(e.getId(), e);
                    it.remove();
                } else {
                    Menu p = menuMap.get(pid);
                    if (p != null) {
                        SortedSet<Menu> children = p.getChildren();
                        if (children == null) {
                            children = new TreeSet<>();
                            p.setChildren(children);
                        }
                        children.add(e);
                        it.remove();
                    }
                }
            }

            // 一轮循环结束后可能有的子菜单还没有被处理
            if (!list.isEmpty()) {
                for (Menu e : list) {
                    Long pid = e.getParentId();
                    Menu p = menuMap.get(pid);
                    if (p == null) {
                        menuMap.put(e.getId(), e);
                    } else {
                        SortedSet<Menu> children = p.getChildren();
                        if (children == null) {
                            children = new TreeSet<>();
                            p.setChildren(children);
                        }
                        children.add(e);
                    }
                }
            }
        }

        if (menuMap.isEmpty()) {
            return new ArrayList<>();
        } else {
            return menuMap.values().stream()
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    @Override
    public String toString() {
        return "Menu{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", target='" + target + '\'' +
                ", href='" + href + '\'' +
                ", icon='" + icon + '\'' +
                ", parentId=" + parentId +
                ", order=" + order +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", menuIdPath='" + menuIdPath + '\'' +
                ", menuCode='" + menuCode + '\'' +
                ", system=" + system +
                ", systemStr='" + systemStr + '\'' +
                ", openType='" + openType + '\'' +
                ", operateCode='" + operateCode + '\'' +
                ", children=" + children +
                '}';
    }
}