package org.example.demo.base;

import java.io.Serializable;

/**
 * @description:
 * @project: backend-server
 * @author: gaoyanfei3
 * @datetime: 2021/12/29 07:05 Wednesday
 */
public interface Entity<P extends Serializable> extends Serializable {
    P getId();

    void setId(P var);
}
