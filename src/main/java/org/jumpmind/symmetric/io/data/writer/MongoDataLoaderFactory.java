package org.jumpmind.symmetric.io.data.writer;

import java.util.List;

import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.extension.IExtensionPoint;
import org.jumpmind.symmetric.ISymmetricEngine;
import org.jumpmind.symmetric.db.ISymmetricDialect;
import org.jumpmind.symmetric.ext.ISymmetricEngineAware;
import org.jumpmind.symmetric.io.data.IDataWriter;
import org.jumpmind.symmetric.load.IDataLoaderFactory;

public class MongoDataLoaderFactory implements IDataLoaderFactory, ISymmetricEngineAware,
        IExtensionPoint {

    protected ISymmetricEngine engine;
    
    @Override
    public void setSymmetricEngine(ISymmetricEngine engine) {
        this.engine = engine;
    }

    @Override
    public String getTypeName() {
        return "mongodb";
    }

    @Override
    public IDataWriter getDataWriter(String sourceNodeId, ISymmetricDialect symmetricDialect,
            TransformWriter transformWriter, List<IDatabaseWriterFilter> filters,
            List<IDatabaseWriterErrorHandler> errorHandlers,
            List<? extends Conflict> conflictSettings, List<ResolvedData> resolvedData) {
        MongoDatabaseWriter writer = new MongoDatabaseWriter();
        writer.setConflictSettings(conflictSettings);
        writer.setErrorHandlers(errorHandlers);
        writer.setFilters(filters);
        writer.setParameterService(engine.getParameterService());
        return writer;
    }

    @Override
    public boolean isPlatformSupported(IDatabasePlatform platform) {
        return false;
    }

}
