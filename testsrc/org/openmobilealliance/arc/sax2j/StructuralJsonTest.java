// StructuralJsonTest.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Check the parser accepts some valid documents.
 */
@RunWith(Parameterized.class)
public class StructuralJsonTest
{
  private static final String SCHEMA_DIR = "testdata/schemas";
  private static final String XML_DIR = "testdata/xmldocs";
  private static final String JSON_DIR = "testdata/sa-json";

  @Parameters(name = "{index}: {1}-{0}")
  public static Collection<Object[]> data()
  {
    return Arrays.asList(new Object[][]
    {
     { "simple1.xsd", "simple1-ok.xml", "simple1-ok.json" },
    });
  }

  private final String mSchemaName;
  private final String mDocName;
  private final String mJsonName;

  private XmlSchema mSchema;
  private XmlDocument mDoc;

  public StructuralJsonTest(String xiSchemaName,
                            String xiDocName,
                            String xiJsonName)
  {
    mSchemaName = xiSchemaName;
    mDocName = xiDocName;
    mJsonName = xiJsonName;
  }

  @Test
  public void test() throws Exception
  {
    File lFile = new File(JSON_DIR, mJsonName);
    String lExpected = new String(Files.readAllBytes(lFile.toPath()), "UTF-8");

    mSchema = new XmlSchema(new File(SCHEMA_DIR, mSchemaName));
    mSchema.parse();
    mDoc = new XmlDocument(mSchema, new File(XML_DIR, mDocName));
    mDoc.parse();

    String lActual = mDoc.toJson();
    assertEquals("JSON document generated from " + mDocName + " and " + mSchemaName,
                 lExpected,
                 lActual);
  }
}
