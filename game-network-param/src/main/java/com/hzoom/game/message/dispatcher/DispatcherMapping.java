package com.hzoom.game.message.dispatcher;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

@AllArgsConstructor
@Data
public class DispatcherMapping {
    private Object targetClass;
    private Method targetMethod;
}
