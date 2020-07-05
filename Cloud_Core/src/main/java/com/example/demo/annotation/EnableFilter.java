package com.example.demo.annotation;

import com.example.demo.config.EnableFilterImportSelector;
import com.example.demo.enums.FilterEnum;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableFilterImportSelector.class)
public @interface EnableFilter {
    FilterEnum[] value();
}
