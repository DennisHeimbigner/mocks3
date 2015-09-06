/* Copyright Alan Gutierrez 2006 */
package com.snipreel.mocks3;

import java.io.IOException;

import junit.framework.TestCase;

public class S3InterfaceTest
extends TestCase
{
    public void testNothing() throws IOException
    {
        S3ConnectionFactory factory = new S3ConnectionFactory("s3.snipreel.com");
        S3Connection s3Connection = factory.newConnection("GET", "s3.snipreel.com", "database", "/images/puppy.jpg");
        s3Connection.setHeader("Date", "Tue, 27 Mar 2007 19:36:42 +0000");
        String expect = "GET\n" +
            "\n" +
            "\n" +
            "Tue, 27 Mar 2007 19:36:42 +0000\n" +
            "/database/images/puppy.jpg\n";
        assertEquals(expect, s3Connection.getSignaturePlainText());
    }
}

/* vim: set et sw=4 ts=4 ai tw=78 nowrap: */