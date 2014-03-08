package org.jumpmind.symmetric.io.data.writer;

import java.util.Map;

import org.jumpmind.symmetric.SymmetricException;
import org.jumpmind.symmetric.io.data.CsvData;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * The default mapping is that a catalog or schema or catalog.schema is mapped
 * to a Mongo database that is named the same as the catalog and/or schema
 * and/or pair combined.
 */
public class MongoDatabaseWriter extends AbstractDatabaseWriter {

    /*
     * TODO talk about initial load. if reload channel is set to mongodb then
     * sym_node_security records would be written to mongo unless we filtered
     * out sym_ records. sym_node_security would never get updated in the
     * regular database if i turn off the use.reload property initial load
     * works.
     * 
     * TODO add support for mappings between catalog/schema and mongo database
     * names. Should it be in the properties file? Should this be entire
     * database writer be driven by a separate properties file (or xml or json
     * file).
     * 
     * TODO property for write concern
     * http://api.mongodb.org/java/current/com/mongodb
     * /WriteConcern.html#ACKNOWLEDGED
     * 
     * TODO bulk load test (flush lots of dbobjects together for performance
     * reasons)
     * 
     * TODO support sql execute mongo commands
     * 
     * * TODO It looks like mongodb handles strings, byte[] and date() objects
     * how do we determine when to insert certain types because there is no
     * schema in mongo db. One idea I had was to cache create xml for tables
     * somewhere in mongodb and use it to determine types from the source.
     * 
     * TODO record load time
     */

    protected IMongoClientManager clientManager;

    protected IDBObjectMapper objectMapper;

    public MongoDatabaseWriter(IDBObjectMapper objectMapper, IMongoClientManager clientManager,
            IDatabaseWriterConflictResolver conflictResolver, DatabaseWriterSettings settings) {
        super(conflictResolver, settings);
        this.clientManager = clientManager;
        this.objectMapper = objectMapper;
    }

    @Override
    protected LoadStatus insert(CsvData data) {
        return upsert(data);
    }

    @Override
    protected LoadStatus update(CsvData data, boolean applyChangesOnly, boolean useConflictDetection) {
        return upsert(data);
    }

    protected LoadStatus upsert(CsvData data) {
        DB db = clientManager.getDB(objectMapper.mapToDatabase(this.targetTable));
        DBCollection collection = db.getCollection(objectMapper.mapToCollection(this.targetTable));
        String[] columnNames = sourceTable.getColumnNames();
        Map<String, String> newData = data.toColumnNameValuePairs(columnNames, CsvData.ROW_DATA);
        Map<String, String> oldData = data.toColumnNameValuePairs(columnNames, CsvData.OLD_DATA);
        Map<String, String> pkData = data.toColumnNameValuePairs(
                sourceTable.getPrimaryKeyColumnNames(), CsvData.PK_DATA);
        DBObject query = objectMapper.mapToDBObject(sourceTable, newData, oldData, pkData, true);
        DBObject object = objectMapper.mapToDBObject(sourceTable, newData, oldData, pkData, false);
        WriteResult results = collection.update(query, object, true, false,
                WriteConcern.ACKNOWLEDGED);
        if (results.getN() == 1) {
            return LoadStatus.SUCCESS;
        } else {
            throw new SymmetricException("Failed to write data: " + object.toString());
        }
    }

    @Override
    protected LoadStatus delete(CsvData data, boolean useConflictDetection) {
        DB db = clientManager.getDB(objectMapper.mapToDatabase(this.targetTable));
        DBCollection collection = db.getCollection(objectMapper.mapToCollection(this.targetTable));
        String[] columnNames = sourceTable.getColumnNames();
        Map<String, String> newData = data.toColumnNameValuePairs(columnNames, CsvData.ROW_DATA);
        Map<String, String> oldData = data.toColumnNameValuePairs(columnNames, CsvData.OLD_DATA);
        Map<String, String> pkData = data.toColumnNameValuePairs(
                sourceTable.getPrimaryKeyColumnNames(), CsvData.PK_DATA);
        DBObject query = objectMapper.mapToDBObject(sourceTable, newData, oldData, pkData, true);
        WriteResult results = collection.remove(query, WriteConcern.ACKNOWLEDGED);
        if (results.getN() != 1) {
            log.warn("Attempted to remove a single object" + query.toString()
                    + ".  Instead removed: " + results.getN());
        }
        return LoadStatus.SUCCESS;

    }

    @Override
    protected boolean create(CsvData data) {
        return true;
    }

    @Override
    protected boolean sql(CsvData data) {
        return false;
    }

}
