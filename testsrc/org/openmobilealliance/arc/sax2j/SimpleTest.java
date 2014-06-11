// SimpleTest.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import java.io.File;

import org.junit.Test;

public class SimpleTest
{

  @Test
  public void test() throws Exception
  {
    ProgressWriter lProgress = new ConsoleProgressWriter();

    XmlSchema lSchema = new XmlSchema(new File("testdata/schemas",
                                               "simple1.xsd"));
    lSchema.setProgressWriter(lProgress);
    lSchema.parse();

    XmlDocument lDoc = new XmlDocument(lSchema, new File("testdata/xmldocs",
                                                         "simple1-ok.xml"));
    lDoc.setProgressWriter(lProgress);
    lDoc.parse();

    lDoc.walk();

  }

}
