/**
 * Copyright (c) 2007-2011 Sonatype, Inc. All rights reserved.
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
package org.sonatype.sisu.encryptor;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import org.codehaus.plexus.util.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Tests for {@link RsaAesEncryptor}.
 */
public class RsaAesEncryptorTest
    implements Module
{
    private static final String ENCRYPTED_TEXT;

    private static final String PUBLIC_KEY;

    private static final String PRIVATE_KEY;

    private static final String BIG_TEXT_TO_ENCRYPT;

    static
    {
        try
        {
            ENCRYPTED_TEXT = IOUtil.toString( RsaAesEncryptorTest.class.getResourceAsStream("text.enc") );
            PUBLIC_KEY = IOUtil.toString( RsaAesEncryptorTest.class.getResourceAsStream("UT-public-key.txt") );
            PRIVATE_KEY = IOUtil.toString( RsaAesEncryptorTest.class.getResourceAsStream("UT-private-key.txt") );
            BIG_TEXT_TO_ENCRYPT = IOUtil.toString( RsaAesEncryptorTest.class.getResourceAsStream("text.txt") );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private RsaAesEncryptor encryptor;

    @Before
    public void setUp()
        throws Exception
    {
        Injector injector = Guice.createInjector(Stage.DEVELOPMENT, this);
        encryptor = injector.getInstance(RsaAesEncryptor.class);
    }

    public void configure(final Binder binder) {
        binder.bind(Encryptor.class).to(RsaAesEncryptor.class);
    }

    @After
    public void tearDown()
        throws Exception
    {
        encryptor = null;
    }

    @Test
    public void testGenerateKeys()
        throws GeneralSecurityException, IOException
    {
        ByteArrayOutputStream publicKeyOut = new ByteArrayOutputStream();
        ByteArrayOutputStream privateKeyOut = new ByteArrayOutputStream();

        encryptor.generateKeys( publicKeyOut, privateKeyOut );

        byte[] publicBytes = publicKeyOut.toByteArray();
        byte[] privateBytes = privateKeyOut.toByteArray();
        assertFalse( publicBytes.length == 0 );
        assertFalse( privateBytes.length == 0 );
        assertTrue( privateBytes.length > publicBytes.length );

        assertFalse( new String( publicBytes ).equals( PUBLIC_KEY ) );
        assertFalse( new String( privateBytes ).equals( PRIVATE_KEY ) );

        System.out.println( "Public key:\n" + new String( publicBytes ) );
        System.out.println( "Private key:\n" + new String( privateBytes ) );

        final ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream();
        encryptor.encrypt( new ByteArrayInputStream( "Simple decryption test!".getBytes() ), encryptedOut,
                           new ByteArrayInputStream( publicBytes ) );

        String encryptedText = new String( encryptedOut.toByteArray() );
        System.out.println( "Encrypted text:\n" + encryptedText );
    }

    @Test
    public void testKeyRead()
        throws Exception
    {
        PublicKey publicKey =
            encryptor.readPublicKey( new ByteArrayInputStream( RsaAesEncryptorTest.PUBLIC_KEY.getBytes() ) );
        assertNotNull( publicKey );

        PrivateKey privateKey =
            encryptor.readPrivateKey( new ByteArrayInputStream( RsaAesEncryptorTest.PRIVATE_KEY.getBytes() ) );
        assertNotNull( privateKey );
    }

    @Test
    public void donttestEncrypt()
        throws Exception
    {
        final ByteArrayOutputStream plainOutput = new ByteArrayOutputStream();
        encryptor.encrypt( new ByteArrayInputStream( "Simple decryption test!".getBytes() ), plainOutput,
                           new ByteArrayInputStream( PUBLIC_KEY.getBytes() ) );

        String encryptedText = new String( plainOutput.toByteArray() );
        System.out.println( encryptedText );
    }

    @Test
    public void testDecrypt()
        throws Exception
    {
        final ByteArrayOutputStream plainOutput = new ByteArrayOutputStream();
        encryptor.decrypt( new ByteArrayInputStream( ENCRYPTED_TEXT.getBytes() ), plainOutput,
                           new ByteArrayInputStream( PRIVATE_KEY.getBytes() ) );

        String decryptedText = new String( plainOutput.toByteArray() );
        assertEquals( "Simple decryption test!", decryptedText );
    }

    @Test
    public void testEncDec()
        throws Exception
    {
        String textToEncrypt = "This is a simple text to be encrypted!!!";

        final ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream();
        encryptor.encrypt( new ByteArrayInputStream( textToEncrypt.getBytes() ), encryptedOut,
                           new ByteArrayInputStream( PUBLIC_KEY.getBytes() ) );

        String encryptedText = new String( encryptedOut.toByteArray() );
        System.out.println( "Enc text" + encryptedText );
        assertFalse( textToEncrypt.equals( encryptedText ) );

        final ByteArrayOutputStream plainOutput = new ByteArrayOutputStream();
        encryptor.decrypt( new ByteArrayInputStream( encryptedOut.toByteArray() ), plainOutput,
                           new ByteArrayInputStream( PRIVATE_KEY.getBytes() ) );

        String decryptedText = new String( plainOutput.toByteArray() );
        assertEquals( textToEncrypt, decryptedText );
    }

    @Test
    public void testEncryptBigText()
        throws Exception
    {
        final ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream();
        encryptor.encrypt( new ByteArrayInputStream( BIG_TEXT_TO_ENCRYPT.getBytes() ), encryptedOut,
                           new ByteArrayInputStream( PUBLIC_KEY.getBytes() ) );

        final ByteArrayOutputStream plainOutput = new ByteArrayOutputStream();
        encryptor.decrypt( new ByteArrayInputStream( encryptedOut.toByteArray() ), plainOutput,
                           new ByteArrayInputStream( PRIVATE_KEY.getBytes() ) );

        assertEquals( BIG_TEXT_TO_ENCRYPT, new String( plainOutput.toByteArray() ) );
    }

    @Test
    public void testEncryptZip()
        throws Exception
    {
        byte[] zip = IOUtil.toByteArray( getClass().getResourceAsStream("compressed.zip") );
        final ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream();
        encryptor.encrypt( new ByteArrayInputStream( zip ), encryptedOut,
                           new ByteArrayInputStream( PUBLIC_KEY.getBytes() ) );

        final ByteArrayOutputStream plainOutput = new ByteArrayOutputStream();
        encryptor.decrypt( new ByteArrayInputStream( encryptedOut.toByteArray() ), plainOutput,
                           new ByteArrayInputStream( PRIVATE_KEY.getBytes() ) );

        assertTrue( Arrays.equals( zip, plainOutput.toByteArray() ) );
    }

}
