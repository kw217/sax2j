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
             .add(JsonString.create("World"))
             .add(JsonString.create("\"\\/\b\f\n\r\t\'<>&\u0000\u4eac\u90fd")))
        .put("bravo", JsonNull.create())
        .put("delta", JsonArray.create())
        .put("echo", JsonArray.create()
             .add(JsonObject.create()
                  .put("nested", JsonString.create("tahi"))
                  .put("also", JsonNumber.create("2"))
                  .put("empty", JsonObject.create())))
        .put("foxtrot", JsonArray.create()
             .add(JsonObject.create()
                  .put("a", JsonNumber.create("1"))
                  .put("b", JsonNumber.create("2"))))
        .put("golf", JsonArray.create()
             .add(JsonObject.create()
                  .put("say", JsonString.create("geronimo!"))))
        .put("hotel", JsonArray.create()
             .add(JsonString.create("geronimo!")));

  @Test
  public void testSimpleCompact()
  {
    RenderParams lParams = RenderParams.createCompact();
    StringBuilder lBuilder = new StringBuilder();
    mValue.render(lBuilder, lParams);
    String lCompact = "{\"charlie\": 52, \"alpha\": [\"Hello\", \"World\", " +
        "\"\\\"\\\\/\\b\\f\\n\\r\\t'<>&\\u0000\\u4EAC\\u90FD\"], " +
        "\"bravo\": null, \"delta\": [], \"echo\": [{\"nested\": \"tahi\", " +
        "\"also\": 2, \"empty\": {}}], \"foxtrot\": [{\"a\": 1, \"b\": 2}], " +
        "\"golf\": [{\"say\": \"geronimo!\"}], \"hotel\": [\"geronimo!\"]}";

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
        "    \"World\",\n" +
        "    \"\\\"\\\\/\\b\\f\\n\\r\\t'<>&\\u0000\\u4EAC\\u90FD\"\n" +
        "  ],\n" +
        "  \"bravo\": null,\n" +
        "  \"delta\": [],\n" +
        "  \"echo\": [\n" +
        "    { \"nested\": \"tahi\",\n" +
        "      \"also\": 2,\n" +
        "      \"empty\": {}\n" +
        "    }\n" +
        "  ],\n" +
        "  \"foxtrot\": [\n" +
        "    { \"a\": 1,\n" +
        "      \"b\": 2\n" +
        "    }\n" +
        "  ],\n" +
        "  \"golf\": [ { \"say\": \"geronimo!\" } ],\n" +
        "  \"hotel\": [ \"geronimo!\" ]\n" +
        "}";
    assertEquals(lPretty, lBuilder.toString());
  }
}
