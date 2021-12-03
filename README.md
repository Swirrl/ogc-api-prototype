# ogc-api-prototype

Work in progress!

Prototype [OGC API](https://ogcapi.ogc.org/) service
using [Apache Jena](https://jena.apache.org/) + GeoSPARQL.

## Requirements
Clojure, and JDK11 for Jena.

## Run it

Get [jena-fuseki-geosparql-4.0.2.jar](https://repo1.maven.org/maven2/org/apache/jena/jena-fuseki-geosparql/4.2.0/)
and pick a `$DATAPATH` to store the data.
```
java -jar ./jena-fuseki-geosparql-4.2.0.jar -t $DATAPATH -t2 -i
```

Import some data.
```
tdb2.tdbloader --loc $DATAPATH FILE.nt
```

Run it with `clj -A:dev` and enter `(dev) (go)` to make it do things.

