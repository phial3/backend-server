package org.example.demo.base;

/**
 * @description:
 * @project: backend-server
 * @author: gaoyanfei3
 * @datetime: 2021/12/27 22:44 Monday
 */
public class PageResult {
    public static String limit(int pageNo, int pageSize) {
        if (pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize < 1) {
            pageSize = 20;
        }
        int offset = pageSize * (pageNo - 1);
        // int totalPage = Math.ceil(total / pageSize) + 1;
        return offset + "," + pageSize;
    }

}
