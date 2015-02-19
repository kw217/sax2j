// ExtractorTest.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.extract;

import org.junit.Test;
import org.openmobilealliance.arc.sax2j.ConsoleProgressWriter;

public class ExtractorTest
{

  @Test
  public void test() throws Exception
  {
    Extractor lExtractor = new Extractor("C:/code/xml2json/extracted/OMA-TS-REST_NetAPI_NMS-V1_0-20140527-D_CR123.txt",
                                         "C:/code/xml2json/result/xmldata_",
                                         ".xml.json");
    lExtractor.setProgressWriter(new ConsoleProgressWriter());
    lExtractor.extract();
  }

}
