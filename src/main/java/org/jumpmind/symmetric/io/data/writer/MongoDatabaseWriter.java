package org.jumpmind.symmetric.io.data.writer;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import org.jumpmind.db.model.Table;
import org.jumpmind.symmetric.io.data.CsvData;

/**
 * The default mapping is that a catalog or schema or catalog.schema is mapped
 * to a Mongo database that is named the same as the catalog and/or schema
 * and/or pair combined.
 */
public class MongoDatabaseWriter extends AbstractDatabaseWriter {

    /*
     * TODO add support for mappings between catalog/schema and mongo database
     * names. Should it be in the properties file? Should this be entire
     * database writer be driven by a separate properties file (or xml or json
     * file).
     * 
     * TODO property for write concern
     * http://api.mongodb.org/java/current/com/mongodb
     * /WriteConcern.html#ACKNOWLEDGED
     */
    
    protected IMongoClientManager clientManager;    

    public MongoDatabaseWriter(IMongoClientManager clientManager,
            IDatabaseWriterConflictResolver conflictResolver, DatabaseWriterSettings settings) {
        super(conflictResolver, settings);
        this.clientManager = clientManager;
    }
    
    @Override
    protected LoadStatus insert(CsvData data) {
        return null;
    }

    @Override
    protected LoadStatus update(CsvData data, boolean applyChangesOnly, boolean useConflictDetection) {
        return null;
    }


    @Override
    protected LoadStatus delete(CsvData data, boolean useConflictDetection) {
        return null;
    }

    @Override
    protected boolean create(CsvData data) {
        return false;
    }

    @Override
    protected boolean sql(CsvData data) {
        return false;
    }



    protected String getMongoDatabaseNameFrom(Table table) {
        String mongoDatabaseName = table.getCatalog();
        if (isNotBlank(mongoDatabaseName) && isNotBlank(table.getSchema())) {
            mongoDatabaseName += ".";
        }

        if (isNotBlank(table.getSchema())) {
            mongoDatabaseName += table.getSchema();
        }

        if (isBlank(mongoDatabaseName)) {
            mongoDatabaseName = clientManager.getDefaultDatabaseName();
        }

        return mongoDatabaseName;
    }



}
