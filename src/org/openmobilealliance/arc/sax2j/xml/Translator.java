// Translator.java
// (C) COPYRIGHT METASWITCH NETWORKS 2014
package org.openmobilealliance.arc.sax2j.xml;

import java.util.regex.Pattern;

import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.openmobilealliance.arc.sax2j.json.JsonArray;
import org.openmobilealliance.arc.sax2j.json.JsonNull;
import org.openmobilealliance.arc.sax2j.json.JsonNumber;
import org.openmobilealliance.arc.sax2j.json.JsonObject;
import org.openmobilealliance.arc.sax2j.json.JsonString;
import org.openmobilealliance.arc.sax2j.json.JsonValue;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Translator from XML to JSON.
 */
public class Translator
{
  /**
   * Regexp for whitespace-only text.
   */
  private static final Pattern WHITESPACE_TEXT = Pattern.compile("[ \t\n]*");

  /**
   * Add the given element (as a name/value pair) to the given object.
   *
   * @param xiMode
   * @param xoObject
   * @param xiElement
   */
  public static void toJson(TranslationMode xiMode,
                            JsonObject xoObject,
                            Element xiElement)
  {
    String lKey = xiElement.getLocalName();
    JsonValue lValue = toJsonValue(xiMode, xiElement);
    put(xiMode, xoObject, xiElement, lKey, lValue);
  }

  /**
   * Convert the content of the given element to a JSON value.
   *
   * @param xiMode
   * @param xiElement
   * @return
   */
  public static JsonValue toJsonValue(TranslationMode xiMode, Element xiElement)
  {
    JsonValue lret;

    if (xiElement.getFirstChild() == null)
    {
      // TODO: Respect schema?
      lret = JsonNull.create();
    }
    else if (hasNoInterestingChildren(xiElement))
    {
      String lValue = xiElement.getTextContent();

      // TODO: switch on translation mode here?
      ElementPSVI lPsvi = (ElementPSVI)xiElement;
      XSElementDeclaration lDecl = lPsvi.getElementDeclaration();

      if (lDecl.getTypeDefinition().derivedFrom("http://www.w3.org/2001/XMLSchema", "decimal", (short)-1) ||
          lDecl.getTypeDefinition().derivedFrom("http://www.w3.org/2001/XMLSchema", "float", (short)-1) ||
          lDecl.getTypeDefinition().derivedFrom("http://www.w3.org/2001/XMLSchema", "double", (short)-1))
      {
        lret = JsonNumber.create(lValue);
      }
      else
      {
        lret = JsonString.create(lValue);
      }
    }
    else
    {
      JsonObject lObject = JsonObject.create();
      lret = lObject;

      for (Node lNode = xiElement.getFirstChild();
          lNode != null;
          lNode = lNode.getNextSibling())
      {
        switch (lNode.getNodeType())
        {
          case Node.ATTRIBUTE_NODE:
          {
            String lKey = lNode.getNodeName();
            String lStrValue = lNode.getNodeValue();
            JsonValue lValue = JsonString.create(lStrValue);
            // TODO: apply translation mode here
            put(xiMode, lObject, null, lKey, lValue);
          }
          break;

          case Node.ELEMENT_NODE:
          {
            toJson(xiMode, lObject, (Element)lNode);
          }
          break;

          case Node.TEXT_NODE:
          {
            String lStrValue = lNode.getNodeValue();

            if (WHITESPACE_TEXT.matcher(lStrValue).matches())
            {
              // Ignore whitespace
            }
            else
            {
              // TODO implement "$t" rules
              throw new RuntimeException("Mixed content unsupported");
            }
          }
          break;

          default:
          {
            throw new RuntimeException("Unsupported node type " + lNode.getNodeType() + " at node " + lNode);
          }
        }
      }
    }

    return lret;
  }

  /**
   * @param xiElement element to examine
   * @return true if the element has no attribute or element children
   */
  private static boolean hasNoInterestingChildren(Element xiElement)
  {
    boolean lret = true;

    for (Node lNode = xiElement.getFirstChild();
         lNode != null;
         lNode = lNode.getNextSibling())
    {
      short lType = lNode.getNodeType();
      if ((lType == Node.ATTRIBUTE_NODE) || (lType == Node.ELEMENT_NODE))
      {
        lret = false;
        break;
      }
    }

    return lret;
  }

  private static void put(TranslationMode xiMode,
                          JsonObject xiObject,
                          Element xiElement,
                          String xiKey,
                          JsonValue xiValue)
  {
    if (xiMode.useSchema())
    {
      if (!xiObject.containsKey(xiKey))
      {
        if (xiElement != null)
        {
          if (isElementMultiValued(xiElement))
          {
            // It's multivalued, so create an array for the values.
            xiObject.put(xiKey, JsonArray.create(), false);
          }
        }
      }
      xiObject.put(xiKey, xiValue, false);
    }
    else
    {
      xiObject.put(xiKey, xiValue, true);
    }
  }

  /**
   * Is it possible for this element to occur more than once within its
   * enclosing type?
   * @param xiElement
   * @return
   */
  private static boolean isElementMultiValued(Element xiElement)
  {
    boolean lmulti;

    // See http://xerces.apache.org/xerces2-j/faq-xs.html#faq-7
    ElementPSVI lPsvi = (ElementPSVI)xiElement;
    XSElementDeclaration lDecl = lPsvi.getElementDeclaration();

    XSComplexTypeDefinition lComplex = lDecl.getEnclosingCTDefinition();

    if (lComplex == null)
    {
      lmulti = false;
    }
    else
    {
      XSParticle lTopParticle = lComplex.getParticle();

      Boolean lResult = isElementMultiWithin(lDecl, false, lTopParticle);

      if (lResult == null)
      {
        throw new RuntimeException("Couldn't find declaration of " + lDecl.getName() + " within " + lComplex.getName());
      }

      lmulti = lResult;
    }

    return lmulti;
  }

  /**
   * Test if the given element may appear multipe times within the given
   * particle.
   *
   * @param xiDecl the declaration to search for
   * @param xiMulti does the particle itself already occur multiple times?
   * @param xiParticle the particle to search within
   * @return true if it may appear multiple times, false if it may appear at
   * most once, null if the element is not found within the particle.
   */
  private static Boolean isElementMultiWithin(XSElementDeclaration xiDecl,
                                              boolean xiMulti,
                                              XSParticle xiParticle)
  {
    if (xiParticle == null)
    {
      return null;
    }

    // Determine context multiplicity: either we're already multiple, or this
    // particle can appear multiple times.
    boolean lOuter =
        xiMulti ||
        xiParticle.getMaxOccursUnbounded() ||
        (xiParticle.getMaxOccurs() > 1);

    Boolean lMulti;
    XSTerm lTerm = xiParticle.getTerm();

    if (lTerm instanceof XSModelGroup)
    {
      XSModelGroup lGroup = (XSModelGroup)lTerm;
      XSObjectList lParticles = ((XSModelGroup)lTerm).getParticles();
      lMulti = null;

      for (Object lParticleObj : lParticles)
      {
        XSParticle lParticle = (XSParticle)lParticleObj;

        Boolean lSub = isElementMultiWithin(xiDecl, lOuter, lParticle);

        if (lSub != null)
        {
          if (lMulti == null)
          {
            // Normal case: we've found a single match
            lMulti = lSub;
          }
          else
          {
            // Multiple matches for the element - that's not very sane
            // (although we could just do lMulti = true).
            throw new RuntimeException("Multiple separate occurrences of the same element: " + xiDecl.getName());
          }
        }
      }
    }
    else if (lTerm instanceof XSElementDeclaration)
    {
      XSElementDeclaration lElementDecl = (XSElementDeclaration)lTerm;

      if (isEqualName(lElementDecl, xiDecl))
      {
        // We have a match! Take the multiplicity we've determined for this
        // context.
        lMulti = lOuter;
      }
      else
      {
        lMulti = null;
      }
    }
    else
    {
      throw new RuntimeException("Wildcard or other particle (complex type component) not supported: " + xiDecl.getName());
    }

    return lMulti;
  }

  /**
   * Do two XS objects have the same namespace and name?
   * @param xiA
   * @param xiB
   * @return
   */
  public static boolean isEqualName(XSObject xiA, XSObject xiB)
  {
    if (!xiA.getName().equals(xiB.getName()))
    {
      return false;
    }

    if (xiA.getNamespace() == null)
    {
      return (xiB.getNamespace() == null);
    }

    return xiA.getNamespace().equals(xiB.getNamespace());
  }

  /**
   * The translation mode to use for the XML -> JSON conversion.
   */
  public enum TranslationMode
  {
    /**
     * Instance-based JSON generation, per section 5.6.1 of
     * OMA-TS-REST_NetAPI_Common-V1_0-20140604-D.doc.
     */
    INSTANCE_BASED,

    /**
     * Structure-aware JSON generation, per section 5.6.2 of
     * OMA-TS-REST_NetAPI_Common-V1_0-20140604-D.doc.
     */
    STRUCTURE_AWARE;

    boolean useSchema()
    {
      return (this == STRUCTURE_AWARE);
    }
  }

}
