// JsonNumber.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

/**
 * JSON number. We don't validate or translate - we assume the lexical
 * representation is provided to us correctly.
 */
public class JsonNumber implements JsonValue
{
  private String mValue;

  public static JsonNumber create(String xiValue)
  {
    return new JsonNumber(xiValue);
  }

  private JsonNumber(String xiValue)
  {
    // private constructor
    mValue = xiValue;
  }

  @Override
  public void render(StringBuilder xiBuffer, RenderParams xiParams)
  {
    xiBuffer.append(mValue);
  }

  @Override
  public boolean isSimple()
  {
    return true;
  }
}
