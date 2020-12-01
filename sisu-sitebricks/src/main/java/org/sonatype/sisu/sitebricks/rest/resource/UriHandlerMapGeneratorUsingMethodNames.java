package org.sonatype.sisu.sitebricks.rest.resource;


import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.sonatype.sisu.sitebricks.rest.resource.UriHandler.Operation;
import org.sonatype.sisu.sitebricks.rest.resource.executor.MvelExecutor;

import com.google.sitebricks.SitebricksModule;
import com.google.sitebricks.http.Delete;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;
import com.google.sitebricks.http.Put;

// allow the method names for CRUD operations to be configurable
// validation that methods for CRUD operation have the right number of parameters

public class UriHandlerMapGeneratorUsingMethodNames implements UriHandlerMapGenerator {

  @Inject Logger logger;
  private String path;
  private Class<?> clazz;
  private SitebricksModule sitebricksModule;

  public UriHandlerMapGeneratorUsingMethodNames(String path, Class<?> clazz, SitebricksModule sitebricksModule) {
    this.path = path;
    this.clazz = clazz;
    this.sitebricksModule = sitebricksModule;
  }

  public UriHandlerMap generate() {
    UriHandlerMap uriHandlerMap = new UriHandlerMap(path, sitebricksModule);
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
      String methodName = method.getName();
      System.out.println(">>> " + methodName);
      if (methodName.startsWith("create")) {
        //
        // CREATE
        //
        Class<?> type = findTypeFromCreateMethod(method);
        if (type != null) {
          String p = "/" + methodName.substring(6).toLowerCase();
          String expression = "create" + methodName.substring(6) + "(entity)";
          UriHandler handler = new UriHandler(Operation.CREATE, Post.class, type, p, new MvelExecutor(expression, clazz));
          uriHandlerMap.addHandler(handler);
        }
      } else if (methodName.startsWith("read") && isPlural(methodName.substring(4))) {
        //
        // READ COLLECTION
        //
        Class<?> type = findTypefromReadCollectionMethod(method);
        if (type != null) {
          //
          // Need to find the base non-plural, remove the plural
          //
          String p = methodName.substring(4);
          p = "/" + p.substring(0,p.length()-1).toLowerCase();
          String expression = "read" + methodName.substring(4) + "()";
          UriHandler handler = new UriHandler(Operation.READ_COLLECTION, Get.class, type, p, new MvelExecutor(expression, clazz));
          uriHandlerMap.addHandler(handler);
        }
      } else if (methodName.startsWith("read")) {
        //
        // READ
        //
        Class<?> type = findTypeFromReadMethod(method);
        if (type != null) {
          String p = "/" + methodName.substring(4).toLowerCase();
          String expression = "read" + methodName.substring(4) + "(id)";
          UriHandler handler = new UriHandler(Operation.READ, Get.class, type, p + "/:id", new MvelExecutor(expression, clazz));
          uriHandlerMap.addHandler(handler);
        }
      } else if (methodName.startsWith("update")) {
        //
        // UPDATE
        //
        Class<?> type = findTypeFromUpdateMethod(method);
        if (type != null) {
          String p = "/" + methodName.substring(6).toLowerCase();
          String expression = "update" + methodName.substring(6) + "(entity)";
          UriHandler handler = new UriHandler(Operation.UPDATE, Put.class, type, p + "/:id", new MvelExecutor(expression, clazz));
          uriHandlerMap.addHandler(handler);
        }
      } else if (methodName.startsWith("delete")) {
        //
        // DELETE
        //
        String p = "/" + methodName.substring(6).toLowerCase();
        String expression = "delete" + methodName.substring(6) + "(id)";
        UriHandler handler = new UriHandler(Operation.DELETE, Delete.class, null, p + "/:id", new MvelExecutor(expression, clazz));
        uriHandlerMap.addHandler(handler);
      }
    }
    return uriHandlerMap;
  }

  //
  // A very simple plural stemmer to determine whether a noun is of singular or plural form.
  //
  private boolean isPlural(String name) {
    if (name.endsWith("ies") && (!name.endsWith("eies") || !name.endsWith("aies"))) {
      return true;
    } else if (name.endsWith("es") && (!name.endsWith("aes") && !name.endsWith("ees") && !name.endsWith("oes"))) {
      return true;
    } else if (name.endsWith("s") && (!name.endsWith("us") && !name.endsWith("ss"))) {
      return true;
    }

    return false;
  }

  private Class<?> findTypeFromCreateMethod(Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length == 1) {
      return parameterTypes[0];
    }
    return null;
  }

  private Class<?> findTypefromReadCollectionMethod(Method method) {
    Type returnType = method.getGenericReturnType();
    if (returnType instanceof ParameterizedType) {
      ParameterizedType type = (ParameterizedType) returnType;
      Type[] typeArguments = type.getActualTypeArguments();
      return (Class<?>) typeArguments[0];
    }
    return null;
  }

  private Class<?> findTypeFromUpdateMethod(Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length == 1) {
      return parameterTypes[0];
    }
    return null;
  }

  private Class<?> findTypeFromReadMethod(Method method) {
    return method.getReturnType();
  }
}
