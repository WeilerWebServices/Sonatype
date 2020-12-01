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

import javax.inject.Inject;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.sonatype.micromailer.imp.DefaultMailType;
import org.sonatype.micromailer.imp.HtmlMailType;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import org.sonatype.sisu.litmus.testsupport.inject.InjectedTestSupport;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link EMailer}.
 */
public class EMailerTest
    extends InjectedTestSupport
{
    @Inject
    private EMailer eMailer;

    private GreenMail server;

    @After
    public void stopServer()
    {
        if ( server != null )
        {
            server.stop();
        }
    }

    @Test
    public void withoutConfiguration()
        throws Exception
    {
        MailRequest request = new MailRequest( "testId", DefaultMailType.DEFAULT_TYPE_ID );
        request.getBodyContext().put( DefaultMailType.SUBJECT_KEY, "Test" );
        request.getBodyContext().put( DefaultMailType.BODY_KEY, "Body" );

        MailRequestStatus status = eMailer.sendMail( request );

        int count = 0;

        while ( !status.isSent() && count < 10 )
        {
            Thread.sleep( 100 );
            count++;
        }

        assertThat(status.isSent(), is(false));
        assertThat(status, notNullValue());
        assertThat(status.getErrorCause(), notNullValue());

        assertThat(MailCompositionMessagingException.class.isAssignableFrom(status.getErrorCause().getClass()), is(true));
    }

    @Test
    public void tealOnLocalhost()
        throws Exception
    {
        final String host = "localhost";
        final int port = 42358;
        final String username = "smtp-username";
        final String password = "smtp-password";
        final String systemMailAddress = "system@nexus.org";

        // mail server config
        ServerSetup smtp = new ServerSetup( port, null, ServerSetup.PROTOCOL_SMTP );
        server = new GreenMail( smtp );
        server.setUser( systemMailAddress, username, password );
        server.start();

        // mailer config
        EmailerConfiguration config = new EmailerConfiguration();
        config.setMailHost( host );
        config.setMailPort( port );
        config.setUsername( username );
        config.setPassword( password );
        config.setSsl( false );
        config.setTls( false );
        config.setDebug( true );

        eMailer.configure( config );

        // prepare a mail request
        MailRequest request = new MailRequest( "Mail-Test", HtmlMailType.HTML_TYPE_ID );
        request.setFrom( new Address( systemMailAddress, "Nexus Manager" ) );
        request.getToAddresses().add( new Address( "user1@nexus.org" ) );
        request.getToAddresses().add( new Address( "user2@nexus.org" ) );
        request.getToAddresses().add( new Address( "user3@nexus.org" ) );
        request.getToAddresses().add( new Address( "user4@nexus.org" ) );
        request.getBodyContext().put( DefaultMailType.SUBJECT_KEY, "Nexus: Mail Test Begin." );
        StringBuilder body = new StringBuilder();
        body.append( "The following artifacts have been staged to the test Repository:<br><br>" );
        body
            .append( "<i>(Set the Base URL parameter in Nexus Server Administration to retrieve links to these artifacts in future emails)</i><br><br>" );
        body.append( "<a href='http://www.sonatype.com'>Sonatype</a><br>" );
        request.getBodyContext().put( DefaultMailType.BODY_KEY, body.toString() );

        // send the mail
        eMailer.sendMail( request );

        // validate
        long timeout = 1000;
        if ( !server.waitForIncomingEmail( timeout, 1 ) )
        {
            fail( "Could not receive any email in a timeout of " + timeout );
        }
        MimeMessage msgs[] = server.getReceivedMessages();
        assertEquals( 4, msgs.length );
        String receivedBody = GreenMailUtil.getBody( msgs[0] );
        assertTrue( receivedBody.contains( "Sonatype" ) );

    }

    @Test
    public void emptyUsername()
    {
        final String host = "localhost";
        final int port = 12345;

        // mailer config
        EmailerConfiguration config = new EmailerConfiguration();
        config.setMailHost( host );
        config.setMailPort( port );
        config.setUsername( null );
        config.setPassword( null );
        config.setSsl( false );
        config.setTls( false );
        config.setDebug( true );

        // should be null
        Assert.assertNull( config.getAuthenticator() );

        config.setUsername( "" );
        config.setPassword( null );
        // should be null
        Assert.assertNull( config.getAuthenticator() );

        config.setUsername( null );
        config.setPassword( "" );
        // should be null
        Assert.assertNull( config.getAuthenticator() );
        
        config.setUsername( "user" );
        config.setPassword( "" );
        // should be null
        Assert.assertNotNull( config.getAuthenticator() );
        
        config.setUsername( "" );
        config.setPassword( "invalid" );
        // should be null
        Assert.assertNull( config.getAuthenticator() );
        
    }

    @Test
    public void mailParts()
        throws Exception
    {
        final String host = "localhost";
        final int port = 42358;
        final String systemMailAddress = "system@nexus.org";

        // mail server config
        ServerSetup smtp = new ServerSetup( port, null, ServerSetup.PROTOCOL_SMTP );
        server = new GreenMail( smtp );
        server.start();

        // mailer config
        EmailerConfiguration config = new EmailerConfiguration();
        config.setMailHost( host );
        config.setMailPort( port );
        config.setSsl( false );
        config.setTls( false );
        config.setDebug( true );

        eMailer.configure( config );

        // prepare a mail request
        MailRequest request = new MailRequest( "Mail-Test", HtmlMailType.HTML_TYPE_ID );
        request.setFrom( new Address( systemMailAddress, "Nexus Manager" ) );
        request.getToAddresses().add( new Address( "user1@nexus.org" ) );
        request.getBodyContext().put( DefaultMailType.SUBJECT_KEY, "Mail Test Begin." );
        request.getBodyContext().put( DefaultMailType.BODY_KEY, "Hello World" );

        MailPart part = new MailPart();
        part.setContent( "Embedded-Image", "text/plain" );
        part.setDisposition( "inline" );
        part.setContentId( "<foo@acme.org>" );
        part.setHeader( "X-Test", "testme" );
        request.addPart( part );

        // send the mail
        eMailer.sendMail( request );

        // validate
        long timeout = 1000;
        if ( !server.waitForIncomingEmail( timeout, 1 ) )
        {
            fail( "Could not receive any email in a timeout of " + timeout );
        }
        MimeMessage msgs[] = server.getReceivedMessages();
        assertEquals( 1, msgs.length );
        Multipart mp = (Multipart) msgs[0].getContent();
        MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart( 1 );
        assertEquals( part.getDisposition(), bp.getDisposition() );
        assertEquals( part.getContentId(), bp.getContentID() );
        assertEquals( "testme", bp.getHeader( "X-Test", null ) );
    }

}
