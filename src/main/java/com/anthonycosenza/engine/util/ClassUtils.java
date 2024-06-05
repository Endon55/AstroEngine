package com.anthonycosenza.engine.util;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassUtils
{
    
    public static List<Field> getAllFields(Class<?> clazz)
    {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while(current != null)
        {
            fields.addAll(List.of(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields;
    }
    
    public static <T> Set<Class<? extends T>>  findAllClasses(String basePackage, Class<T> supertype)
    {
        try
        {
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
