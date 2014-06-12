// JsonNull.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

public class JsonNull implements JsonValue
{
  private static JsonNull NULL = new JsonNull();

  public static JsonNull create()
  {
    return NULL;
  }

  private JsonNull()
  {
    // private constructor
  }

  @Override
  public void render(StringBuilder xiBuffer, RenderParams xiParams)
  {
    xiBuffer.append("null");
  }
}
