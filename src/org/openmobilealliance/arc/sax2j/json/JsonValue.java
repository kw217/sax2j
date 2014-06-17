// JsonValue.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

/**
 * Common interface for a suite of RFC7159-compliant JSON classes.
 */
public interface JsonValue
{
  public void render(StringBuilder xiBuffer, RenderParams xiParams);
  boolean isSimple();
}
