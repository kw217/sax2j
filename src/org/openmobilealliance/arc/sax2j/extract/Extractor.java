// Extractor.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.extract;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openmobilealliance.arc.sax2j.ProgressWriter;

/**
 * Extracts XML and JSON documents from text files saved from Microsoft Word
 * in UTF-8 format.
 */
public class Extractor
{
  /**
   * Beginning of a JSON section (in appendix D). Assumes particular
   * formatting and numbering.
   */
  private static Pattern JSON_SECTION =
    Pattern.compile("^(D\\.\\d+) (.*) \\(section (6\\.[\\d\\.]+)\\)$");

  /**
   * Beginning of a request or response. Group 1 matches requests, group 2
   * matches responses, group 3 matches errors.
   */
  private static Pattern HTTP_BEGIN =
    Pattern.compile("^(?:([A-Z]{3,}) /.* HTTP/\\d\\.\\d|HTTP/\\d\\.\\d (\\d{3})|([A-Z1-9]\\d*\\.\\d+) ).*$");

  /**
   * Matches start of body. If group 1 matches, it's succeeded; otherwise we've
   * matched something we shouldn't see before the body.
   */
  private static Pattern START_BODY =
    Pattern.compile("^(?:()[<{]|[A-Z]{3,} /|HTTP/|[A-Z1-9]\\d*\\.\\d+ ).*$");

  /**
   * Matches end of body. If group 1 matches, it's succeeded; otherwise we've
   * matched something we shouldn't see in the body. Don't forget single-line
   * JSON documents!
   */
  private static Pattern END_BODY =
      Pattern.compile("^(</|[]}]*}|[^\\s{<]|\\{.*\\}).*$");

  private String mFilename;
  private String mPrefix;
  private String mSuffix;
  private BufferedReader mReader;
  private String mLine;
  private ProgressWriter mProgress = new ProgressWriter.NullProgressWriter();

  /**
   * Constructor
   * @param xiFilename Filename to parse. Should be .txt in UTF-8 format as
   * saved from Microsoft Word, in OMA ARC REST NetAPI format.
   * @param xiPrefix Prefix to use for JSON filenames.
   * @param xiSuffix Suffix to use for JSON filenames.
   */
  public Extractor(String xiFilename, String xiPrefix, String xiSuffix)
  {
    mFilename = xiFilename;
    mPrefix = xiPrefix;
    mSuffix = xiSuffix;
  }

  public void setProgressWriter(ProgressWriter xiProgress)
  {
    mProgress = xiProgress;
  }

  public void extract() throws IOException
  {
    InputStream lInput =
        new BufferedInputStream(new FileInputStream(new File(mFilename)));
    mReader =
        new BufferedReader(new InputStreamReader(lInput, "UTF-8"));

    findJsonSectionStart();
  }

  /**
   * Normal, acceptable end of file.
   * @throws IOException
   */
  private void endOfFile() throws IOException
  {
    mReader.close();
    mReader = null;
  }

  /**
   * Unexpected end of file
   * @param xiMessage while doing...
   * @throws IOException
   */
  private void badEndOfFile(String xiMessage) throws IOException
  {
    mReader.close();
    mReader = null;

    mProgress.log("*** UNEXPECTED END OF FILE while " + xiMessage);
  }

  private void findJsonSectionStart() throws IOException
  {
    mLine = mReader.readLine();

    if (mLine != null)
    {
      findJsonSectionStartHere();
      return;
    }

    endOfFile();
  }

  /**
   * Variant that assumes the first line has already been read.
   * @throws IOException
   */
  private void findJsonSectionStartHere() throws IOException
  {
    do {
      Matcher lMatcher = JSON_SECTION.matcher(mLine);

      if (lMatcher.matches())
      {
        mProgress.log("Parsing section JSON " + lMatcher.group(3) + ": " +
                                  lMatcher.group(1) + " " + lMatcher.group(2));
        findJsonBegin(lMatcher.group(3), false, false);
        return;
      }
    }
    while ((mLine = mReader.readLine()) != null);

    endOfFile();
  }

  private void findJsonBegin(String xiSection,
                             boolean xiSeenRequest,
                             boolean xiSeenResponse) throws IOException
  {
    mLine = mReader.readLine();

    if (mLine != null)
    {
      findJsonBeginHere(xiSection, xiSeenRequest, xiSeenResponse);
      return;
    }

    badEndOfFile("searching for beginning of JSON " + xiSection);
  }

  private void findJsonBeginHere(String xiSection,
                                 boolean xiSeenRequest,
                                 boolean xiSeenResponse) throws IOException
  {
    do
    {
      Matcher lMatcher = HTTP_BEGIN.matcher(mLine);

      if (lMatcher.matches())
      {
        if (lMatcher.group(3) != null)
        {
          if (!xiSeenRequest || !xiSeenResponse)
          {
            mProgress.log("  *** Missing JSON " + (xiSeenRequest ? "response" : "request") + " for " + xiSection);
          }

          findJsonSectionStartHere();
          return;
        }
        else
        {
          boolean lRequest = lMatcher.group(1) != null;
          boolean lResponse = lMatcher.group(2) != null;
          String lWhat = lRequest ? lMatcher.group(1) : lMatcher.group(2);

          if ((lRequest && xiSeenRequest) ||
              (lResponse && xiSeenResponse))
          {
            mProgress.log("  *** Already seen a " +
                    (lRequest ? "request" : "response") + " for " + xiSection);
            findJsonBegin(xiSection, xiSeenRequest, xiSeenResponse);
            return;
          }

          if ("GET".equals(lWhat) ||
                   "DELETE".equals(lWhat) ||
                   "204".equals(lWhat))
          {
            // Never has a body
            mProgress.log("  --- Skipping " + lWhat);
            findJsonBegin(xiSection,
                          lRequest || xiSeenRequest,
                          lResponse || xiSeenResponse);
            return;
          }
          else
          {
            String lSubSection = xiSection + "." + (lRequest ? "1" : "2");
            mProgress.log("  Found start of " + lSubSection + " " + lWhat);

            findJsonBodyStart(xiSection,
                              lSubSection,
                              lRequest || xiSeenRequest,
                              lResponse || xiSeenResponse);
            return;
          }
        }
      }
    } while ((mLine = mReader.readLine()) != null);

    badEndOfFile("searching for beginning of JSON " + xiSection);
  }

  private void findJsonBodyStart(String xiSection,
                                 String xiSubSection,
                                 boolean xiSeenRequest,
                                 boolean xiSeenResponse)
      throws IOException
  {
    while ((mLine = mReader.readLine()) != null)
    {
      Matcher lMatcher = START_BODY.matcher(mLine);

      if (lMatcher.matches())
      {
        if (lMatcher.group(1) != null)
        {
          findJsonBody(xiSection, xiSubSection, xiSeenRequest, xiSeenResponse);
          return;
        }
        else
        {
          mProgress.log("  *** Failed to find body start in JSON " + xiSubSection);
          findJsonBeginHere(xiSection, xiSeenRequest, xiSeenResponse);
          return;
        }
      }
    }

    badEndOfFile("searching for body start of JSON " + xiSubSection);
  }

  private void findJsonBody(String xiSection,
                            String xiSubSection,
                            boolean xiSeenRequest,
                            boolean xiSeenResponse)
                                throws IOException
  {
    StringBuilder lBuf = new StringBuilder();

    do
    {
      Matcher lMatcher = END_BODY.matcher(mLine);

      if (lMatcher.matches())
      {
        if (lMatcher.group(1) != null)
        {
          lBuf.append(mLine).append("\n");
          saveJson(xiSubSection, lBuf.toString());
          findJsonBegin(xiSection, xiSeenRequest, xiSeenResponse);
          return;
        }
        else
        {
          mProgress.log("  *** Failed to find body end of JSON " + xiSubSection);
          findJsonBeginHere(xiSection, xiSeenRequest, xiSeenResponse);
          return;
        }
      }
      else
      {
        lBuf.append(mLine).append("\n");
      }
    } while ((mLine = mReader.readLine()) != null);

    badEndOfFile("searching for body end of JSON " + xiSubSection);
  }

  private void saveJson(String xiSection, String xiJson) throws IOException
  {
    File lFile = new File(mPrefix + xiSection + mSuffix);
    FileUtils.writeStringToFile(lFile, xiJson, "UTF-8");
    mProgress.log("  Done JSON " + xiSection);
  }
}
