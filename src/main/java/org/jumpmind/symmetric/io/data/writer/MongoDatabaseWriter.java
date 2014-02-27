package org.jumpmind.symmetric.io.data.writer;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.db.model.Table;
import org.jumpmind.symmetric.io.data.Batch;
import org.jumpmind.symmetric.io.data.CsvData;
import org.jumpmind.symmetric.io.data.DataContext;
import org.jumpmind.symmetric.io.data.IDataWriter;
import org.jumpmind.symmetric.service.IParameterService;
import org.jumpmind.util.Statistics;

import com.mongodb.DB;

/**
 * The default mapping is that a catalog or schema or catalog.schema is mapped
 * to a Mongo database that is named the same as the catalog and/or schema
 * and/or pair combined.
 */
public class MongoDatabaseWriter implements IDataWriter {

    /*
     * TODO add support for mappings between catalog/schema and mongo database
     * names. Should it be in the properties file? Should this be entire
     * database writer be driven by a separate properties file (or xml or json
     * file).
     * 
     * TODO should there be a direct reference to the parameter service? if we
     * remove it we could prob use this write in dbimport/export
     * 
     * TODO username and password are really per databsae
     * 
     * TODO configuration of a clustered mongo instance is different than
     * passing host and post
     * 
     * TODO property for write concern
     * http://api.mongodb.org/java/current/com/mongodb
     * /WriteConcern.html#ACKNOWLEDGED
     */

    protected Map<Batch, Statistics> statistics = new HashMap<Batch, Statistics>();

    protected List<IDatabaseWriterFilter> filters;

    protected List<IDatabaseWriterErrorHandler> errorHandlers;

    protected List<? extends Conflict> conflictSettings;

    protected IParameterService parameterService;

    protected Table currentTable;

    protected DB currentDB;

    protected Statistics currentStatistics;

    /*
     * This is static because it wraps a pool of connections
     */
    protected static MongoClientWrapper clientWrapper;

    protected String defaultCollection;

    public MongoDatabaseWriter() {
    }

    protected void init() {
        if (clientWrapper == null) {
            int port = 27017;
            String host = "localhost";
            String username = null;
            String password = null;
            if (parameterService != null) {
                port = parameterService.getInt(MongoConstants.PORT, port);
                host = parameterService.getString(MongoConstants.HOST, host);
                username = parameterService.getString(MongoConstants.USERNAME, username);
                password = parameterService.getString(MongoConstants.PASSWORD, password);
                defaultCollection = parameterService.getString(MongoConstants.DEFAULT_COLLECTION,
                        "default");
            }
            clientWrapper = new MongoClientWrapper(host, port, username, password);
        }
    }

    @Override
    public void open(DataContext context) {
        init();
    }

    @Override
    public void close() {
    }

    @Override
    public Map<Batch, Statistics> getStatistics() {
        return statistics;
    }

    @Override
    public void start(Batch batch) {
        currentStatistics = new Statistics();
        statistics.put(batch, currentStatistics);
    }

    @Override
    public boolean start(Table table) {
        currentTable = table;
        currentDB = clientWrapper.getDB(getMongoDatabaseNameFrom(table));
        return true;
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
            mongoDatabaseName = defaultCollection;
        }

        return mongoDatabaseName;
    }

    @Override
    public void write(CsvData data) {
    }

    @Override
    public void end(Table table) {
    }

    @Override
    public void end(Batch batch, boolean inError) {
    }

    public void setParameterService(IParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setConflictSettings(List<? extends Conflict> conflictSettings) {
        this.conflictSettings = conflictSettings;
    }

    public void setErrorHandlers(List<IDatabaseWriterErrorHandler> errorHandlers) {
        this.errorHandlers = errorHandlers;
    }

    public void setFilters(List<IDatabaseWriterFilter> filters) {
        this.filters = filters;
    }

}
