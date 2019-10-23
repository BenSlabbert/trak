package io.github.benslabbert.trak.search.sonic;

import com.github.twohou.sonic.ChannelFactory;
import com.github.twohou.sonic.ControlChannel;
import com.github.twohou.sonic.IngestChannel;
import com.github.twohou.sonic.SearchChannel;
import io.github.benslabbert.trak.search.config.SonicConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Create as many instances of this bean using Spring's ObjectFactory
 *
 * <p>These methods are not thread safe, create an object pool of this class
 *
 * @see org.springframework.beans.factory.ObjectFactory
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SonicChannelPrototype {

  private final ChannelFactory factory;
  private ControlChannel control;
  private SearchChannel search;
  private IngestChannel ingest;

  public SonicChannelPrototype(final SonicConfig sonicConfig) throws IOException {
    this.factory =
        new ChannelFactory(
            sonicConfig.getAddress(),
            sonicConfig.getPort(),
            sonicConfig.getPassword(),
            sonicConfig.getConnectionTimeout(),
            sonicConfig.getReadTimeout());
    this.ingest = factory.newIngestChannel();
    this.search = factory.newSearchChannel();
    this.control = factory.newControlChannel();
  }

  void ingest(String collection, String bucket, String object, String text) throws IOException {
    ingest.ping();
    ingest.push(collection, bucket, object, text);
  }

  void consolidate() throws IOException {
    control.consolidate();
  }

  List<String> query(String collection, String bucket, String query) throws IOException {
    return search.query(collection, bucket, query);
  }

  List<String> suggest(String collection, String bucket, String query) throws IOException {
    return search.suggest(collection, bucket, query);
  }

  void ping() {
    try {
      ingest.ping();
    } catch (IOException e) {
      log.warn("Failed to ping ingest, recreate");
      try {
        ingest = factory.newIngestChannel();
      } catch (IOException ex) {
        log.error("Failed to recreate ingest channel: ", ex);
      }
    }

    try {
      search.ping();
    } catch (IOException e) {
      log.warn("Failed to ping search, recreate");
      try {
        search = factory.newSearchChannel();
      } catch (IOException ex) {
        log.error("Failed to recreate search channel: ", ex);
      }
    }

    try {
      control.ping();
    } catch (IOException e) {
      log.warn("Failed to ping control, recreate");
      try {
        control = factory.newControlChannel();
      } catch (IOException ex) {
        log.error("Failed to recreate control channel: ", ex);
      }
    }
  }
}

@Getter
class KV {
  private final String object;
  private final String text;

  KV(String object, String text) {
    this.object = object;
    this.text = text;
  }
}
