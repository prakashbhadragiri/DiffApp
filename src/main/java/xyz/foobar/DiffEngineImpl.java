package xyz.foobar;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

public class DiffEngineImpl implements DiffEngine {
    public <T extends Serializable> T apply(T original, Diff<?> diff) throws DiffException {
        T temp = null;
        try {

            temp = (T) BeanUtils.cloneBean(original);
            List<Differences> differencesList = diff.getDifferencesList();

            for (Differences difference : differencesList) {
                if (difference.getAction().equalsIgnoreCase("Delete")) {
                    if (difference.getFieldName().equals(temp.getClass().getSimpleName())) {
                        temp = null;
                    } else {
                        setBeanPropertyVal(difference.getFieldName(), difference.getValue(), temp);
                    }
                }
                if (difference.getAction().equalsIgnoreCase("Create")) {
                    setBeanPropertyVal(difference.getFieldName(), difference.getValue(), temp);
                }
                if (difference.getAction().equalsIgnoreCase("Update")) {
                    setBeanPropertyVal(difference.getFieldName(), difference.getValue(), temp);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return temp;
    }

    private boolean setBeanPropertyVal(String fieldName, Object fieldValue, Object temp) {
        Field field;
        try {
            field = temp.getClass().getDeclaredField(fieldName);

            if (field == null) {
                return false;
            }
            field.setAccessible(true);

            field.set(temp, fieldValue);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <T extends Serializable> Diff<T> calculate(T original, T modified) throws DiffException {
        if (original != null && modified != null && !original.getClass().equals(modified.getClass())) {
            throw new DiffException("Input objects are not same");
        }

        T t;
        Diff<T> diff = new Diff<T>();

        try {
            if (modified != null && original == null) {
                t = (T) BeanUtils.cloneBean(modified);
            } else {
                t = (T) BeanUtils.cloneBean(original);
            }
            diff.setVal(t);

            viewDifferencesBetweenOriginalAndModified(diff, original, modified, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;
    }

    private <T extends Serializable> void viewDifferencesBetweenOriginalAndModified(Diff<T> diff, T original, T modified, int level) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, ClassNotFoundException {

        Field[] fields;
        if (modified != null) {
            fields = modified.getClass().getDeclaredFields();
        } else {
            fields = original.getClass().getDeclaredFields();
        }
        if (original == null) {
            whenOriginalIsNull(diff, modified, level, fields);
        } else if (modified == null) {
            whenModifiedIsNull(diff, original, level);
        } else {
            whenOriginalAndModifiedBothNotNull(diff, original, modified, level, fields);
        }
    }

    private <T extends Serializable> void whenModifiedIsNull(Diff<T> diff, T original, int level) {
        diff.addDiff(new Differences("Delete", original.getClass().getName(), null, level, true));
        diff.setVal(null);
    }

    private <T extends Serializable> void whenOriginalIsNull(Diff<T> diff, T modified, int level, Field[] fields) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, ClassNotFoundException {
        diff.addDiff(new Differences("Create", modified.getClass().getName(), null, level, true));
        for (Field field : fields) {
            if (checkIfVariableIsStatic(field)) {
                continue;
            }

            if (field.getType().equals(modified.getClass())) { // if equals Person class
                diff.addDiff(new Differences("Create", field.getName(), field.getType().getSimpleName(), level, false));
            } else {
                field.setAccessible(true);
                Object modifiedInstance = field.get(modified);
                diff.addDiff(new Differences("Create", field.getName(), field.getType().getSimpleName(), null, modifiedInstance, level, false));
            }


            /*if (field.getType().equals(modified.getClass().getDeclaredField("pet").getType())) {
                System.out.println("Prakash::: "+modified.getClass().getDeclaredField("pet"));
                System.out.println(BeanUtils.getProperty(modified, field.getName()));
                if (BeanUtils.getProperty(modified, field.getName()) != null) {
                    Field[] f = BeanUtils.getProperty(modified, field.getName()).getClass().getDeclaredFields();
                    for (Field field1 : f) {
                        field1.setAccessible(true);
//                        System.out.println(field1.getName() + " = " + field1.get(BeanUtils.getProperty(modified, field.getName())).toString());
                    }
                }
            }*/

            if (field.getType().equals(modified.getClass())) {
                field.setAccessible(true);
                T value = (T) field.get(modified);
                if (value != null) {
                    viewDifferencesBetweenOriginalAndModified(diff, null, value, level + 1);
                }
            }
        }
    }

    private <T extends Serializable> void whenOriginalAndModifiedBothNotNull(Diff<T> diff, T original, T modified, int level, Field[] fields) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, ClassNotFoundException {
        diff.addDiff(new Differences("Update", modified.getClass().getName(), null, level, true));
        for (Field field : fields) {
            if (checkIfVariableIsStatic(field)) {
                continue;
            }
            boolean isCollection = false;

            if (original != null || modified != null) {
                field.setAccessible(true);
                Object instance = (original != null) ? field.get(original) : field.get(modified);
                isCollection = instance instanceof Collection;
            }
            if (checkIfOriginalAndModifiedFieldIsNull(original, modified, field)) {
                continue;
            }
            if (field.getType().equals(original.getClass())) {
                diff.addDiff(new Differences("Update", field.getName(), field.getType().getSimpleName(), level, false));
                field.setAccessible(true);

                T fieldOriginal = (T) field.get(original);
                T fieldModified = (T) field.get(modified);

                if (fieldModified == null && fieldOriginal != null) {
                    // if modified field is null then delete
                    diff.addDiff(new Differences("Delete", field.getName(), field.getType().getSimpleName(), level, false));
                    continue;
                }
                viewDifferencesBetweenOriginalAndModified(diff, fieldOriginal, fieldModified, level + 1);
                continue;

            }
            if (BeanUtils.getProperty(original, field.getName()) == null && BeanUtils.getProperty(modified, field.getName()) != null) {
                field.setAccessible(true);
                Object changedInstence = field.get(modified);
                diff.addDiff(new Differences("Update", field.getName(), field.getType().getSimpleName(), null, changedInstence, level, false));

            }
            if (BeanUtils.getProperty(original, field.getName()) != null && BeanUtils.getProperty(modified, field.getName()) != null) {

                if (!isCollection && compareOriginalFieldAndModifiedField(original, modified, field)) {
                    continue;
                } else {
                    field.setAccessible(true);
                    Object modifiedObj = field.get(modified);
                    Object originalObj = field.get(original);
                    // check if it is a collection variable
                    if (isCollection) {
                        if (CollectionUtils.isEqualCollection((Collection) originalObj, (Collection) modifiedObj)) {
                            continue;
                        }
                    }
                    diff.addDiff(new Differences("Update", field.getName(), field.getType().getSimpleName(), originalObj, modifiedObj, level, false));
                }
            }
            if (BeanUtils.getProperty(modified, field.getName()) == null && BeanUtils.getProperty(original, field.getName()) != null) {
                diff.addDiff(new Differences("Delete", field.getName(), field.getType().getName(), level, false));
            }

        }
    }

    private boolean checkIfVariableIsStatic(Field field) {
        return java.lang.reflect.Modifier.isStatic(field.getModifiers());
    }

    private <T extends Serializable> boolean checkIfOriginalAndModifiedFieldIsNull(T original, T modified, Field field) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtils.getProperty(original, field.getName()) == null && BeanUtils.getProperty(modified, field.getName()) == null;
    }

    private <T extends Serializable> boolean compareOriginalFieldAndModifiedField(T original, T modified, Field field) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return BeanUtils.getProperty(original, field.getName()).equals(BeanUtils.getProperty(modified, field.getName()));
    }
}