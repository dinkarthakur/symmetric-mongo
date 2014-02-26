package org.jumpmind.symmetric.io.data.writer;

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

public class MongoDatabaseWriter implements IDataWriter {

    /*
     * TODO should there be a direct reference to the parameter service? if we
     * remove it we could prob use this write in dbimport/export TODO username
     * and password are really per databsae TODO configuration of a clustered
     * mongo instance is different than passing host and post
     */

    protected Map<Batch, Statistics> statistics = new HashMap<Batch, Statistics>();

    protected List<IDatabaseWriterFilter> filters;

    protected List<IDatabaseWriterErrorHandler> errorHandlers;

    protected List<? extends Conflict> conflictSettings;

    protected IParameterService parameterService;

    protected static MongoClientWrapper client;

    public MongoDatabaseWriter() {
    }

    protected void init() {
        if (client == null) {
            int port = 27017;
            String host = "localhost";
            String username = null;
            String password = null;
            if (parameterService != null) {
                port = parameterService.getInt(MongoConstants.PORT, port);
                host = parameterService.getString(MongoConstants.HOST, host);
                username = parameterService.getString(MongoConstants.USERNAME, username);
                password = parameterService.getString(MongoConstants.PASSWORD, password);
            }
            client = new MongoClientWrapper(host, port, username, password);
        }
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
    }

    @Override
    public boolean start(Table table) {
        return true;
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

}
