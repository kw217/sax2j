// XmlDocument.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlDocument
{
  /**
   * The progress writer we use.
   */
  private ProgressWriter mProgress = new ProgressWriter.NullProgressWriter();

  /**
   * The schema we were parsed with.
   */
  private final XmlSchema mSchema;

  /**
   * The parsed DOM document.
   */
  private final Document mDoc;

  /**
   * Constructor. Accessible only from within package - constructed by
   * XmlSchema.
   * @param xiSchema the schema that parsed this.
   */
  XmlDocument(XmlSchema xiSchema)
  {
    mSchema = xiSchema;
    mDoc = null;
  }

  public XmlDocument(XmlSchema xiSchema, File xiFile) throws ParserConfigurationException, SAXException, IOException
  {
    // Thanks http://xerces.apache.org/xerces2-j/faq-dom.html#faq-8
    DocumentBuilderFactoryImpl lFactory = (DocumentBuilderFactoryImpl)DocumentBuilderFactory.newInstance(DocumentBuilderFactoryImpl.class.getName(), null);
    lFactory.setNamespaceAware(true);
    lFactory.setValidating(true);
    lFactory.setAttribute("http://apache.org/xml/features/validation/schema", Boolean.TRUE);
    lFactory.setAttribute("http://apache.org/xml/features/validation/schema-full-checking", Boolean.TRUE);
    lFactory.setAttribute("http://apache.org/xml/properties/dom/document-class-name", "org.apache.xerces.dom.PSVIDocumentImpl");
    lFactory.setSchema(xiSchema.getSchema());
    // Undocumented but necessary to force the
    // org.apache.xerces.impl.xs.XmlSchemaValidator to actually use our schema
    // - otherwise the above schema doesn't make it all the way down the
    // stack. Sigh.
    lFactory.setAttribute("http://apache.org/xml/properties/internal/grammar-pool", xiSchema.getSchema());
    DocumentBuilder lBuilder = lFactory.newDocumentBuilder();

    ThrowingErrorHandler lErrors = new ThrowingErrorHandler();
    lErrors.setProgressWriter(mProgress);
    lBuilder.setErrorHandler(lErrors);

    mDoc = lBuilder.parse(xiFile);

    if (!mDoc.getDocumentElement().isSupported("psvi", "1.0"))
    {
      throw new RuntimeException("PSVI not supported by document");
    }

    mSchema = null;
  }

  public void setProgressWriter(ProgressWriter xiProgress)
  {
    mProgress = xiProgress;
  }
}
