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
      throw new OptimisticLockingFailureException("Failed to persist!");
    } else if (retry > 1) {
      log.warn("Lock exception, retrying... {}", retry);
      try {
        Thread.sleep(100L * retry);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new OptimisticLockingFailureException("Failed to sleep retry/persist!");
      }
    }

    try {
      return repo.saveAndFlush(object);
    } catch (OptimisticLockException e) {
      log.warn("OptimisticLockException while saving! Retrying ...");
      return retry(object, ++retry, repo);
    } catch (ObjectOptimisticLockingFailureException e) {
      log.warn("ObjectOptimisticLockingFailureException while saving! Retrying ...");
      return retry(object, ++retry, repo);
    }
  }
}
