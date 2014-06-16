// JsonString.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

import org.apache.commons.lang3.StringEscapeUtils;

public class JsonString implements JsonValue
{
  private String mValue;

  public static JsonString create(String xiValue)
  {
    return new JsonString(xiValue);
  }

  private JsonString(String xiValue)
  {
    // private constructor
    mValue = xiValue;
  }

  @Override
  public void render(StringBuilder xiBuffer, RenderParams xiParams)
  {
    renderString(mValue, xiBuffer, xiParams);
  }

  /**
   * Render the given string as a JSON string.
   * @param xiValue
   * @param xiBuffer
   * @param xiParams
   */
  static void renderString(String xiValue, StringBuilder xiBuffer, RenderParams xiParams)
  {
    xiBuffer.append("\"");
    // TODO: Prevent this from escaping ordinary slashes "/".
    xiBuffer.append(StringEscapeUtils.escapeJson(xiValue));
    xiBuffer.append("\"");
  }
}
