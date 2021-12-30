package org.example.demo.base;

public @interface Dependency {

    Class<? extends DataController> type();

    String method();
}
