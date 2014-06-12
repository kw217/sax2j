// TestJsonValue.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestJsonValue
{
  @Test
  public void testSimple()
  {
    JsonValue lValue = JsonObject.create()
        .put("charlie", JsonNumber.create("52"))
        .put("alpha", JsonArray.create()
             .add(JsonString.create("Hello"))
             .add(JsonString.create("World")))
        .put("bravo", JsonNull.create());

    RenderParams lParams = new RenderParams();
    StringBuilder lBuilder = new StringBuilder();
    lValue.render(lBuilder, lParams);
    assertEquals("{666}", lBuilder.toString());
  }
}
