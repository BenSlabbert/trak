package io.github.benslabbert.trak.api.grpc;

import io.github.benslabbert.trak.api.rabbitmq.rpc.AddProductRPC;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import io.github.benslabbert.trak.entity.rabbitmq.rpc.AddProductRPCRequestFactory;
import io.github.benslabbert.trak.grpc.AddProductRequest;
import io.github.benslabbert.trak.grpc.AddProductResponse;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class ProductGRPCTest {

    @Mock
    private StreamObserver<AddProductResponse> responseObserver;
    @Mock
    private ProductService productService;
    @Mock
    private SellerService sellerService;
    @Mock
    private AddProductRPC addProductRPC;
    @Mock
    private PriceService priceService;

    @InjectMocks
    private ProductGRPC grpc;

    @Before
    public void init() {
        assertNotNull(grpc);
    }

    @Test
    public void badURLTest() {

        grpc.addProduct(
                AddProductRequest.newBuilder().setPlId("https://someUrl").build(), responseObserver);

        Mockito.verify(responseObserver, Mockito.atLeastOnce()).onError(Mockito.any());
        Mockito.verify(responseObserver, Mockito.atLeastOnce()).onCompleted();
    }

    @Test
    public void goodURLTest_noSellerFound() {

        Mockito.when(sellerService.findByNameEquals("Takealot")).thenReturn(Optional.empty());

        grpc.addProduct(
                AddProductRequest.newBuilder()
                        .setPlId(
                                "https://www.takealot.com/hoppy-easter-adults-unisex-t-shirt-grey/PLID53564640")
                        .build(),
                responseObserver);

        Mockito.verify(responseObserver, Mockito.atLeastOnce()).onError(Mockito.any());
        Mockito.verify(responseObserver, Mockito.atLeastOnce()).onCompleted();
    }

    @Test
    public void goodURLTest_sellerFound() {

        Seller seller = Seller.builder().id(1L).name("Takealot").build();

        Mockito.when(sellerService.findByNameEquals("Takealot")).thenReturn(Optional.of(seller));

        Mockito.when(
                addProductRPC.addProduct(
                        AddProductRPCRequestFactory.create(
                                URI.create(
                                        "https://www.takealot.com/hoppy-easter-adults-unisex-t-shirt-grey/PLID53564640"),
                                seller,
                                53564640L)))
                .thenReturn(123L);

        grpc.addProduct(
                AddProductRequest.newBuilder()
                        .setPlId(
                                "https://www.takealot.com/hoppy-easter-adults-unisex-t-shirt-grey/PLID53564640")
                        .build(),
                responseObserver);

        Mockito.verify(responseObserver, Mockito.never()).onError(Mockito.any());
        Mockito.verify(responseObserver, Mockito.atLeastOnce())
                .onNext(AddProductResponse.newBuilder().setProductId(123L).build());
        Mockito.verify(responseObserver, Mockito.atLeastOnce()).onCompleted();
    }
}
