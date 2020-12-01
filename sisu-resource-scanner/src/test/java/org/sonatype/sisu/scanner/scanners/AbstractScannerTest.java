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
package org.sonatype.sisu.scanner.scanners;

import java.io.File;
import java.io.FileFilter;

import org.sonatype.sisu.litmus.testsupport.TestSupport;
import org.sonatype.sisu.resource.scanner.Listener;
import org.sonatype.sisu.resource.scanner.Scanner;

import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public abstract class AbstractScannerTest
    extends TestSupport
{

  /**
   * Scan a directory and checks that listener is called.
   */
  @Test
  public void filesAreScanned() throws Exception {
    Listener listener = mock(Listener.class);

    File dir = util.resolveFile("src/test/data");

    Scanner scanner = createScanner();
    scanner.scan(dir, listener);

    verify(listener).onBegin();
    verify(listener).onEnterDirectory(dir);
    verify(listener).onFile(new File(dir, "file1"));
    verify(listener).onExitDirectory(dir);
    verify(listener).onEnterDirectory(new File(dir, "dir1"));
    verify(listener).onExitDirectory(new File(dir, "dir1"));
    verify(listener).onFile(new File(dir, "dir1/file11"));
    verify(listener).onFile(new File(dir, "dir1/file12"));
    verify(listener).onEnterDirectory(new File(dir, "dir2"));
    verify(listener).onExitDirectory(new File(dir, "dir2"));
    verify(listener).onFile(new File(dir, "dir2/file21"));
    verify(listener).onEnd();
  }

  /**
   * Scan a directory, filter out some directory and checks that listener is called.
   */
  @Test
  public void filtering() throws Exception {
    Listener listener = mock(Listener.class);

    File dir = util.resolveFile("src/test/data");

    Scanner scanner = createScanner();
    scanner.scan(dir, listener, new FileFilter()
    {
      public boolean accept(File file) {
        return !"dir2".equals(file.getName());
      }
    });

    verify(listener).onBegin();
    verify(listener).onEnterDirectory(dir);
    verify(listener).onFile(new File(dir, "file1"));
    verify(listener).onExitDirectory(dir);
    verify(listener).onEnterDirectory(new File(dir, "dir1"));
    verify(listener).onExitDirectory(new File(dir, "dir1"));
    verify(listener).onFile(new File(dir, "dir1/file11"));
    verify(listener).onFile(new File(dir, "dir1/file12"));
    verify(listener, never()).onEnterDirectory(new File(dir, "dir2"));
    verify(listener, never()).onExitDirectory(new File(dir, "dir2"));
    verify(listener, never()).onFile(new File(dir, "dir2/file21"));
    verify(listener).onEnd();
  }

  /**
   * Checks that onEnter/onExit directory are not called for an inexistent file.
   */
  @Test
  public void onEnterAndOnExitAreNotCalledForInexistingDir() throws Exception {
    Listener listener = mock(Listener.class);

    File dir = util.resolveFile("src/test/fake");

    Scanner scanner = createScanner();
    scanner.scan(dir, listener);

    verify(listener).onBegin();
    verify(listener, never()).onEnterDirectory(Matchers.any(File.class));
    verify(listener, never()).onFile(Matchers.any(File.class));
    verify(listener, never()).onExitDirectory(Matchers.any(File.class));
    verify(listener).onEnd();
  }

  protected abstract Scanner createScanner();

}
