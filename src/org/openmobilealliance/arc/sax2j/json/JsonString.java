// JsonString.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;
import org.apache.commons.lang3.text.translate.LookupTranslator;

public class JsonString implements JsonValue
{
  private String mValue;

  /**
   * JSON escaper, based on
   * org.apache.commons.lang3.StringEscapeUtils.ESCAPE_JSON but with the
   * escape for "/" removed. It's unnecessary, and it makes URIs look really
   * unplesasant - which is not good for our target application.
   */
  public static final CharSequenceTranslator ALT_ESCAPE_JSON =
    new AggregateTranslator(
      new CharSequenceTranslator[] {
        new LookupTranslator(new String[][] { {"\"", "\\\""},
                                              {"\\", "\\\\"}}),
        new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE()),
        JavaUnicodeEscaper.outsideOf(32, 127)
      });

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
    xiBuffer.append(ALT_ESCAPE_JSON.translate(xiValue));
    xiBuffer.append("\"");
  }

  @Override
  public boolean isSimple()
  {
    return true;
  }
}
