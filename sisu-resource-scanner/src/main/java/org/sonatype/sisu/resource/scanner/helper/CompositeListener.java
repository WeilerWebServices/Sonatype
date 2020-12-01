/*
 * Copyright (c) 2010-2013 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 */
package org.sonatype.sisu.resource.scanner.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.sonatype.sisu.resource.scanner.Listener;

public class CompositeListener
    extends ListenerSupport
{

  private final Collection<Listener> listeners;

  private final CompositeListenerExceptionPolicy exceptionPolicy;

  public CompositeListener(Listener... listeners) {
    this(CompositeListenerExceptionPolicy.ignore(), listeners);
  }

  public CompositeListener(CompositeListenerExceptionPolicy exceptionPolicy, Listener... listeners) {
    this.exceptionPolicy = exceptionPolicy;
    this.listeners = new ArrayList<Listener>();
    if (listeners != null) {
      this.listeners.addAll(Arrays.asList(listeners));
    }
  }

  public CompositeListener add(Listener listener) {
    listeners.add(listener);
    return this;
  }

  public CompositeListener remove(Listener listener) {
    listeners.remove(listener);
    return this;
  }

  @Override
  public void onBegin() {
    for (Listener listener : listeners) {
      try {
        listener.onBegin();
      }
      catch (Exception e) {
        exceptionPolicy.onBegin(listener, e);
      }
    }
  }

  @Override
  public void onEnterDirectory(File directory) {
    for (Listener listener : listeners) {
      try {
        listener.onEnterDirectory(directory);
      }
      catch (Exception e) {
        exceptionPolicy.onEnterDirectory(listener, e, directory);
      }
    }
  }

  @Override
  public void onExitDirectory(File directory) {
    for (Listener listener : listeners) {
      try {
        listener.onExitDirectory(directory);
      }
      catch (Exception e) {
        exceptionPolicy.onExitDirectory(listener, e, directory);
      }
    }
  }

  @Override
  public void onFile(File file) {
    for (Listener listener : listeners) {
      try {
        listener.onFile(file);
      }
      catch (Exception e) {
        exceptionPolicy.onFile(listener, e, file);
      }
    }
  }

  @Override
  public void onEnd() {
    for (Listener listener : listeners) {
      try {
        listener.onEnd();
      }
      catch (Exception e) {
        exceptionPolicy.onEnd(listener, e);
      }
    }
  }

}
