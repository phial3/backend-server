package org.example.demo.entity;

import org.phial.mybatisx.api.annotation.Index;
import org.phial.mybatisx.api.annotation.IndexColumn;
import org.phial.mybatisx.api.annotation.Table;
import org.phial.mybatisx.api.entity.NamedEntity;

import java.io.Serial;

/**
 * @description:
 * @project: backend-sever
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:36 Monday
 */
@Table(value = "t_role",
        indexes = {
                @Index(value = "idx_name", columns = @IndexColumn("name"))
        },
        comment = "角色")
public class Role extends NamedEntity {

    @Serial
    private static final long serialVersionUID = -4173535503189314896L;

    public Role() {
        super();
    }

    public Role(Long id) {
        super(id);
    }

    public Role(Long id, String name) {
        super(id,name);
    }

}
