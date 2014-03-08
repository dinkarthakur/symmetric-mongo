package org.jumpmind.symmetric.io.data.writer;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Map;
import java.util.Set;

import org.jumpmind.db.model.Table;
import org.jumpmind.symmetric.SymmetricException;
import org.jumpmind.symmetric.io.data.CsvData;

import com.mongodb.BasicDBObject;
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
        return upsert(data);
    }

    @Override
    protected LoadStatus update(CsvData data, boolean applyChangesOnly, boolean useConflictDetection) {
        return upsert(data);
    }

    protected LoadStatus upsert(CsvData data) {
        DB db = clientManager.getDB(getMongoDatabaseNameFrom(this.targetTable));
        DBCollection collection = db.getCollection(getMongoCollectionNameFrom(this.targetTable));
        Map<String, String> newData = data.toColumnNameValuePairs(sourceTable.getColumnNames(),
                CsvData.ROW_DATA);

        DBObject query = buildWithKey(data, newData);
        DBObject object = buildWithKeyAndData(data, newData);
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
        DB db = clientManager.getDB(getMongoDatabaseNameFrom(this.targetTable));
        DBCollection collection = db.getCollection(getMongoCollectionNameFrom(this.targetTable));
        Map<String, String> newData = data.toColumnNameValuePairs(sourceTable.getColumnNames(),
                CsvData.ROW_DATA);

        DBObject query = buildWithKey(data, newData);
        WriteResult results = collection.remove(query, WriteConcern.ACKNOWLEDGED);
        if (results.getN() != 1) {
            log.warn("Attempted to remove a single object" + query.toString() + ".  Instead removed: " + results.getN());
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

    protected BasicDBObject buildWithKey(CsvData data, Map<String, String> newData) {
        Map<String, String> oldData = data.toColumnNameValuePairs(sourceTable.getColumnNames(),
                CsvData.OLD_DATA);
        if (oldData == null) {
            oldData = data.toColumnNameValuePairs(sourceTable.getColumnNames(), CsvData.PK_DATA);
        }
        if (oldData == null) {
            oldData = newData;
        }

        String[] keyNames = sourceTable.getPrimaryKeyColumnNames();

        BasicDBObject object = new BasicDBObject();

        // TODO support property to just let mongodb create ids?
        if (keyNames != null && keyNames.length > 0) {
            if (keyNames.length == 1) {
                object.put("_id", oldData.get(keyNames[0]));
            } else {
                BasicDBObject key = new BasicDBObject();
                for (String keyName : keyNames) {
                    key.put(keyName, oldData.get(keyName));
                }
                object.put("_id", key);
            }
        }

        return object;
    }

    protected BasicDBObject buildWithKeyAndData(CsvData data, Map<String, String> newData) {
        /*
         * TODO It looks like mongodb handles strings, byte[] and date() objects
         * how do we determine when to insert certain types because there is no
         * schema in mongo db.
         * 
         * One idea I had was to cache create xml for tables somewhere in
         * mongodb and use it to determine types from the source.
         * 
         * _id is the primary key of a mongo. map composite keys using a
         * dbobject as the _id
         */

        BasicDBObject object = buildWithKey(data, newData);

        Set<String> newDataKeys = newData.keySet();
        for (String newDataKey : newDataKeys) {
            object.put(newDataKey, newData.get(newDataKey));
        }

        return object;
    }

    protected String getMongoCollectionNameFrom(Table table) {
        return table.getName();
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
