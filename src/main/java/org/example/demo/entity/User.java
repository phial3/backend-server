package org.example.demo.entity;

import org.example.demo.base.AbstractUser;
import org.phial.mybatisx.api.annotation.Column;
import org.phial.mybatisx.api.annotation.Index;
import org.phial.mybatisx.api.annotation.IndexColumn;
import org.phial.mybatisx.api.annotation.Table;
import org.phial.mybatisx.api.entity.NamedEntity;
import org.phial.mybatisx.api.enums.DataType;
import org.phial.mybatisx.api.enums.IndexType;

import java.io.Serial;

/**
 * @description:
 * @project: backend-sever
 * @datetime: 2021/12/27 17:50 Monday
 */
@Table(value = "t_user",
        indexes = {
                @Index(value = "idx_username", columns = @IndexColumn(value = "username", length = 32), type = IndexType.UNIQUE)
        },
        comment = "用户")
public class User extends AbstractUser {

    @Serial
    private static final long serialVersionUID = -2335190050160138088L;

    @Column(comment = "是否管理员", type = DataType.BIT, length = "1")
    private Boolean administrator;

    public User() {
    }

    public User(Long id) {
        super(id);
    }

    public User(String username) {
        super(username);
    }

    public User(Long id, String username) {
        super(id, username);
    }

    public Boolean getAdministrator() {
        return administrator;
    }

    public void setAdministrator(Boolean administrator) {
        this.administrator = administrator;
    }
}
