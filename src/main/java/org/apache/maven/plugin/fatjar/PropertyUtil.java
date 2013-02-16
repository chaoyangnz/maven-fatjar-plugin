/**
 * 
 */
package org.apache.maven.plugin.fatjar;

import java.lang.reflect.Field;

/**
 * @author Ñî³¬  <ychao@bankcomm.com>
 *
 * 2010-1-23
 */
public class PropertyUtil {
	public static void setProperty(Object obj, String propertyName, Object value) {
		Class<? extends Object> clazz = obj.getClass();
		Field[] fields= clazz.getDeclaredFields();
		for(Field field : fields) {
			if(propertyName.equals(field.getName())) {
				field.setAccessible(true);
				try {
					field.set(obj, value);
				} catch (Exception e) {
					e.printStackTrace();
//					super.getLog().info(propertyName);
				}
			}
		}
	}
	
	public static Object getProperty(Object obj, String propertyName) {
		Class<? extends Object> clazz = obj.getClass();
		Field[] fields= clazz.getDeclaredFields();
		Object value = null;
		for(Field field : fields) {
			if(propertyName.equals(field.getName())) {
				field.setAccessible(true);
				try {
					value = field.get(obj);
				} catch (Exception e) {
					e.printStackTrace();
//					super.getLog().info(propertyName);
				}
			}
		}
		
		return value;
	}
}
