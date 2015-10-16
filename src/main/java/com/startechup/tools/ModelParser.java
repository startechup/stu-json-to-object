/*
 *  Copyright (2015) StarTechUp Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.startechup.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a class that will parse dynamically a {@link JSONObject JSONObjects} or {@link JSONArray
 * JSONArrays}(and any object related to json construction) into a Model Object.
 * This class eliminates the need of a parser class to parse a json.
 */
public class ModelParser {

    private static final String TAG = "JsonParser";
    private static final String NULL = "null";

    public ModelParser() {
    }

    /**
     * Creates a new instance of the model class.
     *
     * @param classModel The class that will contain the parse json values and the one
     *                   that will direct the parsing.
     * @return Returns a new object instance of the model class, null if exceptions occur.
     */
    private Object getNewInstance(Class classModel) {
        Object object = null;
        try {
            object = classModel.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return object;
    }

    /**
     * Gets the values from the {@link JSONObject JSONObjects}/response returned from an api call
     * request and assign it to the field inside the class that will contain the parsed values.
     *
     * @param classModel The class type that will contain the parse value.
     * @param jsonObject The API response object.
     * @return Returns a generic class containing the values from the json, null if exception occurs.
     */
    public <T> T parse(Class<T> classModel, JSONObject jsonObject) {
        Object object = getNewInstance(classModel);

        if (object != null) {
            // TODO this solution is much accurate but not flexible when using a 3rd party library
            // for object model creation like squidb from yahoo, annotation should be added to the class method
            // and we cannot do that on squidb generated Class model.
            for (Method method : classModel.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SetterSpec.class)) {
                    SetterSpec methodLinker = method.getAnnotation(SetterSpec.class);
                    String jsonKey = methodLinker.jsonKey();

                    initMethodInvocation(method, object, jsonKey, jsonObject);
                }
            }
            return classModel.cast(object);
        } else {
            return null;
        }
    }

    /**
     * Gets the values from the {@link JSONArray JSONArrays}/response return from an api call
     * request and assign it to the field inside the class that will contain the parsed values.
     *
     * @param objectType The type of object that the arrayList will contain.
     * @param jsonArray The API response object.
     * @return Returns an arrayList with a generic type parameter.
     */
    public <E> ArrayList<E> parse(Class<E> objectType, JSONArray jsonArray) {
        ArrayList<E> arrayList = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            Object value = getValueFromJsonArray(jsonArray, i);
            if (value instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) value;
                arrayList.add(parse(objectType, jsonObject));

            } else if (value instanceof Number) {
                Object castedObject = castNumberObject(objectType, value);
                arrayList.add(objectType.cast(castedObject));
            } else {
                arrayList.add(objectType.cast(value));
            }
        }
        return arrayList;
    }

    /**
     * Invokes the setter method that was link to the json key.
     *
     * @param method The setter method to be invoked.
     * @param classInstance The instance of the container class.
     * @param key The json key serving as the reference of the value in the json object
     * @param jsonObject The API response object.
     */
    private void initMethodInvocation(Method method, Object classInstance, String key, JSONObject jsonObject) {
        Object value = getValueFromJsonObject(jsonObject, key);

        // Only invoke the method when the value is not null
        if (value != null) {
            method.setAccessible(true);
            Object castedObject = value;

            if (value instanceof Number) {
                castedObject = castNumberObject(method.getParameterTypes()[0], value);
            } else if (value instanceof JSONArray) {
                if (method.getParameterTypes()[0].isArray()) {
                    //TODO find a way to genetically convert json array to array, for now throw our custom exception
                    throwException("Cannot parse " + JSONArray.class + " to " + method.getParameterTypes()[0]);
                } else {
                    Object parameterInstance = getNewInstance(method.getParameterTypes()[0]);
                    if (parameterInstance instanceof Collection) {
                        ParameterizedType parameterizedType = (ParameterizedType) method.getGenericParameterTypes()[0];
                        Class<?> classType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        castedObject = parse(classType, (JSONArray) value);
                    } else {
                        //TODO find a way to genetically convert json array to the other parameter class, for now throw our custom exception
                        throwException("Cannot parse " + JSONArray.class + " to " + method.getParameterTypes()[0]);
                    }
                }
            } else if (value instanceof JSONObject) {
                castedObject = parse(method.getParameterTypes()[0], ((JSONObject) value));
            }

            // Finally invoke the method after casting the values into the method parameter type
            invoke(method, classInstance, castedObject);
        }
    }

    /**
     * Finally invokes the method to assign the values.
     *
     * @param method The setter method to be invoked.
     * @param instance The instance of the container class.
     * @param value The parsed value from the json.
     */
    private void invoke(Method method, Object instance, Object value) {
        try {
            method.invoke(instance, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the value from the jsonObject using the json key.
     *
     * @param jsonObject The JsonObject containing the values.
     * @param jsonKey The json key corresponding to the value.
     * @return Returns an object from the json object, null otherwise.
     */
    private Object getValueFromJsonObject(JSONObject jsonObject, String jsonKey) {
        Object value = null;
        if (jsonObject.has(jsonKey)) {
            try {
                value = jsonObject.get(jsonKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // If object from json contains null then set the object as null
        if (value != null && value.toString().equalsIgnoreCase(NULL)) {
            value = null;
        }

        return value;
    }

    /**
     * Gets the value from the jsonArray using the object index.
     *
     * @param jsonArray Tha json array containing the values.
     * @param index The index of the single object inside the array.
     * @return Returns an object from the json object, null otherwise.
     */
    private Object getValueFromJsonArray(JSONArray jsonArray, int index) {
        Object value = null;
        try {
            value = jsonArray.get(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Converts/Cast the value according to the type of method parameter.
     *
     * @param parameterType The parameter type define in the setter method.
     * @param value The number value to be casted into the parameter type.
     * @return Returns an object casted into the parameter type.
     */
    private Object castNumberObject(Class<?> parameterType, Object value) {
        Object castedObject = value;
        if (parameterType == Float.class || parameterType == float.class) {
            castedObject = ((Number) value).floatValue();
        } else if (parameterType == Short.class || parameterType == short.class) {
            castedObject = ((Number) value).shortValue();
        } else if (parameterType == Long.class || parameterType == long.class) {
            castedObject = ((Number) value).longValue();
        } else if (parameterType == Integer.class || parameterType == int.class) {
            castedObject = ((Number) value).intValue();
        } else if (parameterType == String.class) {
            castedObject = value.toString();
        }

        return castedObject;
    }

    /**
     * Throws the error exception.
     *
     * @param message The error message.
     */
    private void throwException(String message) {
        try {
            throw new ModelParserException(message);
        } catch (ModelParserException e) {
            e.printStackTrace();
        }
    }
}
