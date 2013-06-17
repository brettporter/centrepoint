Centrepoint is an example Maven application used in the book [Apache Maven 2: Effective Implementation](http://www.packtpub.com/apache-maven-2-effective-implementation/book).

Building
--------

Before building Centrepoint, you need the license resources built (as there
is no repository to distribute them from at present):

```
cd license-resources
mvn clean install
````

To build and use the Centrepoint application:

```
cd centrepoint
mvn clean install
```

After a successful build, the file `distribution/target/centrepoint-1.0-SNAPSHOT-bin.zip`
contains the full standalone application.

To run, unzip it and run the following command:

```
bin/centrepoint console
```

The application can then be viewed on http://localhost:8080/centrepoint/
