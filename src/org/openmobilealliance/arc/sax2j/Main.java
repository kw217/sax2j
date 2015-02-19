// Main.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import java.io.File;

import org.openmobilealliance.arc.sax2j.json.JsonValue;
import org.openmobilealliance.arc.sax2j.json.RenderParams;
import org.openmobilealliance.arc.sax2j.xml.Translator.TranslationMode;
import org.openmobilealliance.arc.sax2j.xml.XmlDocument;
import org.openmobilealliance.arc.sax2j.xml.XmlSchema;

public class Main
{
  private ProgressWriter mProgress = new ConsoleProgressWriter();

  private String[] mArgs;

  private XmlSchema mSchema;

  public static void main(String[] args) throws Exception
  {
    new Main(args).run();
  }

  public Main(String[] args)
  {
    mArgs = args;
  }

  public void run() throws Exception
  {
    String lSchemaFile = mArgs[0];
    mSchema = new XmlSchema(new File(lSchemaFile));
    mSchema.setProgressWriter(mProgress);
    mSchema.parse();

    for (int i = 1; i < mArgs.length; i++)
    {
      convert(mArgs[i]);
    }
  }

  public void convert(String xiFilename) throws Exception
  {
    XmlDocument mDoc = new XmlDocument(mSchema, new File(xiFilename));
    mDoc.setProgressWriter(mProgress);
    mDoc.parse();

    JsonValue lJson = mDoc.toJson(TranslationMode.STRUCTURE_AWARE);

    StringBuilder lBuffer = new StringBuilder();
    RenderParams lParams = RenderParams.createPretty();
    lJson.render(lBuffer, lParams);
    lBuffer.append("\n");

    System.out.print(lBuffer.toString());
  }
}
