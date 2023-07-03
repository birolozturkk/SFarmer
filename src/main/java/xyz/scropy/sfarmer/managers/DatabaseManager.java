package xyz.scropy.sfarmer.managers;


import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.logger.NullLogBackend;
import com.j256.ormlite.stmt.query.In;
import com.j256.ormlite.support.ConnectionSource;
import lombok.Getter;
import xyz.scropy.sfarmer.SFarmerPlugin;
import xyz.scropy.sfarmer.config.SQL;
import xyz.scropy.sfarmer.model.AutoSell;
import xyz.scropy.sfarmer.repository.Repository;
import xyz.scropy.sfarmer.repository.impl.AutoSellRepository;
import xyz.scropy.sfarmer.repository.impl.CollectedItemRepository;
import xyz.scropy.sfarmer.repository.impl.FarmerRepository;

import java.io.File;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.UUID;

@Getter
public class DatabaseManager {

    private ConnectionSource connectionSource;

    private FarmerRepository farmerRepository;
    private CollectedItemRepository collectedItemRepository;
    private AutoSellRepository autoSellRepository;

    public void init() throws SQLException {
        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        SQL sqlConfig = SFarmerPlugin.getInstance().getSql();
        String databaseURL = getDatabaseURL(sqlConfig);

        this.connectionSource = new JdbcConnectionSource(
                databaseURL,
                sqlConfig.username,
                sqlConfig.password,
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        this.collectedItemRepository = new CollectedItemRepository(connectionSource);
        this.farmerRepository = new FarmerRepository(connectionSource);
        this.autoSellRepository = new AutoSellRepository(connectionSource);
    }

    /**
     * Database connection String used for establishing a connection.
     *
     * @return The database URL String
     */
    private String getDatabaseURL(SQL sqlConfig) {
        switch (sqlConfig.driver) {
            case MYSQL:
                return "jdbc:" + sqlConfig.driver.name().toLowerCase() + "://" + sqlConfig.host + ":" + sqlConfig.port + "/" + sqlConfig.database + "?useSSL=" + sqlConfig.useSSL;
            case SQLITE:
                return "jdbc:sqlite:" + new File(SFarmerPlugin.getInstance().getDataFolder(), sqlConfig.database + ".db");
            default:
                throw new UnsupportedOperationException("Unsupported driver (how did we get here?): " + sqlConfig.driver.name());
        }
    }

}
