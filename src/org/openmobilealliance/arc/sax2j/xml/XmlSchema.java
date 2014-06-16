// XmlSchema.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
// TODO: Decide on correct copyright banner.
// TODO: Decide on correct package name.
package org.openmobilealliance.arc.sax2j.xml;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.openmobilealliance.arc.sax2j.ProgressWriter;
import org.openmobilealliance.arc.sax2j.ProgressWriter.NullProgressWriter;
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
  private final boolean sUseXsd11 = true;

  /**
   * The progress writer we use.
   */
  private ProgressWriter mProgress = new ProgressWriter.NullProgressWriter();

  /**
   * The resolver we use.
   */
  private final Resolver mResolver = new Resolver();
  {
    mResolver.setRetrievalEnabled(true);
    mResolver.setProgressWriter(mProgress);
  }

  /**
   * The file to parse.
   */
  private final File mSchemaFile;

  /**
   * The parsed XML schema.
   */
  private Schema mSchema;

  /**
   * Construct a new schema from file. Use {@link #parse()} to parse it.
   *
   * @param xiFile the file
   */
  public XmlSchema(File xiFile)
  {
    mSchemaFile = xiFile;
  }

  public void setProgressWriter(ProgressWriter xiProgress)
  {
    mProgress = xiProgress;
  }

  /**
   * Parse the schema.
   *
   * @throws SAXException
   */
  public void parse() throws SAXException
  {
    if (mSchema != null)
    {
      throw new RuntimeException("Already parsed");
    }

    String lSchemaLanguage = sUseXsd11 ? "http://www.w3.org/XML/XMLSchema/v1.1"
                                         : XMLConstants.W3C_XML_SCHEMA_NS_URI;
    SchemaFactory lFactory = SchemaFactory.newInstance(lSchemaLanguage);
    mProgress.log("Using XML Schema " + (sUseXsd11 ? "1.1" : "1.0"));
    lFactory.setResourceResolver(mResolver);
    Source lSchemaSource = new StreamSource(mSchemaFile);
    mSchema = lFactory.newSchema(lSchemaSource);
    mProgress.log("Parsed schema " + mSchemaFile);
  }

  /**
   * @return a factory for parsers that use this schema.
   */
  DocumentBuilderFactory getDocumentBuilderFactory()
  {
    if (mSchema == null)
    {
      throw new RuntimeException("Not yet parsed");
    }

    // Thanks http://xerces.apache.org/xerces2-j/faq-dom.html#faq-8
    DocumentBuilderFactoryImpl lFactory =
        (DocumentBuilderFactoryImpl)DocumentBuilderFactory.newInstance(
                             DocumentBuilderFactoryImpl.class.getName(), null);
    lFactory.setNamespaceAware(true);
    lFactory.setValidating(true);
    lFactory.setAttribute("http://apache.org/xml/features/validation/schema",
                          Boolean.TRUE);
    lFactory.setAttribute("http://apache.org/xml/features/validation/schema-full-checking",
                          Boolean.TRUE);
    lFactory.setAttribute("http://apache.org/xml/properties/dom/document-class-name",
                          "org.apache.xerces.dom.PSVIDocumentImpl");

    lFactory.setSchema(mSchema);
    // Undocumented but necessary to force the
    // org.apache.xerces.impl.xs.XmlSchemaValidator to actually use our schema
    // - otherwise the above schema doesn't make it all the way down the
    // stack. Sigh.
    lFactory.setAttribute("http://apache.org/xml/properties/internal/grammar-pool",
                          mSchema);

    return lFactory;
  }
}
