package org.jumpmind.symmetric.io.data.writer;

import java.net.UnknownHostException;

import org.jumpmind.symmetric.SymmetricException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoClientWrapper {

    protected MongoClient client;

    protected String host;

    protected int port;

    protected String username;

    protected String password;

    protected DB db;

    public MongoClientWrapper(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    protected MongoClient getClient() {
        if (client == null) {
            try {
                client = new MongoClient(host, port);
            } catch (UnknownHostException e) {
                throw new SymmetricException(e);
            }
        }
        return client;
    }

    public DB getDB(String name) {
        if (db == null || !db.getName().equals(name)) {
            db = getClient().getDB(name);
            if (!db.authenticate(username, password.toCharArray())) {
                throw new SymmetricException("Failed to authenticate with the mongo database: "
                        + name);
            }
        }
        return db;
    }

}
