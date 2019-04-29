package io.github.benslabbert.trak.worker.util;

import io.github.benslabbert.trak.worker.response.ProductResponse;
import io.github.benslabbert.trak.worker.response.takealot.TakealotApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@Slf4j
public abstract class ProductRequest {

  protected Optional<ProductResponse> getProductResponse(String url) {

    try {
      RestTemplate restTemplate = new RestTemplate();
      // todo this appears to be broken when in the container
      // javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure

      ResponseEntity<TakealotApiResponse> exchange =
          restTemplate.exchange(URI.create(url), HttpMethod.GET, null, TakealotApiResponse.class);

      if (exchange == null) return Optional.empty();

      return Optional.ofNullable(exchange.getBody());

    } catch (HttpClientErrorException e) {
      log.warn(
          "{}: Client exception: {} with HTTP Status code: {}",
          e.getMessage(),
          url,
          e.getRawStatusCode());
      return Optional.empty();
    } catch (Exception e) {
      log.warn("Failed to deserialize api response: " + url, e);
      return Optional.empty();
    }
  }
}
