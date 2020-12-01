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

import org.sonatype.sisu.resource.scanner.Listener;

public abstract class CompositeListenerExceptionPolicy
{

  public abstract void onBegin(Listener listener, Exception e);

  public abstract void onEnterDirectory(Listener listener, Exception e, File directory);

  public abstract void onExitDirectory(Listener listener, Exception e, File directory);

  public abstract void onFile(Listener listener, Exception e, File file);

  public abstract void onEnd(Listener listener, Exception e);

  public static final CompositeListenerExceptionPolicy ignore() {
    return new CompositeListenerExceptionPolicy()
    {
      @Override
      public void onBegin(Listener listener, Exception e) {
        // ignore
      }

      @Override
      public void onEnterDirectory(Listener listener, Exception e, File directory) {
        // ignore
      }

      @Override
      public void onExitDirectory(Listener listener, Exception e, File directory) {
        // ignore
      }

      @Override
      public void onFile(Listener listener, Exception e, File file) {
        // ignore
      }

      @Override
      public void onEnd(Listener listener, Exception e) {
        // ignore
      }
    };
  }

  public static final CompositeListenerExceptionPolicy rethrow() {
    return new CompositeListenerExceptionPolicy()
    {
      @Override
      public void onBegin(Listener listener, Exception e) {
        throw new RuntimeException(String.format(
            "Listener [%s] failed to execute on begining of processing with exception [%s]", listener, e.getMessage()),
            e);
      }

      @Override
      public void onEnterDirectory(Listener listener, Exception e, File directory) {
        throw new RuntimeException(String.format(
            "Listener [%s] failed to entering directory [%s] execute with exception [%s]", listener,
            directory.getAbsolutePath(), e.getMessage()), e);
      }

      @Override
      public void onExitDirectory(Listener listener, Exception e, File directory) {
        throw new RuntimeException(String.format(
            "Listener [%s] failed to execute on exiting directory [%s] with exception [%s]", listener,
            directory.getAbsolutePath(), e.getMessage()), e);
      }

      @Override
      public void onFile(Listener listener, Exception e, File file) {
        throw new RuntimeException(String.format("Listener [%s] failed to execute on file [%s] with exception [%s]",
            listener, file.getAbsolutePath(), e.getMessage()), e);
      }

      @Override
      public void onEnd(Listener listener, Exception e) {
        throw new RuntimeException(String.format(
            "Listener [%s] failed to execute on ending of processing with exception [%s]", listener, e.getMessage()), e);
      }
    };
  }

}
