/*******************************************************************************
 * Copyright (c) 2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/
package com.google.inject.tools.ideplugin.eclipse;

import java.lang.reflect.Method;
import java.util.Collections;

import org.sonatype.inject.BeanScanning;

import com.google.inject.Module;

public class SisuIndex {
  public static Iterable<Module> bindings() {
    try {
      Class<?> main = Class.forName("org.sonatype.guice.bean.containers.Main");
      Method wire = main.getDeclaredMethod("wire", BeanScanning.class, Module[].class);
      return Collections.singleton((Module)wire.invoke(null, BeanScanning.INDEX, new Module[0]));
    } catch (Throwable e) {
      throw new RuntimeException("Unable to scan Sisu index", e);
    }
  }
}
