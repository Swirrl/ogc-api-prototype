# Introduction

This report describes research work carried out as part of Geonovum's Spatial Data APIs Testbed project, which ran from September to December 2021.  The project involved five research topics: this report describes Topic 5 "Simple/linked data encodings for spatial data APIs".   

The goals of the topic were:

* exploring the applicability of lighter formats (specifically, JSON) for the publication of geospatial
data.
* experimenting with the use of semantically enabled data - focusing the use of the JSON-LD spec in
combination with geodata in JSON, and the added value of the output.

Swirrl's approach to this task was influenced by the kind of work the company carries out for government organisations, addressing a range of data integration and data dissemination challenges.  Data analysis in government often involves comparing and connecting data from different sources, typically from different departments of a large organisation or from separate organisations.  Typical ‘enterprise’ approaches to data integration are often unsuitable when working across organisation boundaries in this way.

This has led Swirrl to apply the Linked Data approach, exploiting web standards and the globally-scoped identifiers that are typically used in RDF and Linked Data.  For background to what Linked Data involves, see Tim Berners-Lee's [original note](https://www.w3.org/DesignIssues/LinkedData.html) and the [W3C summary page](https://www.w3.org/standards/semanticweb/data). 

Often spatial data is an important element of these data integration and data analysis activities and there is a need to combine spatial and non-spatial data to get a complete picture.  In the past GIS systems and GIS specialists have operated largely independently of web-based data and that group of specialists.  While GIS systems can hold non-spatial properties of ‘spatial things’ and linked data can hold spatial data about the ‘resources’ of interest, each approach has its own strengths and weaknesses and so the best approach to data management and exploitation involved elements of both.  Finding ways to bring together these two communities of users was one of the main objectives of the OGC/W3C Spatial Data on the Web Working Group.

Swirrl’s work with the English Environment Agency (‘EA’) has a particularly strong spatial element to it and the ability to connect spatial and linked data is an area of keen interest to the EA. 

The approach taken in the new set of OGC APIs, and in particular the OGC API Features, seems to offer new opportunities for effective integration of spatial and investigating the potential of this is at the core of this piece of work.

Data integration becomes a lot easier when different data collections share common well-managed reference data. The ability to re-use or refer to definitive data also reduces duplication of effort and makes for more reliable data management. In general a resource-centric or feature-centric approach is more flexible than a ‘layer-centric’ approach typical of many GIS applications.  It is useful to be able to ‘slice’ data in many different ways.

The OGC API Features allows fine-grained addressing and retrieval of data about spatial things.  However not all data relevant to a feature or spatial thing can sensibly be managed as part of a single API, so the ability to link both ways between a Features API endpoint and other data sources, whether linked data or other spatial APIs, will be very useful.

Many of the existing applications that Swirrl operates for the EA are based around a collection of data held as RDF in a triple store.  The potential to offer the OGC API Features as another option for accessing data in this form is attractive. It would of course be possible to transform data from RDF into another format and store it in a different way, but the possibility of using the RDF data directly is attractive and one of the things we have investigated here.

How best to link from one data collection to another was another aspect of the research: whether using existing aspects of Features API or enhancing the responses with JSON-LD.


# Objectives

The main objective of the work was to investigate how the OGC API, perhaps with extensions, could contribute to improving the interoperability of spatial and linked data.

Typically those familiar with spatial data standards and tools have been a separate community to those familiar with Linked Data and RDF based tools. Many of the spatial data tools have been heavily map based, for obvious reasons, and work in terms of 'layers' rather than individual features.  The OGC API - Features is a significant step forward to aligning the two approaches.  While building on existing spatial data formats such as Geojson and GML, it takes a REST style approach consistent with mainstream design of web APIs and has a resource-centric approach that aligns with the key concepts of Linked Data.

It fits naturally with the idea of providing a machine readable description of a spatial thing ( a 'resource' in the language of web architecture).  This is inherent in the Linked Data approach to representing and disseminating data and the OGC API could be seen as one more alternative format to representing data about a resource.

At the heart of Linked Data is of course the importance of links between items.  Many of the properties of a thing are expressed as a relationship between that  and some other thing, where all things of interest are assigned URIs as identifiers.


Investigate how to add aspects of Linked Data to OGC API Features
Test how JSON-LD can help enable this
Demonstrate how the Features API can be an additional access option for linked data.

https://datatracker.ietf.org/doc/html/rfc5988

# Implementing the OGC API Features with RDF and Geosparql

Description of technology used and technical approach

Geosparql queries




# Test data

Four datasets were selected from data created by the English Environment Agency to test the approach and provide real use cases for linking data together.

The datasets are available to download as compressed n-triples files from an Amazon S3 bucket: s3://swirrl-ogc-api-test-data/

## Ecology monitoring sites

This dataset contains monitoring sites where samples are taken to assess the population of invertebrates, diatoms and macrophytes. (These are managed in an Environment Agency system called 'Biosys'). The monitoring sites and associated data can be explored via a [web application](https://environment.data.gov.uk/ecology/explorer/).

Each monitoring site has a point geometry.

[Download](https://swirrl-ogc-api-test-data.s3.eu-west-2.amazonaws.com/biosys-sites.nt.gz)


## Embankments

[Download](https://swirrl-ogc-api-test-data.s3.eu-west-2.amazonaws.com/embankments.nt.gz)

## Water bodies

[Download](https://swirrl-ogc-api-test-data.s3.eu-west-2.amazonaws.com/waterbodies.nt.gz)

## Local authorities

[Download](https://swirrl-ogc-api-test-data.s3.eu-west-2.amazonaws.com/local-authorities.nt.gz)


# Linked Data and the OGC API Features

Making links - examples of why it's useful.  Use cases based on the selected test data

Different views of data for different user groups and applications

Options for technical approaches to those links

Relationship between API URLs and the identifiers for features

Role of JSON-LD

Limitations of the IANA registry of 'rel' types - defining the meaning of a particular term.  RDF provides a mechanism for a very flexible language of property types and a standardised way of defining what they mean.  There is also an ecosystem of standardised data models and associated vocabularies of terms that can be re-used in commonly occurring situations.  

# Hosted endpoint

The API implementation and test data is hosted at: 

https://geonovum-staging.publishmydata.com/

(Note that a browser extension that formats JSON in a more readable way makes it easier to view and understand the API responses: for example this one for Chrome: https://chrome.google.com/webstore/detail/json-formatter/bcjindcccaagfpapjjmafapmmgkkhgoa)

# Testing and compliance

The API has been tested against the [OGC API - Features Conformance Test Suite](https://cite.ogc.org/teamengine/about/ogcapi-features-1.0/1.0/site/).  The most important tests pass but there are still some test failures with the current state of the implementation.

TODO: confirm which tests from the OGC test suite still fail.

The demo application (described below) uses Leaflet, building on the simple example client available at https://github.com/opengeospatial/ogcapi-features/blob/master/implementations/clients/leaflet.md. It shows that the API responses can be correctly interpreted by a library expecting geojson.

We have also successfully tested the API with QGIS, using the QGIS feature to import a layer from an OGC API Features endpoint - see https://docs.qgis.org/3.16/en/docs/user_manual/working_with_ogc/ogc_client_support.html#wfs-and-wfs-t-client .



# Example application

We have developed a simple web application that makes calls to the API and displays the results on a web map, using the Leaflet.js library.  The demo is hosted at:

https://geonovum-staging.publishmydata.com/demo/map.html

It uses the bbox method to retrieve the data contained in the current viewport of the map.  It is currently set to retrieve a maximum of 100 features from a collection within the bounding box, to avoid attempting to load too much data into the browser, so when the map is fully zoomed out, only a subset of features are drawn. (Trying to draw all features from some collections can lead to the browser running out of memory).

As well as demonstrating how the API can be called and the results displayed, the application provides a simple illustration of the approach to linking that we have been investigating.

Clicking on a feature on the map brings up a pop-up with a link to the OGC API item and in some cases a link to another related item, whether part of another collection in the API endpoint (for example the Biosys monitoring sites link to the waterbody that contains them) or to an external link (the waterbodies on the map link to an external web page about the catchment they form part of).



Code at https://github.com/Swirrl/ogc-api-prototype/tree/main/demo

# Further work

full compliance with spec and passing everything in test suite
more on JSON-LD
more general approach to configuration - mapping the RDF representation to the Features json representation


# Conclusions

useful

plays nicely with linked data as is.  Relatively easy to provide as another option for accessing collections of linked data that have a strong spatial element.

possible useful extensions but for practical purposes, prob good to keep it simple


