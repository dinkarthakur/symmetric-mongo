SymmetricDS mongoDB Database Writer
===============

This project is an implementation of a SymmetricDS [IDataWriter] (http://www.symmetricds.org/doc/3.5/javadoc/org/jumpmind/symmetric/io/data/IDataWriter.html) that maps and writes relational data to a [mongoDB] (http://www.mongodb.org/) NoSQL database.  This project can also be used as a reference for how to write extension point for other non sql based data stores.
 
[SymmetricDS] (http://symmetricds.org) is a database change data capture and synchronization application that specializes in its flexibily and its scalability.  One flexible aspect of SymmetricDS is its extendability through [extension points] (http://www.symmetricds.org/doc/3.5/html/advanced-topics.html#extensions).  This project implements the [IDataLoaderFactory] (http://www.symmetricds.org/doc/3.5/javadoc/org/jumpmind/symmetric/load/IDataLoaderFactory.html) extension point in order to allow a mongoDB data writer to wired into SymmetricDS.

### How to Configure

The mongoDB database writer is very simple to install and use.  

The database write is a jar file that is deployed to a standalone SymmetricDS installation.

The open source version of SymmetricDS can be downloaded from [SourceForge] (http://sourceforge.net/projects/symmetricds/).  A good "getting started" resource is the tutorial in the [Users Guide] (http://www.symmetricds.org/doc/3.5/html/tutorial.html).

The professional version can be downloaded from [jumpmind.com] (http://jumpmind.com).  A good "getting started" resource for the professional version is the [Quick Start Guide] (http://www.jumpmind.com/downloads/symmetricds/doc/).

A good overview of SymmetricDS can be found [here] (http://www.symmetricds.org/doc/3.5/html/introduction.html#definition).

The configuration for a SymmetricDS node is stored in a relational database.  Even though the mongoDB writer will be writing to a NoSQL data sink, the configuration for the node still requires a relational database.  The configuration database can be any of the supported [databases] (http://www.symmetricds.org/doc/3.5/html/databases.html).

To make the database writer available to SymmetricDS, copy the symmetric-mongo-XXX.jar and the [mongo-java-driver-XXX.jar] (http://central.maven.org/maven2/org/mongodb/mongo-java-driver/) file into SymmetricDS's web/WEB-INF/lib directory.

The mongoDB writer can be enabled by setting the DATA_LOADER_TYPE on the [channel] (http://www.symmetricds.org/doc/3.5/html/data-model.html#table_channel) that will be used to sync data.  The default data loader type for the mongoDB writer is _mongodb_.

Tables that should be synchronized to mongoDB should be configured to use this channel.

The last configuration step is to supply the writer with settings on how to communicate with the mongoDB database.  These setting can be stored in the SymmetricDS properties file or in the [parameter] (http://www.symmetricds.org/doc/3.5/html/data-model.html#table_parameter) table in the database. 

The following represents the required properties:

```
mongodb.username=xxxx
mongodb.password=xxxx
mongodb.host=xxxx
mongodb.port=xxxx

```

### How Relational Data is Mapped to mongoDB

The default mapping of relational data to mongoDB is managed by [SimpleDBObjectMapper] (https://github.com/JumpMind/symmetric-mongo/blob/master/src/main/java/org/jumpmind/symmetric/io/data/writer/SimpleDBObjectMapper.java).  SimpleDBObjectMapper is a [IDBObjectMapper] (https://github.com/JumpMind/symmetric-mongo/blob/master/src/main/java/org/jumpmind/symmetric/io/data/writer/IDBObjectMapper.java).  If the default mapper does not meet your needs you can implement and inject a custom mapper.

IDBObjectMapper maps to a mongoDB database, collection and document.

By default, the catalog or schema passed by SymmetricDS will be used for the mongoDB database name.  The table passed by SymmetricDS will be used as the mongoDB collection name.

The _id of the mongoDB document will be the primary key of the database record.  If the table has a composite primary key, then the _id will be an embedded document that has name value pairs of the composite key.  The body of the document will be name value pairs of the table column name and table row value.
 
