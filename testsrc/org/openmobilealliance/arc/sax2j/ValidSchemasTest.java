// XmlSchemaTest.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Check the parser accepts some valid schemas.
 */
@RunWith(Parameterized.class)
public class ValidSchemasTest
{
  private static final String SCHEMA_DIR = "testdata/schemas";

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> data()
  {
    return Arrays.asList(new Object[][]
    {
     { "simple1.xsd" },
     { "simple2.xsd" },
     { "OMA-SUP-XSD_rest_netapi_nms-V1_0-20140527-D.xsd" },
    });
  }

  private final String mFilename;

  private XmlSchema mSchema;

  public ValidSchemasTest(String xiFilename)
  {
    mFilename = xiFilename;
  }

  @Test
  public void test() throws Exception
  {
    mSchema = new XmlSchema(new File(SCHEMA_DIR, mFilename));
    mSchema.parse();
  }
}
