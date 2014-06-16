// Resolver.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import org.openmobilealliance.arc.sax2j.ProgressWriter;
import org.openmobilealliance.arc.sax2j.ProgressWriter.NullProgressWriter;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class Resolver implements LSResourceResolver
  {
    /**
     * Root directory for common schemas.
     *
     * TODO: Preconfigured ones shouldn't be in test data!
     */
    private static File sSchemaRoot = new File("testdata/schemas");

    /**
     * Where to find common schemas.
     *
     * TODO: Make this configurable.
     */
    private static Map<String,String> sCatalog = new HashMap() {
      {
        put(XMLConstants.XML_NS_URI, "xml.xsd");
        put("urn:oma:xml:rest:netapi:common:1", "OMA-SUP-XSD_rest_netapi_common-V1_0-20120417-C.xsd");
      }
    };

    private boolean mIsRetrievalEnabled;
    private ProgressWriter mProgress = new ProgressWriter.NullProgressWriter();

    /**
     * Constructor.
     */
    public Resolver()
    {
    }

    /**
     * @param xiIsRetrievalEnabled Allow the resolver to retrieve schemas
     * automatically?
     */
    public void setRetrievalEnabled(boolean xiIsRetrievalEnabled)
    {
      mIsRetrievalEnabled = xiIsRetrievalEnabled;
    }

    public void setProgressWriter(ProgressWriter xiProgress)
    {
      mProgress = xiProgress;
    }

    @Override
    public LSInput resolveResource(String xiType,
                                   String xiNamespaceURI,
                                   String xiPublicId,
                                   String xiSystemId,
                                   String xiBaseURI)
    {
      try
      {
        LSInput lret;

        String lFilename = sCatalog.get(xiNamespaceURI);
        if (lFilename != null)
        {
          mProgress.log("Reading " + xiNamespaceURI + " from local cache: " + lFilename);
          lret = new Input();
          lret.setByteStream(new FileInputStream(new File(sSchemaRoot, lFilename)));
        }
        else if (mIsRetrievalEnabled)
        {
          mProgress.log("Attempting to retrieve " + xiNamespaceURI + " from " + xiSystemId);
          lret = new Input();
          lret.setSystemId(xiSystemId);
        }
        else
        {
          String lType = XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(xiType) ?
                                                             "schema" : xiType;
          throw new RuntimeException("Unable to retrieve " + lType +
            " document for namespace " + xiNamespaceURI + " with public ID " +
              xiPublicId + " and system ID " + xiSystemId);
        }

        return lret;
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    }
  }