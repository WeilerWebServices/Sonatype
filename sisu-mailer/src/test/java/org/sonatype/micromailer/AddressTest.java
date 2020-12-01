/*
 * Copyright (c) 2008-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.micromailer;

import javax.mail.internet.InternetAddress;

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import static org.junit.Assert.*;

/**
 * Tests for {@link Address}.
 */
public class AddressTest
    extends TestSupport
{
    @Test
    public void testGood()
        throws Exception
    {
        Address adr = new Address( "a@b.c" );

        InternetAddress iadr = adr.getInternetAddress( "UTF-8" );

        assertEquals( "a@b.c", iadr.getAddress() );

        assertNull( "Should be null", iadr.getPersonal() );

        adr = new Address( "a@b.c", "Kaizer Soze" );

        iadr = adr.getInternetAddress( "UTF-8" );

        assertEquals( "a@b.c", iadr.getAddress() );

        assertEquals( "Kaizer Soze", iadr.getPersonal() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBad1()
    {
        Address adr = new Address( "abc" );
        fail( "Bad email address, should fail!" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBad2()
    {
        Address adr = new Address( "b-at-@!@bigula" );
        fail( "Bad email address, should fail!" );
    }
}
