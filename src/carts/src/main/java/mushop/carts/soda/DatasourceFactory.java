package mushop.carts.soda;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.exceptions.ConfigurationException;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.jdbc.BasicJdbcConfiguration;
import io.micronaut.oraclecloud.atp.jdbc.AutonomousDatabaseConfiguration;
import io.micronaut.oraclecloud.atp.jdbc.OracleWalletArchiveProvider;
import io.micronaut.oraclecloud.atp.wallet.datasource.CanConfigureOracleDataSource;
import oracle.jdbc.datasource.impl.OracleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//@Factory
public class DatasourceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DatasourceFactory.class);

    private final ApplicationContext applicationContext;
    private final OracleWalletArchiveProvider walletArchiveProvider;

    /**
     * Default constructor.
     *
     * @param applicationContext    The application context
     * @param walletArchiveProvider
     */
    public DatasourceFactory(ApplicationContext applicationContext, OracleWalletArchiveProvider walletArchiveProvider) {
        this.applicationContext = applicationContext;
        this.walletArchiveProvider = walletArchiveProvider;
    }

    @Context
    @EachBean(BasicJdbcConfiguration.class)
    public OracleDataSource dataSource(BasicJdbcConfiguration datasourceConfiguration) {

        AutonomousDatabaseConfiguration autonomousDatabaseConfiguration = applicationContext.findBean(AutonomousDatabaseConfiguration.class,
                Qualifiers.byName(datasourceConfiguration.getName())).orElse(null);

        CanConfigureOracleDataSource walletArchive = null;

        if (autonomousDatabaseConfiguration == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("No AutonomousDatabaseConfiguration for [" + datasourceConfiguration.getName() + "] datasource");
            }
        } else if (autonomousDatabaseConfiguration.getOcid() == null || autonomousDatabaseConfiguration.getWalletPassword() == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Skipping configuration of Oracle Wallet due to missin ocid or wallet password in " +
                        "AutonomousDatabaseConfiguration for [" + datasourceConfiguration.getName() + "] datasource");
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Retrieving Oracle Wallet for DataSource [" + datasourceConfiguration.getName() + "]");
            }
            walletArchive = walletArchiveProvider.loadWalletArchive(autonomousDatabaseConfiguration);
        }

        try {
            try (Connection connection = createDataSource(datasourceConfiguration, walletArchive).getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("GRANT SODA_APP TO " + datasourceConfiguration.getUsername())) {
                    preparedStatement.execute();
                }
            } catch (SQLException e) {
                throw new ConfigurationException("Error initializing SODA: " + e.getMessage(), e);
            }
            return createDataSource(datasourceConfiguration, walletArchive);
        } catch (SQLException | IOException e) {
            throw new ConfigurationException("Error configuring the [" + datasourceConfiguration.getName() + "] datasource: " + e.getMessage(), e);
        }
    }

    private OracleDataSource createDataSource(BasicJdbcConfiguration datasourceConfiguration, CanConfigureOracleDataSource walletArchive) throws SQLException, IOException {
        OracleDataSource oracleDataSource = new OracleDataSource();
        if (walletArchive != null) {
            walletArchive.configure(oracleDataSource);
        }
        oracleDataSource.setURL(datasourceConfiguration.getUrl());
        oracleDataSource.setUser(datasourceConfiguration.getUsername());
        oracleDataSource.setPassword(datasourceConfiguration.getPassword());
        return oracleDataSource;
    }

}