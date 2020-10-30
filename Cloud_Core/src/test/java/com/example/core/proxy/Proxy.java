package com.example.core.proxy;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Proxy {
    private static final String ENTER = "\r\n";

    public static Object instanceProxy(ProxyInvocationHandler h, TargetInterface targetInterface) {
        System.out.println("handler代理方法开始");
        TargetInterface proxyTarget = (TargetInterface) newInstance(new ProxyClassLoader(),
                targetInterface.getClass().getInterfaces(), h);
        return proxyTarget;
    }

    public static Object newInstance(ProxyClassLoader classLoader, Class<?>[] interfaces, ProxyInvocationHandler h){
        try {
            //动态生成代码
            String srcClass = generateSrc(interfaces,h);
            //输出java文件
            String filePath = Proxy.class.getResource("").getPath()+"$Proxy0.java";
            System.out.println(filePath);
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(srcClass);
            fileWriter.flush();
            fileWriter.close();
            //编译java文件为.class文件
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,null,null);
            Iterable iterable = fileManager.getJavaFileObjects(filePath);
            JavaCompiler.CompilationTask task = compiler.getTask(null,fileManager,null,null,null,iterable);
            task.call();
            fileManager.close();
            //加载编译生成的class文件到VM
            Class<?> proxyClass = classLoader.findClass("$Proxy0");
            Constructor<?> constructor = proxyClass.getConstructor(ProxyInvocationHandler.class);
            //删掉虚拟代理类
            File file = new File(filePath);
            file.delete();
            //返回字节码重组以后的代理对象
            return constructor.newInstance(h);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String generateSrc(Class<?>[] interfaces, ProxyInvocationHandler handler){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package com.bigdata.demo.proxy;" +ENTER+ENTER);
        stringBuilder.append("import java.lang.reflect.Method;"+ENTER);
        stringBuilder.append("public class $Proxy0 implements "+interfaces[0].getName()+"{"+ENTER);
        stringBuilder.append(handler.getClass().getInterfaces()[0].getName() + " h ;"+ENTER);
        stringBuilder.append("public $Proxy0("+handler.getClass().getInterfaces()[0].getName() +" h){"+ENTER);
        stringBuilder.append("this.h = h;"+ENTER);
        stringBuilder.append("}"+ENTER);

        for (Method method:interfaces[0].getMethods()){
            stringBuilder.append("public "+method.getReturnType().getName()+" "+method.getName()+"(){"+ENTER);
            stringBuilder.append("try{"+ENTER);
            stringBuilder.append("Method method = "+interfaces[0].getName()+".class.getMethod(\""+method.getName()+"\",new Class[]{});"+ENTER);
            stringBuilder.append("this.h.invoke(this,method,null);"+ENTER);
            stringBuilder.append("}catch(Throwable e){"+ENTER);
            stringBuilder.append("e.getMessage();"+ENTER);
            stringBuilder.append("}"+ENTER);
            stringBuilder.append("}"+ENTER+ENTER);
        }
        stringBuilder.append("}"+ENTER);
        return stringBuilder.toString();
    }
}
