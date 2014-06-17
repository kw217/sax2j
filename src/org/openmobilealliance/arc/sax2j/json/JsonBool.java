// JsonBool.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

public class JsonBool implements JsonValue
{
  private boolean mValue;

  private static JsonBool FALSE = new JsonBool(false);
  private static JsonBool TRUE = new JsonBool(true);

  public static JsonBool create(boolean xiValue)
  {
    return xiValue ? TRUE : FALSE;
  }

  private JsonBool(boolean xiValue)
  {
    // private constructor
    mValue = xiValue;
  }

  @Override
  public void render(StringBuilder xiBuffer, RenderParams xiParams)
  {
    xiBuffer.append(mValue ? "true" : "false");
  }

  @Override
  public boolean isSimple()
  {
    return true;
  }
}
