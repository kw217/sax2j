// XmlSchema.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
// TODO: Decide on correct copyright banner.
// TODO: Decide on correct package name.
package org.openmobilealliance.arc.sax2j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A parsed XML Schema, ready for use for validation and translation of valid
 * XML documents.
 */
public class XmlSchema
{
  /**
   * Use XML Schema 1.1? (vs 1.0)
   */
  private static final boolean sUseXsd11 = true;

  /**
   * The progress writer we use.
   */
  private static final ProgressWriter sProgress = new ConsoleProgressWriter();

  /**
   * The resolver we use.
   */
  private static final Resolver sResolver = new Resolver();
  static
  {
    sResolver.setRetrievalEnabled(true);
    sResolver.setProgressWriter(sProgress);
  }

  /**
   * The parsed XML schema.
   */
  private final Schema mSchema;

  /**
   * Construct (and parse) a new schema from file.
   *
   * @param xiFile the file
   * @throws SAXException
   */
  public XmlSchema(File xiFile) throws SAXException
  {
    String lSchemaLanguage = sUseXsd11 ? "http://www.w3.org/XML/XMLSchema/v1.1"
                                         : XMLConstants.W3C_XML_SCHEMA_NS_URI;
    SchemaFactory lFactory = SchemaFactory.newInstance(lSchemaLanguage);
    sProgress.log("Using XML Schema " + (sUseXsd11 ? "1.1" : "1.0"));
    lFactory.setResourceResolver(sResolver);
    Source lSchemaSource = new StreamSource(xiFile);
    mSchema = lFactory.newSchema(lSchemaSource);
    sProgress.log("Parsed schema " + xiFile);
  }

  /**
   * Parse the given document according to this schema.
   *
   * @param xiFile the XML file to parse
   * @return the parsed document
   */
  public XmlDocument parse(File xiFile) throws IOException, SAXException
  {
    Source lDocument = new SAXSource(new InputSource(new FileInputStream(xiFile)));
    Validator lValidator = mSchema.newValidator();
    lValidator.validate(lDocument);
    XmlDocument lret = new XmlDocument(this);
    lret.setProgressWriter(sProgress);
    return lret;
  }
}
