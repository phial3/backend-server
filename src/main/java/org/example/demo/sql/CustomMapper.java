package org.example.demo.sql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * 自定义SQL的 Mapper :
 * https://www.jianshu.com/p/76672124ca67
 * @since 2019-07-06
 * @author phial
 * @vendor
 */
@Mapper
public interface CustomMapper {
    @UpdateProvider(type = CustomSQLBuilder.class, method = "databaseDDL")
    void generateDatabase();
}
