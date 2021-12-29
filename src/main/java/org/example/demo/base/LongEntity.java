package org.example.demo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description:
 * @project: backend-server
 * @author: gaoyanfei3
 * @datetime: 2021/12/29 15:05 Wednesday
 */
public abstract class LongEntity implements Entity<Long> {

    @TableId(type = IdType.AUTO)
    private Long id;

    public LongEntity() {
    }

    public LongEntity(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
