// ConsoleProgressWriter.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

public class ConsoleProgressWriter implements ProgressWriter
{

  @Override
  public void log(String xiMessage)
  {
    System.err.println(xiMessage);
  }
}
