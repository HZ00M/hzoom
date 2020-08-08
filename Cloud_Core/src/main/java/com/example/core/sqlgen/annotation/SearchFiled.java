package com.example.core.sqlgen.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SearchFiled {
    String[] value() default "*";
}
