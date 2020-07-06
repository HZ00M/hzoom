package com.example.demo.filter.annotation;

import com.example.demo.filter.EnableFilterImportSelector;
import com.example.demo.filter.enums.FilterAutoConfigurationEnum;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableFilterImportSelector.class)
public @interface EnableFilter {
    FilterAutoConfigurationEnum[] value();
}
