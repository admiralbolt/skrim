package avi.mod.skrim.utils;

import java.lang.reflect.Field;

import net.minecraft.entity.ai.attributes.RangedAttribute;

public class Reflection {
	
	public static void hackValueTo(Object instance, String fieldName, Object value) {
		Field field;
		try {
			field = instance.getClass().getDeclaredField(fieldName);
			setFieldValue(instance, field, value);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setFieldValue(Object instance, Field field, Object value) {
		field.setAccessible(true);
		try {
			field.set(instance, value);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void hackSuperValueTo(Object instance, String fieldName, Object value) {
		Field field;
		try {
			field = instance.getClass().getSuperclass().getDeclaredField(fieldName);
			setFieldValue(instance, field, value);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printFields(Object instance) {
		for (Field field: instance.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				System.out.println("field: " + field.getName() + ", value: " + field.get(instance));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void hackAttributeTo(Object instance, String fieldName, Object value) {
		Field field;
		try {
			field = instance.getClass().getDeclaredField("genericAttribute");
			field.setAccessible(true);
			RangedAttribute attr;
			try {
				attr = (RangedAttribute) field.get(instance);
				hackValueTo(attr, fieldName, value);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double getAttributeValue(Object instance, String fieldName) {
		Field field;
		try {
			field = instance.getClass().getDeclaredField("genericAttribute");
			field.setAccessible(true);
			RangedAttribute attr;
			try {
				attr = (RangedAttribute) field.get(instance);
				Field inner = attr.getClass().getDeclaredField(fieldName);
				inner.setAccessible(true);
				return inner.getDouble(attr);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
	}

}
