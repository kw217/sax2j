// ParseSanityTest.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

/**
 * Check that we don't allow foolish behaviour.
 */
public class ParseSanityTest
{

  @Test
  public void testSchemaMustBeParsed() throws Exception
  {
    XmlSchema lSchema = new XmlSchema(new File("testdata/schemas",
                                               "simple1.xsd"));
    XmlDocument lDoc = new XmlDocument(lSchema, new File("testdata/xmldocs",
                                                         "simple1-ok.xml"));
    try
    {
      lDoc.parse();
      fail("Should fail");
    }
    catch (RuntimeException e)
    {
      // expected
    }
  }

  @Test
  public void testCantParseSchemaTwice() throws Exception
  {
    XmlSchema lSchema = new XmlSchema(new File("testdata/schemas",
                                               "simple1.xsd"));
    lSchema.parse();
    try
    {
      lSchema.parse();
      fail("Should fail");
    }
    catch (RuntimeException e)
    {
      // expected
    }
  }

  @Test
  public void testCantParseDocTwice() throws Exception
  {
    XmlSchema lSchema = new XmlSchema(new File("testdata/schemas",
                                               "simple1.xsd"));
    lSchema.parse();
    XmlDocument lDoc = new XmlDocument(lSchema, new File("testdata/xmldocs",
        "simple1-ok.xml"));

    lDoc.parse();

    try
    {
      lDoc.parse();
      fail("Should fail");
    }
    catch (RuntimeException e)
    {
      // expected
    }
  }
}
