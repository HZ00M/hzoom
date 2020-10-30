package com.example.core.netty.web.endpoint;

import com.example.core.netty.web.annotation.ServerEndpoint;
import com.example.core.netty.web.core.WebSocketServer;
import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;

import javax.websocket.DeploymentException;
import java.net.InetSocketAddress;
import java.util.*;

@Slf4j
public class EndpointRegistrar extends ApplicationObjectSupport implements SmartInitializingSingleton, BeanFactoryAware {
    @Autowired
    Environment environment;

    private AbstractBeanFactory beanFactory;

    private final Map<InetSocketAddress, WebSocketServer> addressWebsocketServerMap = new HashMap<>();

    private Map<Class<? extends ChannelHandler>, ChannelHandler> sharableWebSocketHandlersMap = new HashMap<>();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof AbstractBeanFactory)) {
            throw new IllegalArgumentException(
                    "AutowiredAnnotationBeanPostProcessor requires a AbstractBeanFactory: " + beanFactory);
        }
        this.beanFactory = (AbstractBeanFactory) beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        ApplicationContext context = getApplicationContext();
        String[] endpointBeanNames = context.getBeanNamesForAnnotation(ServerEndpoint.class);
        Arrays.stream(endpointBeanNames).distinct().map(context::getType).forEach(this::registerEndpoint);
        init();
    }

    private void init() {
        for (Map.Entry<InetSocketAddress, WebSocketServer> entry : addressWebsocketServerMap.entrySet()) {
            WebSocketServer webSocketServer = entry.getValue();
            try {
                webSocketServer.init();
                EndpointServer endpointServer = webSocketServer.getEndpointServer();
                StringJoiner stringJoiner = new StringJoiner(",");
                endpointServer.getPathMatcherSet().forEach(pathMatcher -> stringJoiner.add("'" + pathMatcher.getPattern() + "'"));
                logger.info(String.format("\033[Netty WebSocket started on port: %s with context path(s): %s .\033[", endpointServer.getPort(), stringJoiner.toString()));
            } catch (InterruptedException e) {
                logger.error(String.format("websocket [%s] init fail", entry.getKey()), e);
            }
        }
    }

    private void registerEndpoint(Class<?> endpointClass) {
        ServerEndpoint annotation = AnnotatedElementUtils.findMergedAnnotation(endpointClass, ServerEndpoint.class);
        if (annotation == null) {
            throw new IllegalStateException("missingAnnotation ServerEndpoint");
        }
        EndpointConfig endpointConfig = buildConfig(annotation);
        ApplicationContext context = getApplicationContext();
        EndpointMethodMapping endpointMethodMapping = null;
        try {
            endpointMethodMapping = new EndpointMethodMapping(endpointClass, context, beanFactory);
        } catch (DeploymentException e) {
            throw new IllegalStateException("Failed to register ServerEndpointConfig: " + endpointConfig, e);
        }

        InetSocketAddress inetSocketAddress = new InetSocketAddress(endpointConfig.getHOST(), endpointConfig.getPORT());
        String path = resolveAnnotationValue(annotation.value(), String.class, "path");
        WebSocketServer webSocketServer = addressWebsocketServerMap.get(inetSocketAddress);
        if (webSocketServer == null) {
            EndpointServer endpointServer = new EndpointServer(endpointMethodMapping, endpointConfig, path);
            webSocketServer = new WebSocketServer(endpointServer, endpointConfig, annotation.beforeHandShakeFilters(), buildBeforeWebSocketHandlers(annotation));
            addressWebsocketServerMap.putIfAbsent(inetSocketAddress, webSocketServer);
        } else {
            webSocketServer.getEndpointServer().addPathMethodMapping(path, endpointMethodMapping);
        }
    }

    private LinkedList<ChannelHandler> buildBeforeWebSocketHandlers(ServerEndpoint annotation) {
        LinkedList<ChannelHandler> handlers = new LinkedList<>();
        ApplicationContext context = getApplicationContext();

        Class<? extends ChannelHandler>[] handlerClazzs = annotation.beforeWebSocketHandlers();
        for (Class<? extends ChannelHandler> handlerClazz : handlerClazzs) {

            try {
                ChannelHandler handler = context.getBean(handlerClazz);
                if (null!=handler){
                    handlers.add(handler);
                }
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
        return handlers;
    }

    private EndpointConfig buildConfig(ServerEndpoint annotation) {
        String host = resolveAnnotationValue(annotation.host(), String.class, "host");
        int port = resolveAnnotationValue(annotation.port(), Integer.class, "port");
        String path = resolveAnnotationValue(annotation.value(), String.class, "value");
        int bossLoopGroupThreads = resolveAnnotationValue(annotation.bossLoopGroupThreads(), Integer.class, "bossLoopGroupThreads");
        int workerLoopGroupThreads = resolveAnnotationValue(annotation.workerLoopGroupThreads(), Integer.class, "workerLoopGroupThreads");

        boolean userIdleStateHandler = resolveAnnotationValue(annotation.userIdleStateHandler(), Boolean.class, "userIdleStateHandler");
        boolean useCompressionHandler = resolveAnnotationValue(annotation.useCompressionHandler(), Boolean.class, "useCompressionHandler");
        boolean useWebSocketFrameAggregator = resolveAnnotationValue(annotation.useWebSocketFrameAggregator(), Boolean.class, "useWebSocketFrameAggregator");

        int optionConnectTimeoutMillis = resolveAnnotationValue(annotation.optionConnectTimeoutMillis(), Integer.class, "optionConnectTimeoutMillis");
        int optionSoBacklog = resolveAnnotationValue(annotation.optionSoBacklog(), Integer.class, "optionSoBacklog");

        int childOptionWriteSpinCount = resolveAnnotationValue(annotation.childOptionWriteSpinCount(), Integer.class, "childOptionWriteSpinCount");
        int childOptionWriteBufferHighWaterMark = resolveAnnotationValue(annotation.childOptionWriteBufferHighWaterMark(), Integer.class, "childOptionWriteBufferHighWaterMark");
        int childOptionWriteBufferLowWaterMark = resolveAnnotationValue(annotation.childOptionWriteBufferLowWaterMark(), Integer.class, "childOptionWriteBufferLowWaterMark");
        int childOptionSoRcvbuf = resolveAnnotationValue(annotation.childOptionSoRcvbuf(), Integer.class, "childOptionSoRcvbuf");
        int childOptionSoSndbuf = resolveAnnotationValue(annotation.childOptionSoSndbuf(), Integer.class, "childOptionSoSndbuf");
        boolean childOptionTcpNodelay = resolveAnnotationValue(annotation.childOptionTcpNodelay(), Boolean.class, "childOptionTcpNodelay");
        boolean childOptionSoKeepalive = resolveAnnotationValue(annotation.childOptionSoKeepalive(), Boolean.class, "childOptionSoKeepalive");
        int childOptionSoLinger = resolveAnnotationValue(annotation.childOptionSoLinger(), Integer.class, "childOptionSoLinger");
        boolean childOptionAllowHalfClosure = resolveAnnotationValue(annotation.childOptionAllowHalfClosure(), Boolean.class, "childOptionAllowHalfClosure");

        int readerIdleTimeSeconds = resolveAnnotationValue(annotation.readerIdleTimeSeconds(), Integer.class, "readerIdleTimeSeconds");
        int writerIdleTimeSeconds = resolveAnnotationValue(annotation.writerIdleTimeSeconds(), Integer.class, "writerIdleTimeSeconds");
        int allIdleTimeSeconds = resolveAnnotationValue(annotation.allIdleTimeSeconds(), Integer.class, "allIdleTimeSeconds");

        int maxFramePayloadLength = resolveAnnotationValue(annotation.maxFramePayloadLength(), Integer.class, "maxFramePayloadLength");

        return new EndpointConfig(host, port, path, bossLoopGroupThreads, workerLoopGroupThreads, userIdleStateHandler, useCompressionHandler, useWebSocketFrameAggregator, optionConnectTimeoutMillis, optionSoBacklog, childOptionWriteSpinCount, childOptionWriteBufferHighWaterMark, childOptionWriteBufferLowWaterMark, childOptionSoRcvbuf, childOptionSoSndbuf, childOptionTcpNodelay, childOptionSoKeepalive, childOptionSoLinger, childOptionAllowHalfClosure, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, maxFramePayloadLength);
    }

    private <T> T resolveAnnotationValue(Object value, Class<T> requiredType, String paramName) {
        if (value == null) {
            return null;
        }
        TypeConverter typeConverter = beanFactory.getTypeConverter();

        if (value instanceof String) {
            String strVal = beanFactory.resolveEmbeddedValue((String) value);
            BeanExpressionResolver beanExpressionResolver = beanFactory.getBeanExpressionResolver();
            if (beanExpressionResolver != null) {
                value = beanExpressionResolver.evaluate(strVal, new BeanExpressionContext(beanFactory, null));
            } else {
                value = strVal;
            }
        }
        try {
            return typeConverter.convertIfNecessary(value, requiredType);
        } catch (TypeMismatchException e) {
            throw new IllegalArgumentException("Failed to convert value of parameter '" + paramName + "' to required type '" + requiredType.getName() + "'");
        }
    }
}
