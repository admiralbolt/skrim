package avi.mod.skrim.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Utilities for fucking with members & methods of the base game.
 */
public class ReflectionUtils {

  /**
   * Set the value of a field of an object.
   */
  public static void hackValueTo(Object instance, Object value, String... fieldNames) {
    Field field;
    for (String fieldName : fieldNames) {
      try {
        field = instance.getClass().getDeclaredField(fieldName);
        setFieldValue(instance, field, value);
      } catch (NoSuchFieldException | SecurityException e) {
        // e.printStackTrace();
      }
    }
  }

  /**
   * Set the value of a field of the superclass of an object.
   */
  public static void hackSuperValueTo(Object instance, Object value, String... fieldNames) {
    Field field;
    for (String fieldName : fieldNames) {
      try {
        field = instance.getClass().getSuperclass().getDeclaredField(fieldName);
        setFieldValue(instance, field, value);
      } catch (NoSuchFieldException | SecurityException e) {
        // e.printStackTrace();
      }
    }
  }

  /**
   * Gets a field of the object.
   */
  public static Object getPrivateField(Object instance, String... fieldNames) {
    Field field;
    for (String fieldName : fieldNames) {
      try {
        field = instance.getClass().getDeclaredField(fieldName);
        return getFieldValue(instance, field);
      } catch (NoSuchFieldException | SecurityException e) {
        // e.printStackTrace();
      }
    }
    System.out.println("[ReflectionUtils] Could not find any fields on instance: [" + instance + "], with names: [" + Arrays.toString(fieldNames) + "]");
    return null;
  }

  /**
   * Gets a field of the super class of the object.
   */
  public static Object getSuperPrivateField(Object instance, String... fieldNames) {
    Field field;
    for (String fieldName : fieldNames) {
      try {
        field = instance.getClass().getSuperclass().getDeclaredField(fieldName);
        return getFieldValue(instance, field);
      } catch (NoSuchFieldException | SecurityException e) {
        // e.printStackTrace();
      }
    }
    System.out.println("[ReflectionUtils] Could not find any fields on instance: [" + instance + "], with names: [" + Arrays.toString(fieldNames) + "]");
    return null;
  }

  /**
   * Gets a field of the super class of the super class of the object.
   */
  public static Object getSuperSuperPrivateField(Object instance, String... fieldNames) {
    Field field;
    for (String fieldName : fieldNames) {
      try {
        field = instance.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
        return getFieldValue(instance, field);
      } catch (NoSuchFieldException e) {
        // e.printStackTrace();
      }
    }
    System.out.println("[ReflectionUtils] Could not find any fields on instance: [" + instance + "], with names: [" + Arrays.toString(fieldNames) + "]");
    return null;
  }

  /**
   * Executes a private method of the object.
   */
  public static Object executePrivateMethod(Object instance, String... methodNames) {
    Method method;
    for (String methodName : methodNames) {
      try {
        method = instance.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(instance);
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Executes a private method of the super class of the object.
   */
  public static Object executeSuperPrivateMethod(Object instance, String... methodNames) {
    Method method;
    for (String methodName : methodNames) {
      try {
        method = instance.getClass().getSuperclass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(instance);
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  /**
   * Get the value of a field.
   */
  private static Object getFieldValue(Object instance, Field field) {
    field.setAccessible(true);
    try {
      return field.get(instance);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Set the value of a field.
   */
  private static void setFieldValue(Object instance, Field field, Object value) {
    field.setAccessible(true);
    try {
      field.set(instance, value);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }


  // Below are functions for use debugging. They will print out all fields of an object at various levels of super-class ness.

  public static void printFields(Object instance) {
    for (Field field : instance.getClass().getDeclaredFields()) {
      try {
        field.setAccessible(true);
        System.out.println("field: " + field.getName() + ", value: " + field.get(instance));
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  public static void printSuperFields(Object instance) {
    for (Field field : instance.getClass().getSuperclass().getDeclaredFields()) {
      try {
        field.setAccessible(true);
        System.out.println("field: " + field.getName() + ", value: " + field.get(instance));
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  public static void printSuperSuperFields(Object instance) {
    for (Field field : instance.getClass().getSuperclass().getSuperclass().getDeclaredFields()) {
      try {
        field.setAccessible(true);
        System.out.println("field: " + field.getName() + ", value: " + field.get(instance));
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

}
