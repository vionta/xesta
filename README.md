# Xesta {#xesta}

## An adjusted Java Object / Xml Document Binding {#an-adjusted-java-object--xml-document-binding}

Xesta is a java to xml binding tool. Xesta balances the approach between object and document side of the binding, trying to rely on tooling and practices aligned with each of the sides.

## Context. {#context}

Document information is usually more complex, updated more frequently, including both data and structure and may be more focused on small details. Documents, Xml documents, are a great way of to handle detail and persistent, isolated \(not directly backed by any system\) data. They are also the backbone of the pulishing and digital humanities industry, including legal texts, books, product instructions, etc.

Xml Documents are usually explored with XPath indications. While Java comes by default with XPath 1.x the Xml community rely on newer versions. Xesta annotations are defined with XPath 3.x instead of positional indications. Future XPath versions may be adopted.

On the other side, Java and object oriented tools are tend to work with more structured data, less complex but with more volume. This part require more consistency and a more structured/less flexible approach. Java tooling frequently rely on Xml schemas when dealing with Xml documents. It is also common for Java tools to look for similar structures on both sides and rely on Xml for plain object serialization.

Xesta was presented as Fento on XML Prague 2026 and afterwards renamed as Xesta.

# Getting Started with Xesta {#fasttrack}

**Dowload and configure the Xesta jar and dependencies**

Download the jar and dependencies or declare the dependency on the maven pom file.

Options:

- Declare the dependency on the pom file.

```

    <dependency>   
      <groupId>net.vionta.xml</groupId>   
      <artifactId>xesta</artifactId>   
      <version>1.0.1</version>   
    </dependency>   
  
```

- If the version is not available on maven central you can download the last version from http://github.com/vionta/xesta/releases and install it locally.

**Annotate your first class.**

Xesta defines the xml document locations using Java annotations \(Bind\), the Xml locations are defined with the "expression" attribute.

Classes should implement the Serializable interface.

```

 @Bind(expression = "//*:CstmrCdtTrfInitn" )   
 public class CheckNoNS implements Serializable {      

```

They should also have a no args constructor.

```

 public CheckNoNS() {}    

```

Then you should annotate the fields.

```

  @Bind(expression = "*:PmtInf/*:DbtrAcct/*:Id/*:Othr/*:Id")   
  String debtorAccount;   

```

A complete Pojo example from a class used to retrieve the value attribute from a console appender from a log4j file.

```

 import java.io.Serializable;      
 import net.vionta.xml.xesta.bind.annotation.Bind;   

 @Bind(expression = "/*:configuration/appender[@name='CONSOLE']")
 public class LogLevelSimple implements Serializable {      
   
 	@Bind(expression = "param[@name='Threshold']/@value")      
 	private String level;      
   
 	public LogLevelSimple() {      
 	  super();      
 	}      
       
 	public String getLevel() {      
 	  return level;      
 	}      
       
 	public void setLevel(String level) {      
 		this.level = level;      
 	}      
      
 }      

```

**Retrieve the document**

In order to retrieve the document you use a repository. The simplest repository type is a File repository where you pass the file location.

```

 FilePathRepository repo = new FilePathRepository("./cases/iso20022/GlobalPayment.xml");      
 GlobalPayment globalPayment = new GlobalPayment();      
 globalPayment = repo.load(globalPayment);      

```

You can find more examples from the XmlPrague 2026 conference at http://www.github.com/vionta/xesta-samples.

# Repository types {#repository-types}

Xesta access to documents are wrapped with the repository abstraction. In order to retrieve or update a document you need to choose from one of the repository types, provide the configuration parameters, usually the path or paths.

Once the repository is configured \(its path or paths are provided\) you request the repository to retrieve or update the document for you.

## Path Adjustment. { .section}

The repository paths \(in the supported cases\) can be adjusted based on the object properties. In order to configure the adjustment the property name \(separated with dots between objects\) must be defined inside the key \{ \} markers.

## File/Folder based repository. { .section}

The simplest possible repository type with a base path and a file path. The base path is not adjusted based on the object properties, while the file path can be adjusted with the \{\} keys.

```

FilePathRepository repo = new FilePathRepository(CommonTest.getBasePath()+"cases/iso20022/GlobalPayment.xml");   
GlobalPayment globalPayment = new GlobalPayment();   
globalPayment = repo.load(globalPayment);   

```

## Classpath repository. { .section}

A repository that retrieves files from the application classpath. This repository does not allow to create new files, also some files may be available for reading but not accesible for writing. The file path is defined with simple file path conventions separating folders with the slash "/" operator.

```

  ClassPathRepository repo = new ClassPathRepository().setPath("log4j.xml");    
  LogLevelSimple log = repo.load(new LogLevelSimple());   
  log.setLevel("DEBUG");   
  repo.persist(log);   

```

Mind that, depending on the running environment, the document path may or may not be accesible using the file system.

## REST based repository. { .section}

A repository that retrieves and stores the files from a REST service. The file path can also be adjusted via the object data using the \{\} keys on the path.

```
** Note: service authentication not yet developed. **
```

## Zip file based repository. { .section}

The zip file repository is similar to the file repository but acts on files that are placed inside zip files. Both the zipfile and the content file can be ajusted via the object properties.

Zip file names are not required to end in ".zip". Zip repository can be used for odt, ods, wars, etc.

```

String zipFilePattern = CommonTest.getBasePath()+"openDocument/text/fictional-report.odt";   
ZipArchiveFileRepository repo =   
  			   	new ZipArchiveFileRepository(   
				zipFilePattern,"content.xml");    
Document1 doc1 = new Document1();   
doc1 = (Document1) repo.load(doc1);   
  
```

## Abstract repository. { .section}

An abstract implementation that supports extensibility in other cases.

Still not available.

```

 ** Note: Functionality in development. **

```

# Bind Annotation Reference. {#bind-annotation-reference}

The Bind annotation reference has the fo

import net.vionta.xml.xesta.bind.annotation.Bind;

-   expression  The XPath mapping expression that points to the selected document nodes.

-   key  Indicates if the attribute identifies the node element. It is only considered when the collection strategy is equal to SerializingMode. BIND\_COLLECTION\_BY\_KEY.

-   classNames \[String \| Default : null \| Mandatory : false\] The class names of a multiple collection. An element list can containt more than one single element type. Xesta looks for the mappings in all of the classes to determine the collection elements. *This feature is under development*

-   serializingMode 

-   deserializingMode 

-   collectionBindStrategy 

-   collectionDeleteUnmatched 

-   namespaceAlias \[String \| Default : null \| Mandatory : false\] A list of the name space alias.

-   namespaceUris \[String \| Default : null \| Mandatory : false\] A list of the name space uris.

-   auto  If true the element name or expression is calculated from the java property name.

-   DeserializingMode

    -   FAIL\_ON\_NOT\_EXISTING : 1

    -   WARN\_ON\_NOT\_EXISTING : 2

    -   AVOID\_ON\_NOT\_EXISTING : 3

-   SerializingMode

    -   FAIL\_ON\_NOT\_EXISTING : 1

    -   CREATE\_ON\_NOT\_EXISTING : 2

    -   SKIP\_ON\_NOT\_EXISTING : 3

    -   BIND\_COLLECTION\_BY\_KEY : 1

    -   BIND\_COLLECTION\_BY\_POSITION : 2

    -   BIND\_COLLECTION\_APPEND\_LAST : 3

    -   BIND\_COLLECTION\_APPEND\_FIRST : 4

    -   BIND\_COLLECTION\_FULL\_RESET : 5

    -   COLLECTION\_DONT\_DELETE\_UNMATCHED : 1

    -   COLLECTION\_DELETE\_UNMATCHED : 2


# Consuming a remote RDF from the DBpedia with Xesta {#consuming-a-remote-rdf-from-the-dbpedia-with-xesta}

One of the several serialization formats used with rdf is Xml. You may be more prone to use turtle, owl or other formats when handling rdf information, but when it comes to integrate data Xml is one of the best choices.

There are lots of data resources on the web, like the Dbpedia, that can be consumed from applications without over-engineer your data model. Consuming the Dbpedia with Xesta is almost trivial for a java developer.

## Introducing the resource sample {#introducing-the-resource-sample}

We are going to query an airport information, taking the Vigo Airport as a sample. The url request would look like the following.

> curl 'https://dbpedia.org/data/Vigo–Peinador\_Airport.rdf'

The returned resource should look like:

>  ?xml version="1.0" encoding="utf-8" ?\> rdf:RDFxmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns\#"xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema\#"xmlns:owl="http://www.w3.org/2002/07/owl\#"xmlns:foaf="http://xmlns.com/foaf/0.1/"xmlns:dct="http://purl.org/dc/terms/"xmlns:dbo="http://dbpedia.org/ontology/"xmlns:dbp="http://dbpedia.org/property/"xmlns:geo="http://www.w3.org/2003/01/geo/wgs84\_pos\#"xmlns:prov="http://www.w3.org/ns/prov\#"xmlns:georss="http://www.georss.org/georss/"xmlns:gold="http://purl.org/linguistics/gold/" \> rdf:Description rdf:about="http://dbpedia.org/resource/List\_of\_airports\_by\_ICAO\_code:\_L"\> dbo:wikiPageWikiLink rdf:resource="http://dbpedia.org/resource/Vigo–Peinador\_Airport" /\> /rdf:Description\> rdf:Description rdf:about="http://dbpedia.org/resource/VG-20"\> dbo:wikiPageWikiLink rdf:resource="http://dbpedia.org/resource/Vigo–Peinador\_Airport" /\> dbo:routeStart rdf:resource="http://dbpedia.org/resource/Vigo–Peinador\_Airport" /\> dbp:terminusA rdf:resource="http://dbpedia.org/resource/Vigo–Peinador\_Airport" /\> /rdf:Description\> rdf:Description rdf:about="http://dbpedia.org/resource/List\_of\_airports\_by\_IATA\_airport\_code:\_V"\> dbo:wikiPageWikiLink rdf:resource="http://dbpedia.org/resource/Vigo–Peinador\_Airport" /\> /rdf:Description\> rdf:Description rdf:about="http://dbpedia.org/resource/Tenerife\_North–Ciudad\_de\_La\_Laguna\_Airport"\> dbo:wikiPageWikiLink rdf:resource="http://dbpedia.org/resource/Vigo–Peinador\_Airport" /\> /rdf:Description\> rdf:Description rdf:about="http://dbpedia.org/resource/Valencia\_Airport"\> dbo:wikiPageWikiLink rdf:resource="http://dbpedia.org/resource/Vigo–Peinador\_Airport" /\> /rdf:Description\> rdf:Description rdf:about="http://dbpedia.org/resource/List\_of\_Air\_Europa\_destinations"\> dbo:wikiPageWikiLink rdf:resource="http://dbpedia.org/resource/Vigo–Peinador\_Airport" /\> /rdf:Description\> rdf:Description rdf:about="http://dbpedia.org/resource/Juan\_Carlos\_I"\> dbo:wikiPageWikiLink rdf:resource="http://dbpedia.org/resource/Vigo–Peinador\_Airport" /\> /rdf:Description\>...

## Annotating the Pojo with mappings. {#annotating-the-pojo-with-mappings}

In order to define from where xml elements our information is taken from, we will create and map a Java POJO. Make sure you have defined the Xesta dependency or added the xesta jar to your path.

> <dependency\><groupId\>net.vionta.xml</groupId\><artifactId\>xesta</artifactId\><version\>1.0.1</version\></dependency\>

We define the property to xml mappings using the Bind annotation. For each class or mapped property, we will add the Bind annotation and add the XPath expression to describe the mapping. The property for the XPath expression is unsurprisingly called "expression".

The significative parts of the java code should look like the code shown below. Regular Setters and Getters should be added for each mapped property.

> @Bind\(expression="/\*:RDF"\)public class Airport implements Serializable \{

> public Airport\(\) \{\}

> private String key;

> @Bind\(expression=".//dbp:name/text\(\)", namespaceAlias= \{"dbp"\},namespaceUris= \{"http://dbpedia.org/property/"\}\)private String name;

> @Bind\(expression=".//\*:label/text\(\)"\)private String localName;

```
>   @Bind(expression=".//dbo:runwayLength[1]",    
>    namespaceAlias = {"dbo"},     
>    namespaceUris = {"http://dbpedia.org/ontology/"})    
```

> private String runwayLenght;

> @Bind\(expression=".//\*:stat1Data"\)private Integer passengers;

> @Bind\(expression=".//\*:stat2Data"\)private String passengersEvolution;

> @Bind\(expression=".//\*:stat3Data"\)private String movements;

> @Bind\(expression=".//\*:stat4Data"\)private Integer cargo;

> @Bind\(expression=".//georss:point",namespaceAlias = \{"georss"\},namespaceUris = \{"http://www.georss.org/georss/"\}\)private String position;

> @Bind\(expression=".//dbp:iata",namespaceAlias = \{"dbp"\},namespaceUris = \{"http://dbpedia.org/property/"\}\)private String iataCode;

> @Bind\(expression=".//dbo:icaoLocationIdentifier",namespaceAlias = \{"dbo"\},namespaceUris = \{"http://dbpedia.org/ontology/"\}\)private String icaoCode;

```
>   //..    
```

You can generate the required setters and getters using your IDE or check the full code sample at the samples repository.

## Querying the resource {#querying-the-resource}

The code needed for quering the resource is also really simple. Just create an HttpRepository object instance. We are also going to use the "key" property, from the mapping bean, to allow to switch between airports. You may notice the key placeholder in the path param.

> HttpRepository repo = new HttpRepository\(\);repo.setDocumentPath\("https://dbpedia.org/data/.rdf"\);

We create an Airport object and set the key to the desired airport.

> Airport testAirport = new Airport\(\);testAirport.setKey\("Vigo–Peinador\_Airport"\);

We can now load the airport information from the wikipedia.

> testAirport = repo.load\(testAirport\);

You should be able to check the information.

> 13:43:24,970 DEBUG  Airport 

At this point, you can play with the mappings, try to retrieve different values or query different resources.

## Querying different rdf resources with arq. {#querying-different-rdf-resources-with-arq}

Rdf resources are usually queried with SPARQLE queries. A simple way to use them is to use the jena arq program. An arq query can be executed to extract a tabular version of the data extracted from an SPARQLE query on a rdf resource. The result can be exported to an xml file.

> $ apache-jena-4.9.0/bin/arq --query queries/Practices.sparqle --data ./../notas-metodologias-definiciones-turtle.ttl --out XML \> results/Practice-Types.xml

The result xml output can be stored as an xml file and map a Xesta bean to use the information. In this case we would be using a File repository. You may also directly use Apache Jena.

## References {#references}

-   Dbpedia https://www.dbpedia.org

-   Apache Jena https://jena.apache.org

-   Apache Maven https://maven.apache.org

-   Xesta dependency https://mvnrepository.com/artifact/net.vionta.xml/xesta

-   Xesta https://vionta.net/vionta/community/xesta.html


# Consuming a remote Xml file with Xesta {#consuming-a-remote-xml-file-with-xesta}

There are lots of data resources available as Xml on the web. Xml is a gread data serialization format when it comes to data integration. We are going to show how web resources can be easily queried and used from Java using Xesta.

We are going to use the spanish weather service and the city of Vigo as sample. The weather predictions from Aemet, the spanish weather service, is available as Xml on the web.

## Introducing the resource sample {#introducing-the-resource-sample}

We are going to query the Vigo weather prediction. The city id could be parametrized using one of the pojo properties. The end url should look like the following.

> curl 'https://www.aemet.es/xml/municipios/localidad\_36057.xml'

The curl request answer should look like the following:

>  root xmlns:xsd="https://www.w3.org/2001/XMLSchema" xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance" id="36057" version="1.0" xsi:noNamespaceSchemaLocation="https://www.aemet.es/xsd/localidades.xsd"\>  !-- https://www.aemet.es/xml/municipios/localidad\_36057.xml --\>  origen\>  productor\>Agencia Estatal de Meteorología - AEMET. Gobierno de España /productor\>  web\>https://www.aemet.es&nbsp;/web\>  enlace\>https://www.aemet.es/es/eltiempo/prediccion/municipios/vigo-id36057&nbsp;/enlace\>  language\>es /language\>  copyright\>© AEMET. Autorizado el uso de la información y su reproducción citando a AEMET como autora de la misma. /copyright\>  nota\_legal\>https://www.aemet.es/es/nota\_legal&nbsp;/nota\_legal\>  /origen\>  elaborado\>2026-10-14T08:55:08 /elaborado\>  nombre\>Vigo /nombre\>  provincia\>Pontevedra /provincia\>  prediccion\>  dia fecha="2026-10-14"\>  prob\_precipitacion periodo="00-24"/\>  prob\_precipitacion periodo="00-12"/\>  prob\_precipitacion periodo="12-24"\>0 /prob\_precipitacion\>  prob\_precipitacion periodo="00-06"/\>  prob\_precipitacion periodo="06-12"\>0 /prob\_precipitacion\>  prob\_precipitacion periodo="12-18"\>0 /prob\_precipitacion\>  prob\_precipitacion periodo="18-24"\>0 /prob\_precipitacion\>  cota\_nieve\_prov periodo="00-24"/\>  cota\_nieve\_prov periodo="00-12"/\>  cota\_nieve\_prov periodo="12-24"/\>  cota\_nieve\_prov periodo="00-06"/\>

> ...

## Annotating the Pojo with mappings {#annotating-the-pojo-with-mappings}

We will create and configure two Java POJOs, one for the location prediction and one for each of the available days. Make sure you have defined the Xesta dependency on the project pom or added the xesta jar to your path.

> <dependency\><groupId\>net.vionta.xml</groupId\><artifactId\>xesta</artifactId\><version\>1.0.1</version\></dependency\>

We have to add the Bind annotation for the class and each property. In each annotation we should add the XPath expression to the desired Xml node. The property for the XPath expression is unsurprisingly called "expression". We have added the source xml data sample on the property comments for reference.

The significative parts of the java code for the location should look like the code shown below. Regular Setters and Getters should be added for each mapped property.

> @Bind\(expression = "/root"\) public class WeatherLocation implements Serializable \{

> public WeatherLocation\(\) \{\}

> @Bind\(expression = "nombre"\) String locationName ;

> @Bind\(expression = "@id"\) String locationCode ;

> @Bind\(expression="prediccion/dia"\) private List<DayPrediction\> nextDays = new ArrayList<DayPrediction\>\(\);

> public String getLocationName\(\) \{ return locationName; \}

> public void setLocationName\(String locationName\)

```
>   //..
```

After the fields are added and configured you can generate the settes and getters with your IDE. The DayPrediction java code may look like the following sample:

> @Bind public class DayPrediction implements Serializable \{

> public DayPrediction\(\) \{\}

> @Bind\(expression="@fecha"\) private String date ;

> @Bind\(expression="cota\_nieve\_provtext\(\)!=0"\) private Integer snow;

> @Bind\(expression="prob\_precipitaciontext\(\)!=0"\) private Integer rain;

> @Bind\(expression="estado\_cielo"\) private String clouds;

> @Bind\(expression="max\(viento/velocidad/text\(\)\)"\) private Integer wind;

> ...

You may notice that we don't need to provide an XPath expression for the DayPrediction object inside the Class Bind annotation, as it has been provided on the collection mapping.

## Querying the resource {#querying-the-resource}

The code needed for quering the resource is also quite simple. Just create an HttpRepository object. We are going to use the key property of the bean to allow to switch between airports. You will notice the key placeholder in the path param.

We can create an http repository object to query the resource with a code like the following.

> HttpRepository repo = new HttpRepository\(\); repo.setDocumentPath\("https://www.aemet.es/xml/municipios/localidad\_.xml"\);

Then we can create a parametrizable weather location object with the following code:

> WeatherLocation weatherLocation = new WeatherLocation\(\); weatherLocation.setLocationCode\("36057"\);

To retrieve the weather information we just need to load the object from the repository.

> weatherLocation = repo.load\(weatherLocation\); log.debug\("Gotted weather :"\); log.debug\(weatherLocation.toString\(\)\);

The end result should look similar to the following one:

> 15:20:21,862 DEBUG  WeatherLocation \[locationName=Vigo, locationCode=36057, nextDays=\[DayPrediction , DayPrediction , DayPrediction , DayPrediction , DayPrediction , DayPrediction , DayPrediction \]\]

At this point, you can play with the mappings, try to retrieve different values or query different resources.

The full code can be checked at the xesta samples repository on Github.

## References {#references}

-   Dbpedia https://www.aemet.es

-   Apache Maven: https://maven.apache.org

-   Xesta dependency: https://mvnrepository.com/artifact/net.vionta.xml/xesta

-   Xesta: https://vionta.net/vionta/community/xesta.html

-   Xesta samples at Github: https://github.com/vionta/xesta-samples


# Status {#status}

Xesta has been developed as an experiment and a proposal for XML Prague 2026. First scheduled presentation will take place at the Prague Economics University on June 6. At this stage, Xesta could be considered as an alpha version. Anyhow, remarkable number of tests has been performed an executed.

# Changelog {#changelog}

-   2026-09-03 Released documentation version 1.1. Documentation and release updated.

-   2026-06-08 Fento is renamed as Xesta. Due to a possible coincidence with another component the Fento Xml Binding component has been renamed as Xesta.


# Acknowledgements. {#acknowledgements}

-   Saxonica The XPath heavy lifting is performed by the mighty tool. www.saxonica.com


# References. {#references}

-   XML Prague presentation The tool, named Fento at the time, was presented at Prague Xml in 2026. https://www.xmlprague.cz/day3-2026/


