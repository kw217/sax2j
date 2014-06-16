// JsonObject.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A JS object (map). This JS object, unlike many other implementations,
 * maintains the order of keys added to it.
 */
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
    return put(xiKey, xiValue, false);
  }

  /**
   * Add a new value to the map.
   * @param xiKey
   * @param xiValue
   * @param xiCombine if there's a non-array value here already, turn it into
   * an array and add the new value on the end?
   * @return (fluid interface)
   */
  public JsonObject put(String xiKey, JsonValue xiValue, boolean xiCombine)
  {
    JsonValue lValue = mValues.get(xiKey);

    if (lValue == null)
    {
      mValues.put(xiKey, xiValue);
    }
    else
    {
      if (lValue instanceof JsonArray)
      {
        ((JsonArray)lValue).add(xiValue);
      }
      else if (xiCombine)
      {
        JsonArray lArray = JsonArray.create().add(lValue).add(xiValue);
        mValues.put(xiKey, lArray);
      }
      else
      {
        throw new RuntimeException("Attempted to add second value for " + xiKey);
      }
    }
    return this;
  }

  public boolean containsKey(String xiKey)
  {
    return mValues.containsKey(xiKey);
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
