package org.example.demo.pixiu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @description:
 * @project: springboot-graalVM
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 18:17 Monday
 */
public abstract class NamedEntity<T extends Model<?>> extends Model<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 4857918608389216803L;

    @TableId(type = IdType.AUTO)
    protected Long id;

    @TableField
    protected String name;

    @TableField
    protected String extras;

    @TableField
    protected String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS",timezone = "GMT+8")
    @TableField
    protected Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS",timezone = "GMT+8")
    @TableField
    protected Date updateTime;

    @TableField
    protected String creator;

    @TableField
    protected String editor;

    /**
     * 0、未刪除 1、已刪除
     */
    @TableLogic
    @TableField(select = false)
    private Integer deleted;


    public NamedEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedEntity)) return false;
        NamedEntity<?> that = (NamedEntity<?>) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(extras, that.extras) &&
                Objects.equals(description, that.description) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(creator, that.creator) &&
                Objects.equals(editor, that.editor) &&
                Objects.equals(deleted, that.deleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, extras, description, createTime, updateTime, creator, editor, deleted);
    }

    @Override
    public String toString() {
        return "NamedEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", extras='" + extras + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", creator='" + creator + '\'' +
                ", editor='" + editor + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
