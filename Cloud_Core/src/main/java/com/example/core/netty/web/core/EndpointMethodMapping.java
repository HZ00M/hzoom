package com.example.core.netty.web.core;

import com.example.core.netty.web.annotation.ServerListener;
import com.example.core.netty.web.enums.ListenerType;
import com.example.core.netty.web.support.MethodArgumentResolver;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;

import javax.websocket.DeploymentException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class EndpointMethodMapping {
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final Class endpointClazz;
    private final ApplicationContext applicationContext;
    private final AbstractBeanFactory beanFactory;
    private final Map<ListenerType, MethodMapping> methodMap;

    public EndpointMethodMapping(Class<?> endpointClazz, ApplicationContext context, AbstractBeanFactory beanFactory) throws DeploymentException {
        this.applicationContext = context;
        this.endpointClazz = endpointClazz;
        this.beanFactory = beanFactory;
        methodMap = new HashMap<ListenerType, MethodMapping>();
        Class<?> currentClazz = endpointClazz;
        Method[] endpointClazzMethods = null;
        while (!currentClazz.equals(Object.class)) {
            Method[] currentClazzMethods = currentClazz.getDeclaredMethods();
            if (currentClazz == endpointClazz) {
                endpointClazzMethods = currentClazzMethods;
            }
            for (Method method : currentClazzMethods) {
                ServerListener serverListener = method.getAnnotation(ServerListener.class);
                if (serverListener != null) {
                    checkPublic(method);
                    MethodMapping methodMapping = new MethodMapping(method);
                    methodMap.putIfAbsent(serverListener.value(), methodMapping);
                    currentClazz = currentClazz.getSuperclass();
                }
            }
        }
    }

    Object getEndpointInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object implement = endpointClazz.getDeclaredConstructor().newInstance();
        AutowiredAnnotationBeanPostProcessor postProcessor = applicationContext.getBean(AutowiredAnnotationBeanPostProcessor.class);
        postProcessor.postProcessPropertyValues(null, null, implement, null);
        return implement;
    }

    private void checkPublic(Method m) throws DeploymentException {
        if (!Modifier.isPublic(m.getModifiers())) {
            throw new DeploymentException(
                    "EndpointMethodMapping.methodNotPublic " + m.getName());
        }
    }

    private boolean isMethodOverride(Method method1, Method method2) {
        return (method1.getName().equals(method2.getName())
                && method1.getReturnType().equals(method2.getReturnType())
                && Arrays.equals(method1.getParameterTypes(), method2.getParameterTypes()));
    }

    private boolean isOverrideWithoutAnnotation(Method[] methods, Method superClazzMethod, Class<? extends Annotation> annotation) {
        for (Method method : methods) {
            if (isMethodOverride(method, superClazzMethod)
                    && (method.getAnnotation(annotation) == null)) {
                return true;
            }
        }
        return false;
    }

    @AllArgsConstructor
    @Getter
    public class MethodMapping {
        private Method method;
        private MethodParameter[] methodParameters;
        private MethodArgumentResolver[] methodArgumentResolvers;

        public MethodMapping(Method method) throws DeploymentException {
            this.method = method;
            this.methodParameters = getParameters(method);
            this.methodArgumentResolvers = getResolvers(methodParameters);
        }

        private MethodParameter[] getParameters(Method m) {
            if (m == null) {
                return new MethodParameter[0];
            }
            int count = m.getParameterCount();
            MethodParameter[] result = new MethodParameter[count];
            for (int i = 0; i < count; i++) {
                MethodParameter methodParameter = new MethodParameter(m, i);
                methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
                result[i] = methodParameter;
            }
            return result;
        }

        private MethodArgumentResolver[] getResolvers(MethodParameter[] parameters) throws DeploymentException {
            MethodArgumentResolver[] methodArgumentResolvers = new MethodArgumentResolver[parameters.length];
            List<MethodArgumentResolver> resolvers = getDefaultResolvers();
            for (int i = 0; i < parameters.length; i++) {
                MethodParameter parameter = parameters[i];
                for (MethodArgumentResolver resolver : resolvers) {
                    if (resolver.supportsParameter(parameter)) {
                        methodArgumentResolvers[i] = resolver;
                        break;
                    }
                }
                if (methodArgumentResolvers[i] == null) {
                    throw new DeploymentException("pojoMethodMapping.paramClassIncorrect parameter name : " + parameter.getParameterName());
                }
            }
            return methodArgumentResolvers;
        }

        public Object[] getMethodArgs(Channel channel, FullHttpRequest req) throws Exception {
            Object[] objects = new Object[methodParameters.length];
            for (int i = 0; i < methodParameters.length; i++) {
                MethodParameter parameter = methodParameters[i];
                MethodArgumentResolver resolver = methodArgumentResolvers[i];
                Object arg = resolver.resolveArgument(parameter, channel, req);
                objects[i] = arg;
            }
            return objects;
        }




        private List<MethodArgumentResolver> getDefaultResolvers() {
            List<MethodArgumentResolver> resolvers = new ArrayList<>();
//        resolvers.add(new SessionMethodArgumentResolver());
//        resolvers.add(new HttpHeadersMethodArgumentResolver());
//        resolvers.add(new TextMethodArgumentResolver());
//        resolvers.add(new ThrowableMethodArgumentResolver());
//        resolvers.add(new ByteMethodArgumentResolver());
//        resolvers.add(new RequestParamMapMethodArgumentResolver());
//        resolvers.add(new RequestParamMethodArgumentResolver(beanFactory));
//        resolvers.add(new PathVariableMapMethodArgumentResolver());
//        resolvers.add(new PathVariableMethodArgumentResolver(beanFactory));
//        resolvers.add(new EventMethodArgumentResolver(beanFactory));
            return resolvers;
        }
    }
}
