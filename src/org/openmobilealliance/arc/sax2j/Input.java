// Input.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

/**
 * Tedious data object representing an input source for the parser.
 */
public class Input implements LSInput
{
  private String mBaseURI;
  private String mPublicId;
  private String mSystemId;

  private InputStream mByteStream;
  private String mEncoding;
  private Reader mCharacterStream;
  private String mStringData;
  private boolean mCertifiedText;


  @Override
  public String getBaseURI()
  {
    return mBaseURI;
  }

  @Override
  public void setBaseURI(String xiBaseURI)
  {
    mBaseURI = xiBaseURI;
  }

  @Override
  public String getPublicId()
  {
    return mPublicId;
  }

  @Override
  public void setPublicId(String xiPublicId)
  {
    mPublicId = xiPublicId;
  }

  @Override
  public String getSystemId()
  {
    return mSystemId;
  }

  @Override
  public void setSystemId(String xiSystemId)
  {
    mSystemId = xiSystemId;
  }

  @Override
  public InputStream getByteStream()
  {
    return mByteStream;
  }

  @Override
  public void setByteStream(InputStream xiByteStream)
  {
    mByteStream = xiByteStream;
  }

  @Override
  public String getEncoding()
  {
    return mEncoding;
  }

  @Override
  public void setEncoding(String xiEncoding)
  {
    mEncoding = xiEncoding;
  }

  @Override
  public Reader getCharacterStream()
  {
    return mCharacterStream;
  }

  @Override
  public void setCharacterStream(Reader xiCharacterStream)
  {
    mCharacterStream = xiCharacterStream;
  }

  @Override
  public String getStringData()
  {
    return mStringData;
  }

  @Override
  public void setStringData(String xiStringData)
  {
    mStringData = xiStringData;
  }

  @Override
  public boolean getCertifiedText()
  {
    return mCertifiedText;
  }

  @Override
  public void setCertifiedText(boolean xiCertifiedText)
  {
    mCertifiedText = xiCertifiedText;
  }
}