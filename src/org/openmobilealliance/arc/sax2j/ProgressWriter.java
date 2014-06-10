// ProgressWriter.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

/**
 * Interface for reporting progress to user.
 */
public interface ProgressWriter
{
  /**
   * Report progress to user.
   *
   * @param xiMessage Message to display (with no trailing newline).
   */
  public void log(String xiMessage);
}
