package org.example.demo.pixiu.base;

/**
 * @description:
 * @project: backend-server
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 22:19 Monday
 */
public enum SortDirection {
    /**
     *
     */
    ASC,
    DESC;

    private SortDirection() {
    }

    public static boolean isAsc(SortDirection direction) {
        return direction == ASC;
    }
}
