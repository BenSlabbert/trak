package io.github.benslabbert.trak.entity.jpa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import javax.persistence.OptimisticLockException;

@Slf4j
public abstract class RetryPersist<T, ID> {

  T retry(T object, JpaRepository<T, ID> repo) {
    return retry(object, 1, repo);
  }

  T retry(T object, int retry, JpaRepository<T, ID> repo) {

    if (retry > 3) {
      log.error("Failed to save entity: Product");
      throw new OptimisticLockingFailureException("Failed to persist product!");
    } else if (retry > 1) {
      log.warn("Lock exception, retrying... {}", retry);
    }

    try {
      return repo.saveAndFlush(object);
    } catch (OptimisticLockException e) {
      log.warn("OptimisticLockException while saving product! Retrying ...");
      return retry(object, retry++, repo);
    } catch (ObjectOptimisticLockingFailureException e) {
      log.warn("ObjectOptimisticLockingFailureException while saving product! Retrying ...");
      return retry(object, retry++, repo);
    }
  }
}
