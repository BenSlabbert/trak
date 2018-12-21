package com.trak.worker.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trak.worker.response.ProductResponse;
import com.trak.worker.response.takealot.TakealotApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Slf4j
abstract class ProductRequest {

  Optional<ProductResponse> getProductResponse(String url) {

    HttpURLConnection con = null;

    try {

      con = (HttpURLConnection) new URL(url).openConnection();
      con.setRequestMethod("GET");

      String response = IOUtils.toString(con.getInputStream());

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
