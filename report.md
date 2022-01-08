# Introduction

This report describes research work carried out as part of Geonovum's Spatial Data APIs Testbed project, which ran from September to December 2021.  The project involved five research topics: this report describes Topic 5 "Simple/linked data encodings for spatial data APIs".   

The goals of the topic were:

* exploring the applicability of lighter formats (specifically, JSON) for the publication of geospatial
data.
* experimenting with the use of semantically enabled data - focusing the use of the JSON-LD spec in
combination with geodata in JSON, and the added value of the output.

Swirrl's approach to this task was influenced by the kind of work the company carries out for government organisations, addressing a range of data integration and data dissemination challenges.  Data analysis in government often involves comparing and connecting data from different sources, typically from different departments of a large organisation or from separate organisations.  Typical ‘enterprise’ approaches to data integration are often unsuitable when working across organisation boundaries in this way.

This has led Swirrl to apply the Linked Data approach, exploiting web standards and the globally-scoped identifiers that are typically used in RDF and Linked Data.

Often spatial data is an important element of these data integration and data analysis activities and there is a need to combine spatial and non-spatial data to get a complete picture.  In the past GIS systems and GIS specialists have operated largely independently of web-based data and that group of specialists.  While GIS systems can hold non-spatial properties of ‘spatial things’ and linked data can hold spatial data about the ‘resources’ of interest, each approach has its own strengths and weaknesses and so the best approach to data management and exploitation involved elements of both.  Finding ways to bring together these two communities of users was one of the main objectives of the OGC/W3C Spatial Data on the Web Working Group.

Swirrl’s work with the English Environment Agency (‘EA’) has a particularly strong spatial element to it and the ability to connect spatial and linked data is an area of keen interest to the EA. 

The approach taken in the new set of OGC APIs, and in particular the OGC API Features, seems to offer new opportunities for effective integration of spatial and investigating the potential of this is at the core of this piece of work.

Data integration becomes a lot easier when different data collections share common well-managed reference data. The ability to re-use or refer to definitive data also reduces duplication of effort and makes for more reliable data management. In general a resource-centric or feature-centric approach is more flexible than a ‘layer-centric’ approach typical of many GIS applications.  It is useful to be able to ‘slice’ data in many different ways.

The OGC API Features allows fine-grained addressing and retrieval of data about spatial things.  However not all data relevant to a feature or spatial thing can sensibly be managed as part of a single API, so the ability to link both ways between a Features API endpoint and other data sources, whether linked data or other spatial APIs, will be very useful.

Many of the existing applications that Swirrl operates for the EA are based around a collection of data held as RDF in a triple store.  The potential to offer the OGC API Features as another option for accessing data in this form is attractive. It would of course be possible to transform data from RDF into another format and store it in a different way, but the possibility of using the RDF data directly is attractive and one of the things we have investigated here.

How best to link from one data collection to another was another aspect of the research: whether using existing aspects of Features API or enhancing the responses with JSON-LD.




# Objectives



# Implementing the OGC API Features with RDF and Geosparql

Description of technology used and technical approach

Geosparql queries




# Test data

Four datasets were selected from data created by the English Environment Agency to test the approach and provide real use cases for linking data together.

The datasets are available to download as compressed n-triples files from an Amazon S3 bucket:

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

# Hosted endpoint

https://geonovum-staging.publishmydata.com/

# Testing and compliance

OGC test suite - which tests still fail?

Using it with Mapbox

Using it with QGIS



# Example application

https://geonovum-staging.publishmydata.com/demo/map.html

Code at https://github.com/Swirrl/ogc-api-prototype/tree/main/demo

