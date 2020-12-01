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

import java.io.UnsupportedEncodingException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.sonatype.micromailer.imp.Strings;

/**
 * The address used in To, From, Sender, etc. fields. The mailbox with optional "personal" name of the mailbox.
 *
 * @author cstamas
 */
public class Address
{
    private final String mailAddress;

    private final String personal;

    public Address( final String mailAddress )
        throws IllegalArgumentException
    {
        this( mailAddress, null );
    }

    public Address( final String mailAddress, final String personal )
        throws IllegalArgumentException
    {
        this.mailAddress = mailAddress;
        this.personal = personal;
        validateAddress( this );
    }

    public String getMailAddress()
    {
        return mailAddress;
    }

    public String getPersonal()
    {
        return personal;
    }

    public InternetAddress getInternetAddress( final String encoding )
        throws AddressException, UnsupportedEncodingException
    {
        final InternetAddress adr = new InternetAddress( getMailAddress(), getPersonal(), encoding );
        adr.validate();
        return adr;
    }

    public String toString()
    {
        if ( Strings.isEmpty(getPersonal()) )
        {
            return "<" + getMailAddress() + ">";
        }
        else
        {
            return "\"" + getPersonal() + "\" <" + getMailAddress() + ">";
        }
    }

    // ==

    /**
     * Performs a formal validation of an e-mail address.
     *
     * @param address string representing an e-mail address.
     * @throws IllegalArgumentException
     */
    public static void validateAddress( final String address )
        throws IllegalArgumentException
    {
        if ( Strings.isEmpty( address ) )
        {
            throw new IllegalArgumentException( "E-mail address cannot be empty!" );
        }
        validateAddress( new Address( address ) );
    }

    /**
     * Performs a formal validation of an Address.
     *
     * @param address the address to validate
     * @throws IllegalArgumentException
     */
    public static void validateAddress( final Address address )
        throws IllegalArgumentException
    {
        if ( address == null )
        {
            throw new IllegalArgumentException( "E-mail address is null!" );
        }
        try
        {
            // this method perform validation too using Java Mail.
            address.getInternetAddress( EMailer.DEFAULT_ENCODING );
        }
        catch ( AddressException e )
        {
            throw new IllegalArgumentException( "Invalid e-mail address: " + address.toString(), e );
        }
        catch ( UnsupportedEncodingException e )
        {
            // huh? Emailer.DEFAULT_ENCODING not supported?
            throw new IllegalStateException( "EMailer needs a JVM that supports " + EMailer.DEFAULT_ENCODING
                + " encoding!" );
        }
    }
}
