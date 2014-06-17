// RenderParams.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.json;

/**
 * Render parameters. Pure immutable data object (all fields are final).
 */
public class RenderParams
{
  /**
   * The string to add to the current indent when increasing a level.
   */
  private final String mIndentStep;

  /**
   * Use newlines?
   */
  final boolean mUseNewlines;

  /**
   * Spacing between [ or { and first element.
   */
  final String mFirstSpacing;

  /**
   * Spacing after colon.
   */
  final String mColonSpacing;

  /**
   * Spacing after comma.
   */
  final String mCommaSpacing;

  /**
   * Spacing between last element and } or ].
   */
  final String mLastSpacing;

  /**
   * The current indentation level.
   */
  private final String mCurrentIndent;

  /**
   * Have we already written to this line?
   */
  final boolean mDirtyLine;

  /**
   * @return a new transport renderer.
   */
  public static RenderParams createTransport()
  {
    return new RenderParams("", false, "", "", "", "");
  }

  /**
   * @return a new compact renderer.
   */
  public static RenderParams createCompact()
  {
    return new RenderParams("", false, "", " ", " ", "");
  }

  /**
   * @return a new pretty renderer.
   */
  public static RenderParams createPretty()
  {
    return new RenderParams("  ", true, " ", " ", "", " ");
  }

  /**
   * Indent the next line.
   *
   * @param xiDirtyLine Have we already written to the current line?
   * @return the new parameters
   */
  RenderParams indent(boolean xiDirtyLine)
  {
    return new RenderParams(this,
                            mCurrentIndent + mIndentStep,
                            xiDirtyLine);
  }

  /**
   * Actually render the current indent.
   *
   * @param xiBuffer
   */
  void doIndent(StringBuilder xiBuffer)
  {
    if (mUseNewlines)
    {
      xiBuffer.append("\n").append(mCurrentIndent);
    }
  }

  /**
   * Private constructor for fresh params
   * @param xiIndentStep
   * @param xiUseNewlines
   */
  private RenderParams(String xiIndentStep,
                       boolean xiUseNewlines,
                       String xiFirstSpacing,
                       String xiColonSpacing,
                       String xiCommaSpacing,
                       String xiLastSpacing)
  {
    mIndentStep = xiIndentStep;
    mUseNewlines = xiUseNewlines;
    mFirstSpacing = xiFirstSpacing;
    mColonSpacing = xiColonSpacing;
    mCommaSpacing = xiCommaSpacing;
    mLastSpacing = xiLastSpacing;
    mCurrentIndent = "";
    mDirtyLine = false;
  }

  /**
   * Private constructor for derivative params
   * @param xiIndentStep
   * @param xiUseNewlines
   */
  private RenderParams(RenderParams xiThat,
                       String xiCurrentIndent,
                       boolean xiDirtyLine)
  {
    mIndentStep = xiThat.mIndentStep;
    mUseNewlines = xiThat.mUseNewlines;
    mFirstSpacing = xiThat.mFirstSpacing;
    mColonSpacing = xiThat.mColonSpacing;
    mCommaSpacing = xiThat.mCommaSpacing;
    mLastSpacing = xiThat.mLastSpacing;
    mCurrentIndent = xiCurrentIndent;
    mDirtyLine = xiDirtyLine;
  }

  @Override
  public String toString()
  {
    return "<" + mCurrentIndent.length() + "/" + (mDirtyLine ? "*" : "-") + ">";
  }
}
