package com.hzoom.core.filter.annotation;

import com.hzoom.core.filter.EnableFilterImportSelector;
import com.hzoom.core.filter.enums.FilterAutoConfigurationEnum;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableFilterImportSelector.class)
public @interface EnableFilter {
    FilterAutoConfigurationEnum[] value();
}
