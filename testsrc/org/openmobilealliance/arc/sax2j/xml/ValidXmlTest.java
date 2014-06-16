// ValidXmlTest.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.xml;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openmobilealliance.arc.sax2j.xml.XmlDocument;
import org.openmobilealliance.arc.sax2j.xml.XmlSchema;

/**
 * Check the parser accepts some valid documents.
 */
@RunWith(Parameterized.class)
public class ValidXmlTest
{
  private static final String SCHEMA_DIR = "testdata/schemas";
  private static final String XML_DIR = "testdata/xmldocs";

  @Parameters(name = "{index}: {1}-{0}")
  public static Collection<Object[]> data()
  {
    return Arrays.asList(new Object[][]
    {
     { "simple1.xsd", "simple1-ok.xml" },
     { "simple2.xsd", "simple2-ok.xml" },
     // TODO: Re-enable once Xerces2-J is fixed to support the revised lexical
     // space of xsd:float.
     // { "simple2.xsd", "simple2-ok2.xml" },
    });
  }

  private final String mSchemaName;
  private final String mDocName;

  private XmlSchema mSchema;
  private XmlDocument mDoc;

  public ValidXmlTest(String xiSchemaName, String xiDocName)
  {
    mSchemaName = xiSchemaName;
    mDocName = xiDocName;
  }

  @Test
  public void test() throws Exception
  {
    mSchema = new XmlSchema(new File(SCHEMA_DIR, mSchemaName));
    mSchema.parse();
    mDoc = new XmlDocument(mSchema, new File(XML_DIR, mDocName));
    mDoc.parse();
  }
}
