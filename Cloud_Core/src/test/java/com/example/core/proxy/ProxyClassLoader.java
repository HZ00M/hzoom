package com.example.core.proxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class ProxyClassLoader extends ClassLoader {
    private File classPathFile;

    public ProxyClassLoader() {
        String classPath = ProxyClassLoader.class.getResource("").getPath();
        this.classPathFile = new File(classPath);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String className = ProxyClassLoader.class.getPackage().getName()+"."+name;
        if(null!=classPathFile){
            File classFile = new File(classPathFile,name+".class");
            if(classFile.exists()){
                FileInputStream in =null;
                ByteArrayOutputStream out = null;
                try {
                    in = new FileInputStream(classFile);
                    out =new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int len;
                    while ((len = in.read(bytes)) != -1){
                        out.write(bytes,0,len);
                    }
                    return defineClass(className,out.toByteArray(),0,out.size());
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        if(null!=in){
                            in.close();
                        }
                        if (null!=out){
                            out.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

}
