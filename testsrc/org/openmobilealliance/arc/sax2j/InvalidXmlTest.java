// XmlSchemaTest.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.SAXException;

/**
 * Check the validator correctly rejects some invalid documents.
 */
@RunWith(Parameterized.class)
public class InvalidXmlTest
{
  private static final String SCHEMA_DIR = "testdata/schemas";
  private static final String XML_DIR = "testdata/xmldocs";

  @Parameters(name = "{index}: {1}-{0}")
  public static Collection<Object[]> data()
  {
    return Arrays.asList(new Object[][]
    {
     { "simple1.xsd",  "simple1-bad.xml", "Invalid content was found starting with element 'nmsEvent'" },
     { "simple1.xsd",  "simple1-bad2.xml", "Invalid content was found starting with element 'flags'" },
     { "simple1.xsd",  "simple1-bad3.xml", "Invalid content was found starting with element 'resetbox'" },
     { "simple1.xsd",  "simple1-bad4.xml", "must be terminated by the matching end-tag" },
    });
  }


  private final String mSchemaName;
  private final String mDocName;
  private final String mMessage;

  private XmlSchema mSchema;
  private XmlDocument mDoc;

  public InvalidXmlTest(String xiSchemaName, String xiDocName, String xiMessage)
  {
    mSchemaName = xiSchemaName;
    mDocName = xiDocName;
    mMessage = xiMessage;
  }

  @Test
  public void test() throws Exception
  {
    mSchema = new XmlSchema(new File(SCHEMA_DIR, mSchemaName));
    try
    {
      mDoc = mSchema.parse(new File(XML_DIR, mDocName));
      fail("Should have thrown!");
    }
    catch (SAXException e)
    {
      assertThat(e.getMessage(), containsString(mMessage));
    }
  }

  @Test
  public void testAlt() throws Exception
  {
    mSchema = new XmlSchema(new File(SCHEMA_DIR, mSchemaName));
    try
    {
      mDoc = new XmlDocument(mSchema, new File(XML_DIR, mDocName));
      fail("Should have thrown!");
    }
    catch (SAXException e)
    {
      assertThat(e.getMessage(), containsString(mMessage));
    }
  }

}
