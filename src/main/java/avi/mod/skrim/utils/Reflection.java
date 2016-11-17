package avi.mod.skrim.utils;

import java.lang.reflect.Field;

import net.minecraft.entity.ai.attributes.RangedAttribute;

public class Reflection {
	
	public static void hackValueTo(Object instance, Object value, String... fieldNames) {
		Field field;
		for (String fieldName : fieldNames) {
			try {
				field = instance.getClass().getDeclaredField(fieldName);
				setFieldValue(instance, field, value);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Object getPrivateField(Object instance, String... fieldNames) {
		Field field;
		for (String fieldName : fieldNames) {
			try {
				field = instance.getClass().getDeclaredField(fieldName);
				return getFieldValue(instance, field);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Object getSuperPrivateField(Object instance, String... fieldNames) {
		Field field;
		for (String fieldName : fieldNames) {
			try {
				field = instance.getClass().getSuperclass().getDeclaredField(fieldName);
				return getFieldValue(instance, field);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Object getSuperSuperPrivateField(Object instance, String... fieldNames) {
		Field field;
		for (String fieldName : fieldNames) {
			try {
				field = instance.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
				return getFieldValue(instance, field);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Object getFieldValue(Object instance, Field field) {
		field.setAccessible(true);
		try {
			return field.get(instance);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	
	public static void hackSuperValueTo(Object instance, Object value, String... fieldNames) {
		Field field;
		try {
			for (String fieldName : fieldNames) {
				field = instance.getClass().getSuperclass().getDeclaredField(fieldName);
				setFieldValue(instance, field, value);
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
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
	
	public static void printSuperFields(Object instance) {
		for (Field field: instance.getClass().getSuperclass().getDeclaredFields()) {
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
	
	public static void printSuperSuperFields(Object instance) {
		for (Field field: instance.getClass().getSuperclass().getSuperclass().getDeclaredFields()) {
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
	
	public static void hackAttributeTo(Object instance, Object value, String... fieldNames) {
		Field field;
		try {
			field = instance.getClass().getDeclaredField("field_111136_b");
			field.setAccessible(true);
			RangedAttribute attr;
			try {
				attr = (RangedAttribute) field.get(instance);
				hackValueTo(attr, value, fieldNames);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double getAttributeValue(Object instance, String... fieldNames) {
		Field field;
		for (String fieldName : fieldNames) {
			try {
				field = instance.getClass().getDeclaredField("field_111136_b");
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
				// e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0.0;
	}

}
