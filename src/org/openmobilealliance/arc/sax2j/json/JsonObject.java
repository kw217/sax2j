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
  public void render(StringBuilder xiBuffer, RenderParams xiOuter)
  {
    xiBuffer.append("{");
//    xiBuffer.append(xiOuter.toString());
    RenderParams lInner = xiOuter.indent(true);

    boolean lFirst = true;
    for (Map.Entry<String, JsonValue> lEntry: mValues.entrySet())
    {
      String lKey = lEntry.getKey();
      JsonValue lValue = lEntry.getValue();

      if (lFirst)
      {
        lFirst = false;
        if (xiOuter.mDirtyLine)
        {
          lInner.doIndent(xiBuffer);
        }
        else
        {
          xiBuffer.append(xiOuter.mFirstSpacing);
        }
      }
      else
      {
        xiBuffer.append(",").append(xiOuter.mCommaSpacing);
        lInner.doIndent(xiBuffer);
      }

      JsonString.renderString(lKey, xiBuffer, lInner);

      xiBuffer.append(":").append(xiOuter.mColonSpacing);

      lValue.render(xiBuffer, lInner);
    }

    if (mValues.size() > 0)
    {
      xiOuter.doIndent(xiBuffer);
      xiBuffer.append(xiOuter.mLastSpacing);
    }

    xiBuffer.append("}");
  }
}
