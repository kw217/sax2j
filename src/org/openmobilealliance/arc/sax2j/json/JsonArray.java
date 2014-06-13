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
  public void render(StringBuilder xiBuffer, RenderParams xiOuter)
  {
    xiBuffer.append("[");
//    xiBuffer.append(xiOuter.toString());
    RenderParams lInner = xiOuter.indent(false);

    boolean lFirst = true;
    for (JsonValue lValue : mValues)
    {
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

      lValue.render(xiBuffer, lInner);
    }

    if (mValues.size() > 0)
    {
      xiOuter.doIndent(xiBuffer);
      xiBuffer.append(xiOuter.mLastSpacing);
    }

    xiBuffer.append("]");
  }
}
