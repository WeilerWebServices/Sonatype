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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Provides a simple interface for encrypting/decrypting.
 *
 * @since 1.0
 */
public interface Encryptor
{

    void generateKeys( File publicKey, File privateKey )
        throws GeneralSecurityException, IOException;

    void generateKeys( OutputStream publicKeyOut, OutputStream privateKeyOut )
        throws GeneralSecurityException, IOException;

    void encrypt( File source, File destination, File publicKey )
        throws IOException, GeneralSecurityException;

    void encrypt( InputStream plainInput, OutputStream encryptedOutput, InputStream publickKey )
        throws IOException, GeneralSecurityException;

    void encrypt( InputStream plainInput, OutputStream encryptedOutput, PublicKey key )
        throws IOException, GeneralSecurityException;

    void decrypt( File source, File destination, File privateKey )
        throws IOException, GeneralSecurityException;

    void decrypt( InputStream encryptedInput, OutputStream plainOutput, InputStream secretKey )
        throws IOException, GeneralSecurityException;

    void decrypt( InputStream encryptedInput, OutputStream plainOutput, PrivateKey key )
        throws IOException, GeneralSecurityException;

    void encrypt( File problemReportBundle, File encryptedZip, InputStream publicKey )
        throws IOException, GeneralSecurityException;

}
