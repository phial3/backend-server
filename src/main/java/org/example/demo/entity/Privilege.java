package org.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.phial.mybatisx.api.annotation.Column;
import org.phial.mybatisx.api.annotation.Index;
import org.phial.mybatisx.api.annotation.IndexColumn;
import org.phial.mybatisx.api.annotation.Table;
import org.phial.mybatisx.api.entity.LongEditableEntity;
import org.phial.mybatisx.api.enums.DataType;
import org.phial.mybatisx.api.enums.IndexType;

/**
 * 权限
 *
 * @author phial
 * @since 2021/4/8
 */
@Table(value = "t_privilege",
        indexes = {
                @Index(value = "idx_name", columns = @IndexColumn("name")),
                @Index(value = "idx_method", columns = @IndexColumn("method")),
                @Index(value = "idx_privilegeCode", columns = @IndexColumn("privilegeCode"), type = IndexType.UNIQUE),

        },
        comment = "权限")
public class Privilege extends LongEditableEntity {

    @Column(length = "32")
    private String name;

    @Column(length = "100", comment = "操作标识")
    private String method;

    @Column(length = "1000", comment = "依赖")
    private String dependencies;

    @Column(length = "500")
    private String description;

    @Column(length = "64", comment = "菜单ID树[],分割")
    private String menuIdPath;

    @Column(comment = "菜单ID")
    private Long menuId;

    @Column(length = "256", comment = "唯一权限code")
    private String privilegeCode;

    @Column(length = "32", comment = "返回前端操作码")
    private String operateCode;

    @Column(type = DataType.TINYINT, comment = "权限类型 0操作权限，1字段权限")
    private Integer privilegeType;

    @Column(type = DataType.TINYINT, comment = "所属系统，1管理端，2门店端，3共享权限")
    @JsonIgnore
    private Integer system;

    @JsonProperty("system")
    private String systemStr;


    public Privilege() {
    }

    public Privilege(Long id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public String getMenuIdPath() {
        return menuIdPath;
    }

    public void setMenuIdPath(String menuIdPath) {
        this.menuIdPath = menuIdPath;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getPrivilegeCode() {
        return privilegeCode;
    }

    public void setPrivilegeCode(String privilegeCode) {
        this.privilegeCode = privilegeCode;
    }

    public Integer getPrivilegeType() {
        return privilegeType;
    }

    public void setPrivilegeType(Integer privilegeType) {
        this.privilegeType = privilegeType;
    }

    public String getOperateCode() {
        return operateCode;
    }

    public void setOperateCode(String operateCode) {
        this.operateCode = operateCode;
    }


    @Override
    public int hashCode() {
        Long id = getId();
        if (id == null) return System.identityHashCode(this);
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Privilege) {
            Long thisId = getId();
            Long thatId = ((Privilege) obj).getId();
            if (thisId == null || thatId == null) {
                return System.identityHashCode(this) == System.identityHashCode(obj);
            } else {
                return thisId.equals(thatId);
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Privilege{" +
                "name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", dependencies='" + dependencies + '\'' +
                ", description='" + description + '\'' +
                ", menuIdPath='" + menuIdPath + '\'' +
                ", menuId=" + menuId +
                ", privilegeCode='" + privilegeCode + '\'' +
                ", operateCode='" + operateCode + '\'' +
                ", privilegeType=" + privilegeType +
                ", system=" + system +
                '}';
    }

}
