// JsonObject.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonObject implements JsonValue
{
  private LinkedHashMap<String, JsonValue> mValues =
      new LinkedHashMap<String, JsonValue>();

  public static JsonObject create()
  {
    return new JsonObject();
  }

  private JsonObject()
  {
    // private constructor
  }

  public JsonObject put(String xiKey, JsonValue xiValue)
  {
    mValues.put(xiKey, xiValue);
    return this;
  }

  @Override
  public void render(StringBuilder xiBuffer, RenderParams xiParams)
  {
    xiBuffer.append("{");

    boolean lFirst = true;
    for (Map.Entry<String, JsonValue> lEntry: mValues.entrySet())
    {
      String lKey = lEntry.getKey();
      JsonValue lValue = lEntry.getValue();

      if (lFirst)
      {
        lFirst = false;
      }
      else
      {
        xiBuffer.append(", ");
      }

      JsonString.renderString(lKey, xiBuffer, xiParams);

      xiBuffer.append(":");

      lValue.render(xiBuffer, xiParams);
    }

    xiBuffer.append("}");
  }
}
