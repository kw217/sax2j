sax2j
=====

Schema Aware XML to JSON translator

*sax2j* is a tool for converting XML to JSON. It uses the XML Schema for the
XML document to guide tricky decisions, such as whether to render a single
element as a scalar or an array.

The rules followed by *sax2j* are documented in the
[Open Mobile Alliance|http://openmobilealliance.org/] standard
[Common definitions for RESTful Network APIs|http://member.openmobilealliance.org/ftp/Public_documents/ARCH/Permanent_documents/OMA-TS-REST_NetAPI_Common-V1_0-20150108-D.zip].


Building
--------

To build *sax2j*, you need
ant (tested with v1.8.1)
and
Java (tested with 1.7.0_51).

    ant build

This will create a runnable JAR file, sax2j.jar.


Converting XML to JSON
----------------------

Because the required XML schema support is not built into Java, you must
pass special options to Java on startup.

To convert foo.xml to JSON using schema foo.xsd, type:

    java -Djava.endorsed.dirs=lib\\commons-lang3-3.3.2;lib\\xerces-2_11_0-xml-schema-1.1-beta -jar sax2j.jar foo.xml foo.xsd > foo.json

(On Linux, use forward slashes and colons instead of backslashes and semicolons.)


Extracting XML from Word documents
----------------------------------

Many of the Open Mobile Alliance standards contain embedded XML documents
which need to be converted to JSON as part of the standards process.
*sax2j* provides assistance in this process.

This process has been tested with recent OMA ARC standards documents only
- with other documents, your mileage may vary.

First, save the document as text - e.g. foo.txt.

Then use *sax2j* to do the conversion as follows:

    java -jar sax2j.jar --extract foo.txt out\\foo-fragment

This will scan foo.txt for XML documents, and write each one to
a file `foo-fragmentNNNN.xml` in the `out` directory (which must
already exist).


