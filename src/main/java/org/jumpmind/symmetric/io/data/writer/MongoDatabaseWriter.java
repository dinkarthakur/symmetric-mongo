package org.jumpmind.symmetric.io.data.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.db.model.Table;
import org.jumpmind.symmetric.io.data.Batch;
import org.jumpmind.symmetric.io.data.CsvData;
import org.jumpmind.symmetric.io.data.DataContext;
import org.jumpmind.symmetric.io.data.IDataWriter;
import org.jumpmind.util.Statistics;

public class MongoDatabaseWriter implements IDataWriter {

    protected Map<Batch, Statistics> statistics = new HashMap<Batch, Statistics>();
    protected List<IDatabaseWriterFilter> filters;
    protected List<IDatabaseWriterErrorHandler> errorHandlers;
    protected List<? extends Conflict> conflictSettings;

    public MongoDatabaseWriter() {
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
