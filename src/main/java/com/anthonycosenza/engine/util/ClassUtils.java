package com.anthonycosenza.engine.util;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassUtils
{
    public static Method getMethod(Class<?> clazz, String methodName)
    {
        for(Method method : clazz.getDeclaredMethods())
        {
            if(method.getName().equals(methodName)) return method;
        }
        return null;
    }
    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException
    {
        for(Field field : clazz.getDeclaredFields())
        {
            if(field.getName().equals(fieldName)) return field;
        }
        throw new NoSuchFieldException(fieldName);
    }
    
    public static Field getFieldInclSuper(Class<?> clazz, String fieldName) throws NoSuchFieldException
    {
        Class<?> current = clazz;
        while(current != null)
        {
            for(Field field : current.getDeclaredFields())
            {
                if(field.getName().equals(fieldName)) return field;
            }
            current = current.getSuperclass();
        }
        throw new NoSuchFieldException(fieldName);
    }
    public static List<Field> getAllFieldsInclSuper(Class<?> clazz)
    {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while(current != null)
        {
            for(Field field : current.getDeclaredFields())
            {
                if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()))
                {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
            current = current.getSuperclass();
        }
        return fields;
    }
    
    public static List<Field> getAllFields(Class<?> clazz)
    {
        List<Field> fields = new ArrayList<>();
        
        for(Field field : clazz.getDeclaredFields())
        {
            if(!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()))
            {
                field.setAccessible(true);
                fields.add(field);
            }
        }
        return fields;
    }
    
    public static <T> Set<Class<? extends T>> findAllClasses(String basePackage, Class<T> supertype, ClassLoader loader)
    {
        try
        {
            return ClassPath.from(loader).getTopLevelClassesRecursive(basePackage)
                    .stream()
                    .map(ClassPath.ClassInfo::load)
                    .filter(supertype::isAssignableFrom).map(clazz -> (Class<? extends T>) clazz)
                    .collect(Collectors.toSet());
            
            
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    public static <T> Set<Class<? extends T>>  findAllClasses(String basePackage, Class<T> supertype)
    {
        return findAllClasses(basePackage, supertype, ClassLoader.getSystemClassLoader());
    }
}
