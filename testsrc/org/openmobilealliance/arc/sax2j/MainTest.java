// MainTest.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import org.junit.Test;

public class MainTest
{
  @Test
  public void test() throws Exception
  {
    Main.main(new String[] { "testdata/schemas/simple1.xsd", "testdata/xmldocs/simple1-ok.xml" });
  }
}
