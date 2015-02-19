sax2j
=====

Schema Aware XML to JSON translator

*sax2j* is a tool for converting XML to JSON. It uses the XML Schema for the
XML document to guide tricky decisions, such as whether to render a single
element as a scalar or an array.

The rules followed by *sax2j* are documented in the
[Open Mobile Alliance](http://openmobilealliance.org/) standard
[Common definitions for RESTful Network APIs OMA-TS-REST_NetAPI_Common-V1_0-20150108-D](http://member.openmobilealliance.org/ftp/Public_documents/ARCH/Permanent_documents/OMA-TS-REST_NetAPI_Common-V1_0-20150108-D.zip).


Building
--------

To build *sax2j*, you need
ant (tested with v1.8.1)
and
Java (tested with 1.7.0_51).

    ant build

This will create a runnable JAR file, sax2j.jar.

You can test the code with

    ant test

and look at the test report at

    testout\index.html

.

Converting XML to JSON
----------------------

Because the required XML schema support is not built into Java, you must
pass special options to Java on startup.

To convert foo.xml to JSON using schema foo.xsd, type:

    java -Djava.endorsed.dirs=lib\commons-io-2.4;lib\commons-lang3-3.3.2;lib\xerces-2_11_0-xml-schema-1.1-beta \
         -jar sax2j.jar foo.xml foo.xsd > foo.json

(On Linux, use forward slashes and colons instead of backslashes and semicolons.)


Extracting XML and JSON from Word documents
-------------------------------------------

Many of the Open Mobile Alliance standards contain embedded XML documents
and JSON documents which need to be extracted as part of the standards
process. The XML documents need to be extracted so that they can be
converted, and the JSON documents need to be extracted so that they can
be checked (to confirm that they are identical to the result of conversion
from XML).

*sax2j* provides assistance in this process.

This process has been tested with recent OMA ARC standards documents only;
with other documents, your mileage may vary.

First, save the document as "Plain Text" in encoding "Unicode (UTF-8)",
without inserted linebreaks. E.g. foo.txt.

Then use *sax2j* to do the conversion as follows:

    java -Djava.endorsed.dirs=lib\commons-io-2.4;lib\commons-lang3-3.3.2;lib\xerces-2_11_0-xml-schema-1.1-beta \
         -jar sax2j.jar --extract foo.txt out\foo-fragment

This will scan foo.txt for XML and JSON documents, and write each one
to a file `foo-fragmentNNNN.xml` or `foo-fragmentNNN.xml.json`
in the `out` directory (which must already exist).

*NOTE* Currently, only JSON extraction is implemented.  OMA ARC has a tool
called *UNICA XML2JSON Converter* for extracting XML, which can be obtained
upon request.


Sample data
-----------

Some sample data is included in the sample/ directory,
from the OMA ARC NMS work.

    sax2j --extract sample/OMA-TS-REST_NetAPI_NMS-V1_0-20140715-C.txt sample/data

will extract the contents of the NMS TS into `sample/data*.xml.json`.

    sax2j sample/OMA-SUP-XSD_rest_netapi_nms-V1_0-20140619-D.xsd xmldata_6.11.3.1.2.xml > xmldata_6.11.3.1.2.xml.json

will convert the XML from section 6.11.3.1.2 into JSON, respecting the schema.

(`sax2j` in the above is an abbreviation for `java -Djava.... -jar sax2j.jar`
as used earlier).

Further sample data can be found in the `testdata` directories.

