SymmetricDS mongoDB Database Writer
===============

This project is an implementation of a SymmetricDS [IDataWriter] (http://www.symmetricds.org/doc/3.5/javadoc/org/jumpmind/symmetric/io/data/IDataWriter.html) that maps and writes relational data to a [mongoDB] (http://www.mongodb.org/) NoSQL database.  This project can also be used as a reference for how to write extension point for other non sql based data stores.
 
[SymmetricDS] (http://symmetricds.org) is a database change data capture and synchronization application that specializes in its flexibily and its scalability.  One flexible aspect of SymmetricDS is its extendability through [extension points] (http://www.symmetricds.org/doc/3.5/html/advanced-topics.html#extensions).  This project implements the [IDataLoaderFactory] (http://www.symmetricds.org/doc/3.5/javadoc/org/jumpmind/symmetric/load/IDataLoaderFactory.html) extension point in order to allow a mongoDB data writer to wired into SymmetricDS.

### How to Configure

The mongoDB database writer is very simple to install and use.  The easiest configuration is to drop the symmetric-mongo-XXX.jar file into a standalone SymmetricDS install's web/WEB-INF/lib directory.

The open source version of SymmetricDS can be downloaded from [SourceForge] (http://sourceforge.net/projects/symmetricds/).  A good "getting started" resource is the tutorial in the [Users Guide] (http://www.symmetricds.org/doc/3.5/html/tutorial.html).

The professional version can be downloaded from [jumpmind.com] (http://jumpmind.com).  A good "getting started" resource for the professional version is the [Quick Start Guide] (http://www.jumpmind.com/downloads/symmetricds/doc/).

A good overview of SymmetricDS can be found [here] (http://www.symmetricds.org/doc/3.5/html/introduction.html#definition).

The configuration for a SymmetricDS node is stored in a relational database.  Even though the mongoDB writer will be writing to a NoSQL data sink, the configuration for the node still requires a relational database.  The configuration database can be any of the supported [databases] (http://www.symmetricds.org/doc/3.5/html/databases.html).

The mongoDB writer can be enabled by setting the DATA_LOADER_TYPE on the [channel] (http://www.symmetricds.org/doc/3.5/html/data-model.html#table_channel) that will be used to sync data.  The default data loader type for the mongoDB writer is _mongodb_.

Tables that should be synchronized to mongoDB should be configured on this channel.

The last configuration step is to supply the writer with settings on how to communicate with the mongoDB database.  These setting can be stored in the SymmetricDS properties file or in the [parameter] (http://www.symmetricds.org/doc/3.5/html/data-model.html#table_parameter) table in the database. 

The following represents the required properties:

```

mongodb.username=xxxx
mongodb.password=xxxx
mongodb.host=xxxx
mongodb.port=xxxx

```

 
### How Relational Data is Mapped to mongoDB
