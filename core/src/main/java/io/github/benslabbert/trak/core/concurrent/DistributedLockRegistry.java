package io.github.benslabbert.trak.core.concurrent;

import java.util.concurrent.locks.Lock;

public interface DistributedLockRegistry {
  String getRegistryKey();

  Lock obtain(String lockKey);
}
