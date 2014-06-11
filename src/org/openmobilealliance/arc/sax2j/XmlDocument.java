// XmlDocument.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class XmlDocument
{
  /**
   * The progress writer we use.
   */
  private ProgressWriter mProgress = new ProgressWriter.NullProgressWriter();

  /**
   * The schema to parse with.
   */
  private final XmlSchema mSchema;

  /**
   * The file to parse.
   */
  private final File mDocFile;

  /**
   * The parsed DOM document.
   */
  private Document mDoc;

  /**
   * Construct an XML document. Use {@link #parse()} to parse it.
   *
   * @param xiSchema
   * @param xiFile
   */
  public XmlDocument(XmlSchema xiSchema, File xiFile)
  {
    mSchema = xiSchema;
    mDocFile = xiFile;
  }

  public void setProgressWriter(ProgressWriter xiProgress)
  {
    mProgress = xiProgress;
  }

  /**
   * Parse the given XML document using the given schema.
   *
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public void parse()
      throws ParserConfigurationException, SAXException, IOException
  {
    if (mDoc != null)
    {
      throw new RuntimeException("Already parsed");
    }

    // Get a parser.
    DocumentBuilderFactory lFactory = mSchema.getDocumentBuilderFactory();
    DocumentBuilder lBuilder = lFactory.newDocumentBuilder();
    ThrowingErrorHandler lErrors = new ThrowingErrorHandler();
    lErrors.setProgressWriter(mProgress);
    lBuilder.setErrorHandler(lErrors);

    // Parse.
    mDoc = lBuilder.parse(mDocFile);

    // Check we're all hunky-dory.
    if (!mDoc.getDocumentElement().isSupported("psvi", "1.0"))
    {
      throw new RuntimeException("PSVI not supported by document");
    }
  }

  // TODO tidy up this quick test implementation
  public void walk()
  {
    walk("", mDoc.getDocumentElement());
  }

  // TODO tidy up this quick test implementation
  void walk(String xiIndent, Element xiElement)
  {
    mProgress.log(xiIndent + "<" + xiElement.getTagName() + ">");
    String lIndent = xiIndent + "  ";

    NodeList lNodes = xiElement.getChildNodes();
    int lNumNodes = lNodes.getLength();

    for (int i = 0; i < lNumNodes; i++)
    {
      Node lNode = lNodes.item(i);

      switch (lNode.getNodeType())
      {
        case Node.ATTRIBUTE_NODE:
          break;

        case Node.ELEMENT_NODE:
        {
          walk(lIndent, (Element)lNode);
        }
        break;

        case Node.TEXT_NODE:
        {
          Text lText = (Text)lNode;
          mProgress.log(lIndent + lText.getNodeValue());
        }
          break;

        default:
        {
          throw new RuntimeException("Unsupported node type " + lNode.getNodeType() + " at node " + lNode);
        }
      }

      // TODO: Handle other types, especially attribute
      if (lNode.getNodeType() == Node.ELEMENT_NODE)
      {
      }
    }

    mProgress.log(xiIndent + "</" + xiElement.getTagName() + ">");
  }

  /**
   * @return this document, converted to JSON
   */
  public String toJson()
  {
    return "haha fooled you";
  }
}
