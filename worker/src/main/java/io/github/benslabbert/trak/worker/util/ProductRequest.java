package io.github.benslabbert.trak.worker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benslabbert.trak.worker.response.ProductResponse;
import io.github.benslabbert.trak.worker.response.takealot.TakealotApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Slf4j
public abstract class ProductRequest {

    protected Optional<ProductResponse> getProductResponse(String url) {

    HttpURLConnection con = null;

    try {

      con = (HttpURLConnection) new URL(url).openConnection();
      con.setRequestMethod("GET");

        byte[] response = IOUtils.toByteArray(con.getInputStream());

      ObjectMapper mapper = new ObjectMapper();
      ProductResponse apiResponse = mapper.readValue(response, TakealotApiResponse.class);

      con.disconnect();
      return Optional.of(apiResponse);

    } catch (FileNotFoundException e) {
      log.warn("bad api endpoint : {}", url);
      if (con != null) con.disconnect();
      return Optional.empty();
    } catch (IOException e) {
      log.warn("failed to deserialize api response: {}", url);
      if (con != null) con.disconnect();
      return Optional.empty();
    }
  }
}
