# ogc-api-prototype

Prototype [OGC API](https://ogcapi.ogc.org/) service
using [Apache Jena](https://jena.apache.org/) + GeoSPARQL.

## Requirements
[Clojure](https://clojure.org/guides/getting_started), and JDK11 for Jena.

## Run it

Get [jena-fuseki-geosparql-4.0.2.jar](https://repo1.maven.org/maven2/org/apache/jena/jena-fuseki-geosparql/4.2.0/)
and pick a `$DATAPATH` to store the data.

For the TDB database and associated tools, get the main [Apache Jena jar](https://repo1.maven.org/maven2/org/apache/jena/apache-jena/4.2.0/apache-jena-4.2.0.zip).


```
java -jar ./jena-fuseki-geosparql-4.2.0.jar -t $DATAPATH -t2 -i
```

Import some data.
```
tdb2.tdbloader --loc $DATAPATH FILE.nt
```

The collections available should be configured in `resources/app.edn`
and refer to SPARQL queries provided in the classpath.
This is a work in progress.

Run it with `clj -A:dev` and enter `(dev) (go)` to run the service.
It listens on port 3000 by default: http://localhost:3000/collections

## License

Copyright © 2021 Swirrl IT Ltd.

Distributed under the Eclipse Public License, version 2.0.

