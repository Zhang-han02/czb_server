package me.zhengjie.utils;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;


public class ClassCompareUtil {

    /**
     * Description: 判断两个对象的各个属性是否相同
     * Param: isUpdate为true，表示对原有属性值进行覆盖更改，为false，则只增加原来没有的属性值，原来有的属性值不进行更改
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T getNewVo(T older, T newer, boolean isUpdate) {
        boolean flag = false;
        try {
            Field[] fields = older.getClass().getDeclaredFields();
            Field field;
            String fieldName;
            Class clz = older.getClass();
            String getMethodName;
            String setMethodName;
            Method getMethod;
            Method setMethod;
            Object oldValue;
            Object newValue;

            for(int i = 0; i < fields.length; i++) {
                field = fields[i];
                fieldName = field.getName();
                getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                getMethod = clz.getMethod(getMethodName, new Class[] {});
                setMethod = clz.getMethod(setMethodName, new Class[] {field.getType()});
                oldValue = getMethod.invoke(older, new Object[]{});
                newValue = getMethod.invoke(newer, new Object[]{});
                if(oldValue == null) {//原属性值为null
                    if(newValue != null) {//新属性值不为null，不管更新标志是否为true
                        setMethod.invoke(older, newValue);//把新属性值赋值给原属性
                        flag = true;
                    }
                } else if (!Objects.equals(oldValue, newValue)) {//原属性值不为null
                    if(newValue != null && isUpdate) {//新属性值不为null，并且更新标志为true
                        setMethod.invoke(older, newValue);//用新属性值覆盖原属性值
                        flag = true;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return flag ? older : null;
    }


}
