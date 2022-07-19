# msv-merge-project

The Multi-Schema-Validator has to be merged from various sources GitHub and Maven Repo Source Jars.
This project provides tools and documentation about this process.

## MSV Sub Projects

MSV consists of a number of sub-projects. Each sub-projects has its own directory, its own build script, etc.
***NOTE:*** Not all forks and releases embrace all the projects below.

| sub-project   | description                                                                                                                                |
|:--------------|:-------------------------------------------------------------------------------------------------------------------------------------------|
| xsdlib        | **XML Schema Datatype (XSD) Library**<br/>An implementation of W3C XML Schema Part 2.                                                      |
| testharness   | **Test harness**<br/>Used to parse composite test suite files (.ssuite).                                                                   |
| msv core      | **Multi-Schema XML Validator**<br/>A schema model and validator implementation. Dependent on XSDLib and testharness.                       |
| generator     | **XML Instance Generator** A tool that produces valid XML documents by reading a schema. Dependent on MSV.                                 |
| schmit        | **MSV Schmit (Schema-in-transformation XSLT add-on)**<br/>XSLT Extension For Schema Annotation.                                            |
| relames       | **Multi-Schema XML Validator Schematron add-on**<br/>An experimental implementation of RELAX NG + Schematron validation. Dependent on MSV. |
| rngconverter  | **RELAX NG Converter**<br/>reads a schema and produces an equivalent RELAX NG schema. Dependent on MSV.                                    |
| tahiti        | **Data-binding implementation**                                                                                                            |
| trexconverter | **TREX Converter**<br/>Reads a schema and produces an equivalent TREX pattern.                                                             |

## MSV Source Code Origins

The primary source code origin is at former Sun/Oracle at [http://java.net/downloads/msv/releases/](http://java.net/downloads/msv/releases/), which redirects to [https://www.oracle.com/splash/java.net/maintenance/index.html](https://www.oracle.com/splash/java.net/maintenance/index.html), where MSV can be found among [https://javaee.github.io/other-migrated-projects.html](https://javaee.github.io/other-migrated-projects.html), where it is stated at the top: "The following projects are archived. If you have an interest in any of these projects or technologies, please contact us through through our discussion list at glassfish at javaee dot groups dot io (you must be a member of javaee dot groups dot io to post messages)".

Therefore the original source code repository is no longer accessible, but there are the following source origins:

1. A **GitHub fork from Kohsuke Kawaguchi** the former Code Owner at Oracle - [https://github.com/kohsuke/msv](https://github.com/kohsuke/msv).
The release with all the sources and the most advanced changes.
2. A **Maven release from RedHat** including Source Code doing some bug fixes. Actually, it has been two Early Access (EA) and one General Availablity (GA) releases, but the sources do not differ among the EA & GA releases:

    A. Maven Repo [https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/](https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/) including:

        1. [msv-core/](https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/msv-core/2011.1-redhat-2/msv-core-2011.1-redhat-2-sources.jar)
        2. [msv-generator/](https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/msv-generator/2011.1-redhat-2/msv-generator-2011.1-redhat-2-sources.jar)
        3. [msv-rngconverter/](https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/msv-rngconverter/2011.1-redhat-2/msv-rngconverter-2011.1-redhat-2-sources.jar)
        4. [msv-testharness/](https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/msv-testharness/2011.1-redhat-2/msv-testharness-2011.1-redhat-2-sources.jar)

    B. Maven Repo [https://maven.repository.redhat.com/ga/net/java/dev/msv/msv-testharness/2011.1-redhat-2/](https://maven.repository.redhat.com/ga/net/java/dev/msv/msv-testharness/2011.1-redhat-2/) including only two projects:

        1. [msv-core/](https://maven.repository.redhat.com/ga/net/java/dev/msv/msv-core/2011.1-redhat-2/msv-core-2011.1-redhat-2-sources.jar)
        2. [msv-testharness/](https://maven.repository.redhat.com/ga/net/java/dev/msv/msv-testharness/2011.1-redhat-2/msv-testharness-2011.1-redhat-2-sources.jar)

3. **Maven releases from Oracle** doing bug fixes and adjusting the copyright header correctly to BSD license.

## MSV Maven Deployment Rights

Were received via from Kohsuke Kawaguchi the former Code Owner at Oracle, see [https://issues.sonatype.org/browse/OSSRH-52887](https://issues.sonatype.org/browse/OSSRH-52887).
