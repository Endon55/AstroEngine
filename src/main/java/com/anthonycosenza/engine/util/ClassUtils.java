package com.anthonycosenza.engine.util;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassUtils
{
    
    public static <T> Set<Class<? extends T>>  findAllClasses(String basePackage, Class<T> supertype)
    {
        try
        {
            /*return ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses().stream()
                    .filter(clazz -> clazz.getPackageName().equalsIgnoreCase(packageName))
                    .map(ClassPath.ClassInfo::load).collect(Collectors.toSet());*/
    
    
            return ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClassesRecursive("com.anthonycosenza")
                    .stream()
                    .map(ClassPath.ClassInfo::load)
                    .filter(supertype::isAssignableFrom).map(clazz -> (Class<? extends T>) clazz)
                    .collect(Collectors.toSet());
            
            
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
