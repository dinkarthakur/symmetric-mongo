SymmetricDS Mongo Database Writer
===============

This project is an implementation of a SymmetricDS [IDataWriter] (http://www.symmetricds.org/doc/3.5/javadoc/org/jumpmind/symmetric/io/data/IDataWriter.html) that maps and writes relational data to a [mongoDB] (http://www.mongodb.org/) NoSQL database.  This project can also be used as a reference for how to write extension point for other non sql based data stores.

[SymmetricDS] (http://symmetricds.org) is a database change data capture and synchronization application that specializes in its flexibily and its scalability.  One flexible aspect of SymmetricDS is its extendability through [extension points] (http://www.symmetricds.org/doc/3.5/html/advanced-topics.html#extensions).  This project implements the [IDataLoaderFactory] (http://www.symmetricds.org/doc/3.5/javadoc/org/jumpmind/symmetric/load/IDataLoaderFactory.html) extension point in order to allow a mongoDB data writer to wired into SymmetricDS.

### How to Configure

### How Relational Data is Mapped to mongoDB

### How the DatabaseWriter Works
