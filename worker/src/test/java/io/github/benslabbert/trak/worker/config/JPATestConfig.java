package io.github.benslabbert.trak.worker.config;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.github.benslabbert.trak.worker.config.Profiles.JPA_TEST_POFILE;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "io.github.benslabbert.trak.entity.jpa.service")
@EnableJpaRepositories(basePackages = "io.github.benslabbert.trak.entity.jpa.repo")
@Profile(JPA_TEST_POFILE)
public class JPATestConfig {

  @MockBean RabbitTemplate rabbitTemplate;

  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .setScriptEncoding("UTF-8")
        .ignoreFailedDrops(true)
        .addScripts("data.sql")
        .build();
  }

  @Bean
  public JpaVendorAdapter jpaVendorAdapter() {

    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

    adapter.setShowSql(true);
    adapter.setGenerateDdl(true);
    adapter.setDatabase(Database.H2);
    adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");

    return adapter;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {

    LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    emf.setDataSource(dataSource);
    emf.setPackagesToScan("io.github.benslabbert.trak.entity.jpa");
    emf.setJpaVendorAdapter(jpaVendorAdapter);

    return emf;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }

  @Bean
  public BeanPostProcessor persistenceTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }

  @Component
  public static class TestLockRegistryConfig implements DistributedLockRegistry {

    private ReentrantLock reentrantLock = new ReentrantLock();

    @Override
    public String getRegistryKey() {
      return "test-registry";
    }

    @Override
    public Lock obtain(String lockKey) {
      return reentrantLock;
    }
  }
}
