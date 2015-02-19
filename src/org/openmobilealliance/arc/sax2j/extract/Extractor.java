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
 * 
 * XML extraction is not yet implemented.
 * 
 * This class is a simple state-machine parser. State changes are implemented
 * by method calls - take care, in almost all cases it is essential to
 * return immediately after such a call, to avoid confusion. 
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
   * documents!
   */
  private static Pattern END_BODY =
      Pattern.compile("^(</|[]}]*}|[^\\s{<]|\\{.*\\}).*$");

  private Pattern mSectionPattern;
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
   * @param xiPrefix Prefix to use for subsection filenames.
   * @param xiSuffix Suffix to use for subsection filenames.
   */
  public Extractor(String xiFilename, String xiPrefix, String xiSuffix)
  {
    mSectionPattern = JSON_SECTION;
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

    findSectionStart();
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

  private void findSectionStart() throws IOException
  {
    mLine = mReader.readLine();

    if (mLine != null)
    {
      findSectionStartHere();
      return;
    }

    endOfFile();
  }

  /**
   * Variant that assumes the first line has already been read.
   * @throws IOException
   */
  private void findSectionStartHere() throws IOException
  {
    do {
      Matcher lMatcher = mSectionPattern.matcher(mLine);

      if (lMatcher.matches())
      {
        mProgress.log("Parsing section " + lMatcher.group(3) + ": " +
                                  lMatcher.group(1) + " " + lMatcher.group(2));
        findHttpBegin(lMatcher.group(3), false, false);
        return;
      }
    }
    while ((mLine = mReader.readLine()) != null);

    endOfFile();
  }

  private void findHttpBegin(String xiSection,
                             boolean xiSeenRequest,
                             boolean xiSeenResponse) throws IOException
  {
    mLine = mReader.readLine();

    if (mLine != null)
    {
      findHttpBeginHere(xiSection, xiSeenRequest, xiSeenResponse);
      return;
    }

    badEndOfFile("searching for beginning of section " + xiSection);
  }

  private void findHttpBeginHere(String xiSection,
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
            mProgress.log("  *** Missing HTTP " + (xiSeenRequest ? "response" : "request") + " for " + xiSection);
          }

          findSectionStartHere();
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
            findHttpBegin(xiSection, xiSeenRequest, xiSeenResponse);
            return;
          }

          if ("GET".equals(lWhat) ||
                   "DELETE".equals(lWhat) ||
                   "204".equals(lWhat))
          {
            // Never has a body
            mProgress.log("  --- Skipping " + lWhat);
            findHttpBegin(xiSection,
                          lRequest || xiSeenRequest,
                          lResponse || xiSeenResponse);
            return;
          }
          else
          {
            String lSubSection = xiSection + "." + (lRequest ? "1" : "2");
            mProgress.log("  Found start of " + lSubSection + " " + lWhat);

            findBodyStart(xiSection,
                          lSubSection,
                          lRequest || xiSeenRequest,
                          lResponse || xiSeenResponse);
            return;
          }
        }
      }
    } while ((mLine = mReader.readLine()) != null);

    badEndOfFile("searching for beginning of section " + xiSection);
  }

  private void findBodyStart(String xiSection,
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
          findBody(xiSection, xiSubSection, xiSeenRequest, xiSeenResponse);
          return;
        }
        else
        {
          mProgress.log("  *** Failed to find body start in subsection " + xiSubSection);
          findHttpBeginHere(xiSection, xiSeenRequest, xiSeenResponse);
          return;
        }
      }
    }

    badEndOfFile("searching for body start of subsection " + xiSubSection);
  }

  private void findBody(String xiSection,
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
          saveContent(xiSubSection, lBuf.toString());
          findHttpBegin(xiSection, xiSeenRequest, xiSeenResponse);
          return;
        }
        else
        {
          mProgress.log("  *** Failed to find body end of subsection " + xiSubSection);
          findHttpBeginHere(xiSection, xiSeenRequest, xiSeenResponse);
          return;
        }
      }
      else
      {
        lBuf.append(mLine).append("\n");
      }
    } while ((mLine = mReader.readLine()) != null);

    badEndOfFile("searching for body end of subsection " + xiSubSection);
  }

  private void saveContent(String xiSubSection, String xiContent) throws IOException
  {
    File lFile = new File(mPrefix + xiSubSection + mSuffix);
    FileUtils.writeStringToFile(lFile, xiContent, "UTF-8");
    mProgress.log("  Done subsection " + xiSubSection);
  }
}
