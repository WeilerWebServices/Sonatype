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
package org.sonatype.sisu.resource.scanner.scanners;

import java.io.File;
import java.io.FileFilter;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.resource.scanner.Listener;
import org.sonatype.sisu.resource.scanner.Scanner;

@Named("serial")
@Singleton
public class SerialScanner
    implements Scanner
{

  public void scan(File directory, Listener listener) {
    scan(directory, listener, null);
  }

  public void scan(File directory, Listener listener, FileFilter filter) {
    if (listener == null) {
      return;
    }
    listener.onBegin();
    recurse(directory, listener, filter);
    listener.onEnd();
  }

  private void recurse(File directory, Listener listener, FileFilter filter) {
    if (!directory.exists()) {
      return;
    }
    listener.onEnterDirectory(directory);
    File[] files = filter == null ? directory.listFiles() : directory.listFiles(filter);
    if (files != null) {
      for (final File file : files) {
        if (file.isDirectory()) {
          recurse(file, listener, filter);
        }
        else {
          listener.onFile(file);
        }
      }
    }
    listener.onExitDirectory(directory);
  }

}
