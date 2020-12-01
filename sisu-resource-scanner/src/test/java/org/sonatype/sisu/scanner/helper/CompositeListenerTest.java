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
package org.sonatype.sisu.scanner.helper;

import java.io.File;

import org.sonatype.sisu.litmus.testsupport.TestSupport;
import org.sonatype.sisu.resource.scanner.Listener;
import org.sonatype.sisu.resource.scanner.helper.CompositeListener;
import org.sonatype.sisu.resource.scanner.helper.CompositeListenerExceptionPolicy;

import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class CompositeListenerTest
    extends TestSupport
{

  /**
   * Creating & using a composite listener without any member listeners should not fail.
   */
  @Test
  public void noListener() throws Exception {
    CompositeListener compositeListener = new CompositeListener();
    compositeListener.onBegin();
    compositeListener.onEnterDirectory(null);
    compositeListener.onFile(null);
    compositeListener.onExitDirectory(null);
    compositeListener.onEnd();
  }

  /**
   * Member listeners are called.
   */
  @Test
  public void listenersAreCalled() throws Exception {
    Listener listener1 = mock(Listener.class);
    Listener listener2 = mock(Listener.class);

    CompositeListener compositeListener = new CompositeListener(listener1, listener2);

    File dir = util.getTargetDir();
    File file = new File(dir, "file");

    compositeListener.onBegin();
    compositeListener.onEnterDirectory(dir);
    compositeListener.onFile(file);
    compositeListener.onExitDirectory(dir);
    compositeListener.onEnd();

    verify(listener1).onBegin();
    verify(listener1).onEnterDirectory(dir);
    verify(listener1).onFile(file);
    verify(listener1).onExitDirectory(dir);
    verify(listener1).onEnd();

    verify(listener2).onBegin();
    verify(listener2).onEnterDirectory(dir);
    verify(listener2).onFile(file);
    verify(listener2).onExitDirectory(dir);
    verify(listener2).onEnd();
  }

  /**
   * Test that exception policy is called when listener throws exception.
   */
  @Test
  public void exceptionPolicyIsCalled() throws Exception {
    Listener listener = mock(Listener.class);
    CompositeListenerExceptionPolicy policy = mock(CompositeListenerExceptionPolicy.class);

    CompositeListener compositeListener = new CompositeListener(policy, listener);

    File dir = util.getTargetDir();
    File file = new File(dir, "file");

    RuntimeException e = new RuntimeException("test");
    doThrow(e).when(listener).onBegin();
    doThrow(e).when(listener).onEnterDirectory(dir);
    doThrow(e).when(listener).onExitDirectory(dir);
    doThrow(e).when(listener).onFile(file);
    doThrow(e).when(listener).onEnd();

    compositeListener.onBegin();
    compositeListener.onEnterDirectory(dir);
    compositeListener.onFile(file);
    compositeListener.onExitDirectory(dir);
    compositeListener.onEnd();

    verify(policy).onBegin(Matchers.<Listener> any(), Matchers.eq(e));
    verify(policy).onEnterDirectory(Matchers.<Listener> any(), Matchers.eq(e), Matchers.eq(dir));
    verify(policy).onFile(Matchers.<Listener> any(), Matchers.eq(e), Matchers.eq(file));
    verify(policy).onExitDirectory(Matchers.<Listener> any(), Matchers.eq(e), Matchers.eq(dir));
    verify(policy).onEnd(Matchers.<Listener> any(), Matchers.eq(e));
  }

  /**
   * Test that all listeners are called when policy does ignores exception.
   */
  @Test
  public void allListenersGetsCalledOnNonThrowingPolicy() throws Exception {
    Listener listener1 = mock(Listener.class);
    Listener listener2 = mock(Listener.class);
    CompositeListenerExceptionPolicy policy = mock(CompositeListenerExceptionPolicy.class);

    CompositeListener compositeListener = new CompositeListener(policy, listener1, listener2);

    File dir = util.getTargetDir();
    File file = new File(dir, "file");

    RuntimeException e = new RuntimeException("test");
    doThrow(e).when(listener1).onBegin();
    doThrow(e).when(listener1).onEnterDirectory(dir);
    doThrow(e).when(listener1).onExitDirectory(dir);
    doThrow(e).when(listener1).onFile(file);
    doThrow(e).when(listener1).onEnd();

    compositeListener.onBegin();
    compositeListener.onEnterDirectory(dir);
    compositeListener.onFile(file);
    compositeListener.onExitDirectory(dir);
    compositeListener.onEnd();

    verify(listener2).onBegin();
    verify(listener2).onEnterDirectory(dir);
    verify(listener2).onFile(file);
    verify(listener2).onExitDirectory(dir);
    verify(listener2).onEnd();
  }

  /**
   * Test that if policy re-throws the exception following listeners are not called.
   */
  @Test
  public void listenersAreNotCalledOnThrowingPolicy() throws Exception {
    Listener listener1 = mock(Listener.class);
    Listener listener2 = mock(Listener.class);
    CompositeListenerExceptionPolicy policy = mock(CompositeListenerExceptionPolicy.class);

    CompositeListener compositeListener = new CompositeListener(policy, listener1, listener2);

    File dir = util.getTargetDir();
    File file = new File(dir, "file");

    RuntimeException e = new RuntimeException("test");
    doThrow(e).when(listener1).onBegin();
    doThrow(e).when(listener1).onEnterDirectory(dir);
    doThrow(e).when(listener1).onExitDirectory(dir);
    doThrow(e).when(listener1).onFile(file);
    doThrow(e).when(listener1).onEnd();

    doThrow(e).when(policy).onBegin(Matchers.<Listener> any(), Matchers.eq(e));
    doThrow(e).when(policy).onEnterDirectory(Matchers.<Listener> any(), Matchers.eq(e), Matchers.eq(dir));
    doThrow(e).when(policy).onFile(Matchers.<Listener> any(), Matchers.eq(e), Matchers.eq(file));
    doThrow(e).when(policy).onExitDirectory(Matchers.<Listener> any(), Matchers.eq(e), Matchers.eq(dir));
    doThrow(e).when(policy).onEnd(Matchers.<Listener> any(), Matchers.eq(e));

    try {
      compositeListener.onBegin();
    }
    catch (Exception ignore) {
      // ignore;
    }
    try {
      compositeListener.onEnterDirectory(dir);
    }
    catch (Exception ignore) {
      // ignore;
    }
    try {
      compositeListener.onFile(file);
    }
    catch (Exception ignore) {
      // ignore;
    }
    try {
      compositeListener.onExitDirectory(dir);
    }
    catch (Exception ignore) {
      // ignore;
    }
    try {
      compositeListener.onEnd();
    }
    catch (Exception ignore) {
      // ignore;
    }

    verify(listener2, never()).onBegin();
    verify(listener2, never()).onEnterDirectory(dir);
    verify(listener2, never()).onFile(file);
    verify(listener2, never()).onExitDirectory(dir);
    verify(listener2, never()).onEnd();
  }

}
