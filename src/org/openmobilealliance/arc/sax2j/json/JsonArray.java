// JsonArray.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

import java.util.ArrayList;
import java.util.List;

public class JsonArray implements JsonValue
{
  private List<JsonValue> mValues = new ArrayList<JsonValue>();

  public static JsonArray create()
  {
    return new JsonArray();
  }

  private JsonArray()
  {
    // private constructor
  }

  public JsonArray add(JsonValue xiValue)
  {
    mValues.add(xiValue);
    return this;
  }

  @Override
  public void render(StringBuilder xiBuffer, RenderParams xiParams)
  {
    xiBuffer.append("[");

    boolean lFirst = true;
    for (JsonValue lValue : mValues)
    {
      if (lFirst)
      {
        lFirst = false;
      }
      else
      {
        xiBuffer.append(", ");
      }

      lValue.render(xiBuffer, xiParams);
    }

    xiBuffer.append("]");
  }
}
