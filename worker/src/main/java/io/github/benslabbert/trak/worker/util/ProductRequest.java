package io.github.benslabbert.trak.worker.util;

import io.github.benslabbert.trak.worker.response.ProductResponse;
import io.github.benslabbert.trak.worker.response.takealot.TakealotApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@Slf4j
public abstract class ProductRequest {

  private Optional<ProductResponse> getProductResponse(String url, int retry) {
    if (retry > 3) {
      log.info("Max retries reached!");
      return Optional.empty();
    }

    try {
      RestTemplate restTemplate = new RestTemplate();
      // todo
      //  javax.net.ssl.SSLHandshakeException: Received fatal alert: handshake_failure
      //  adding this vm arg -Dhttps.protocols=TLSv1.2,TLSv1.1,TLSv1 appears to have helped

      ResponseEntity<TakealotApiResponse> exchange =
          restTemplate.exchange(URI.create(url), HttpMethod.GET, null, TakealotApiResponse.class);

      if (exchange == null || exchange.getBody() == null) {
        log.warn("Failed to get product with URL: {}", url);
        return Optional.empty();
      }

      return Optional.ofNullable(exchange.getBody());

    } catch (HttpClientErrorException e) {
      log.warn(
          "{}: Client exception: {} with HTTP Status code: {}",
          e.getMessage(),
          url,
          e.getRawStatusCode());
      return Optional.empty();
    } catch (ResourceAccessException e) {
      log.warn("ResourceAccessException: Failed to Access API: " + url);
      return getProductResponse(url, ++retry);
    } catch (Exception e) {
      log.warn("Failed to deserialize api response: " + url, e);
      return Optional.empty();
    }
  }

  protected Optional<ProductResponse> getProductResponse(String url) {
    return getProductResponse(url, 0);
  }
}
