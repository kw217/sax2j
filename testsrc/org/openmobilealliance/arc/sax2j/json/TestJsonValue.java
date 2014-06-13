// TestJsonValue.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestJsonValue
{
  private JsonValue mValue =
      JsonObject.create()
        .put("charlie", JsonNumber.create("52"))
        .put("alpha", JsonArray.create()
             .add(JsonString.create("Hello"))
             .add(JsonString.create("World")))
        .put("bravo", JsonNull.create())
        .put("delta", JsonArray.create())
        .put("echo", JsonArray.create()
             .add(JsonObject.create()
                  .put("nested", JsonString.create("tahi"))
                  .put("also", JsonNumber.create("2"))
                  .put("empty", JsonObject.create())));

  @Test
  public void testSimpleCompact()
  {
    RenderParams lParams = RenderParams.createCompact();
    StringBuilder lBuilder = new StringBuilder();
    mValue.render(lBuilder, lParams);
    String lCompact = "{\"charlie\": 52, \"alpha\": [\"Hello\", \"World\"], " +
        "\"bravo\": null, \"delta\": [], \"echo\": [{\"nested\": \"tahi\", " +
        "\"also\": 2, \"empty\": {}}]}";
    assertEquals(lCompact, lBuilder.toString());
  }

  @Test
  public void testSimplePretty()
  {
    RenderParams lParams = RenderParams.createPretty();
    StringBuilder lBuilder = new StringBuilder();
    mValue.render(lBuilder, lParams);
    String lPretty =
        "{ \"charlie\": 52,\n" +
        "  \"alpha\": [\n" +
        "    \"Hello\",\n" +
        "    \"World\"\n" +
        "  ],\n" +
        "  \"bravo\": null,\n" +
        "  \"delta\": [],\n" +
        "  \"echo\": [\n" +
        "    { \"nested\": \"tahi\",\n" +
        "      \"also\": 2,\n" +
        "      \"empty\": {}\n" +
        "    }\n" +
        "  ]\n" +
        "}";
    assertEquals(lPretty, lBuilder.toString());
  }
}
