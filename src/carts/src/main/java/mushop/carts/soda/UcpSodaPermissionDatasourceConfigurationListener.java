package mushop.carts.soda;

import io.micronaut.configuration.jdbc.ucp.DatasourceConfiguration;
import io.micronaut.context.BeanLocator;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.oraclecloud.atp.jdbc.AutonomousDatabaseConfiguration;
import io.micronaut.oraclecloud.atp.jdbc.OracleWalletArchiveProvider;
import io.micronaut.oraclecloud.atp.wallet.datasource.CanConfigureOracleDataSource;
import io.micronaut.transaction.jdbc.DelegatingDataSource;
import oracle.jdbc.datasource.impl.OracleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Requires(classes = DatasourceConfiguration.class)
@Context
@Singleton
public class UcpSodaPermissionDatasourceConfigurationListener implements BeanCreatedEventListener<DatasourceConfiguration> {
    private static final Logger LOG = LoggerFactory.getLogger(UcpSodaPermissionDatasourceConfigurationListener.class);

    private final OracleWalletArchiveProvider walletArchiveProvider;
    private final BeanLocator beanLocator;

    /**
     * Default constructor.
     *
     * @param walletArchiveProvider The wallet archive provider
     * @param beanLocator           The bean locator
     */
    public UcpSodaPermissionDatasourceConfigurationListener(@Nullable OracleWalletArchiveProvider walletArchiveProvider, BeanLocator beanLocator) {
        this.walletArchiveProvider = walletArchiveProvider;
        this.beanLocator = beanLocator;
    }

    @Override
    public DatasourceConfiguration onCreated(BeanCreatedEvent<DatasourceConfiguration> event) {
        DatasourceConfiguration datasourceConfiguration = event.getBean();

        OracleSodaConfiguration.SodaConfiguration soda = beanLocator.findBean(OracleSodaConfiguration.SodaConfiguration.class, Qualifiers.byName(datasourceConfiguration.getName())).orElse(null);
        if (soda == null) {
            LOG.info("Soda configuration not found for datasource: {}", datasourceConfiguration.getUsername());
            return datasourceConfiguration;
        }
        if (!soda.isCreateSodaUser()) {
            LOG.info("Soda create user is disabled for datasource: {}", datasourceConfiguration.getUsername());
            return datasourceConfiguration;
        }
        AutonomousDatabaseConfiguration autonomousDatabaseConfiguration = beanLocator.findBean(AutonomousDatabaseConfiguration.class,
                Qualifiers.byName(datasourceConfiguration.getName())).orElse(null);
        if (autonomousDatabaseConfiguration == null) {
            LOG.info("Autonomous database configuration is missing for datasource: {}", datasourceConfiguration.getUsername());
            return datasourceConfiguration;
        }

        try {
            LOG.info("Granting a role 'SODA_APP' to user: '{}'", datasourceConfiguration.getUsername());
            OracleDataSource oracleDataSource = new OracleDataSource();
            if (walletArchiveProvider != null) {
                CanConfigureOracleDataSource walletConfigure = walletArchiveProvider.loadWalletArchive(autonomousDatabaseConfiguration);
                walletConfigure.configure(oracleDataSource);
            }
            oracleDataSource.setURL(datasourceConfiguration.getUrl());
            oracleDataSource.setUser(datasourceConfiguration.getUsername());
            oracleDataSource.setPassword(datasourceConfiguration.getPassword());

            try (Connection connection = DelegatingDataSource.unwrapDataSource(oracleDataSource).getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("GRANT SODA_APP TO " + datasourceConfiguration.getUsername())) {
                    preparedStatement.execute();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot upgrade soda user: " + e.getMessage(), e);
        }
        return datasourceConfiguration;
    }

}