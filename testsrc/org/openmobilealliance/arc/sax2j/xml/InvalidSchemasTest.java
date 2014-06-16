// InvalidSchemasTest.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.xml;

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
import org.openmobilealliance.arc.sax2j.xml.XmlSchema;
import org.xml.sax.SAXException;

/**
 * Check the parser correctly rejects some invalid schemas.
 */
@RunWith(Parameterized.class)
public class InvalidSchemasTest
{
  private static final String SCHEMA_DIR = "testdata/schemas";

  @Parameters(name = "{index}: {0}")
  public static Collection<Object[]> data()
  {
    return Arrays.asList(new Object[][]
    {
     { "bad1.xsd",  "must be terminated by the matching end-tag" },
     { "bad2.xsd",  "Cannot resolve the name 'Flag'" },
    });
  }

  private final String mFilename;
  private final String mMessage;

  private XmlSchema mSchema;

  public InvalidSchemasTest(String xiFilename, String xiMessage)
  {
    mFilename = xiFilename;
    mMessage = xiMessage;
  }

  @Test
  public void test()
  {
    mSchema = new XmlSchema(new File(SCHEMA_DIR, mFilename));

    try
    {
      mSchema.parse();
      fail("Should have thrown!");
    }
    catch (SAXException e)
    {
      assertThat(e.getMessage(), containsString(mMessage));
    }
  }
}
