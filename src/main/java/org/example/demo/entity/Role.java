package org.example.demo.entity;

import org.example.demo.base.NamedEntity;

import java.io.Serial;

/**
 * @description:
 * @project: backend-sever
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:36 Monday
 */
public final class Role extends NamedEntity {

    @Serial
    private static final long serialVersionUID = -4173535503189314896L;

    public Role() {
        super();
    }

    public Role(Long id) {
        super(id);
    }

}
