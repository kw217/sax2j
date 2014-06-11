// XmlDocument.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

public class XmlDocument
{
  /**
   * The progress writer we use.
   */
  private ProgressWriter mProgress = new ProgressWriter.NullProgressWriter();

  /**
   * The schema we were parsed with.
   */
  private final XmlSchema mSchema;

  /**
   * Constructor. Accessible only from within package - constructed by
   * XmlSchema.
   * @param xiSchema the schema that parsed this.
   */
  XmlDocument(XmlSchema xiSchema)
  {
    mSchema = xiSchema;
  }

  public void setProgressWriter(ProgressWriter xiProgress)
  {
    mProgress = xiProgress;
  }
}
