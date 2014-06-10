// XmlSchema.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * A parsed XML Schema, ready for use for validation and translation of valid
 * XML documents.
 */
public class XmlSchema
{
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
    SchemaFactory lFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Source lSchemaSource = new StreamSource(xiFile);
    mSchema = lFactory.newSchema(lSchemaSource);
  }
}
