package com.auction.point.api.feign.service;

import com.auction.point.api.common.apipayload.ApiResponse;
import com.auction.point.api.common.exception.ApiException;
import com.auction.point.api.feign.dto.response.CouponGetResponseDto;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import feign.RetryableException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CouponServiceTest {
    @Autowired
    private CouponService couponService;

    @MockBean
    private HttpServletRequest request;

    private static final int PORT = 9999;
    private static final WireMockServer wireMockServer = new WireMockServer(PORT);

    @DynamicPropertySource
    static void configureFeignClientBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("feign.server.coupon", () -> "http://localhost:" + wireMockServer.port());
    }

    @BeforeAll
    public static void beforeAll() {
        wireMockServer.start();
        WireMock.configureFor("localhost", PORT);
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.resetAll();
    }

    @Test
    @DisplayName("쿠폰 조회 페잉 테스트 - 성공")
    public void getCouponFeignTest_success() {
        // given
        long userId = 1L;
        long couponUserId = 1L;

        String successJson = """
                {
                    "success": "true",
                    "statusCode": "200",
                    "message": "요청이 정상 처리되었습니다.",
                    "data": {"discountRate": 10}
                }
                """;
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/api/internal/v4/coupons/" + couponUserId))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(successJson)
                )
        );

        // when
        ApiResponse<CouponGetResponseDto> response = couponService.getValidCoupon(userId, couponUserId);

        // then
        wireMockServer.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/api/internal/v4/coupons/" + couponUserId)));
        assertNotNull(response.getData());
        assertEquals(10, response.getData().getDiscountRate());
    }

    @Test
    @DisplayName("쿠폰 조회 페잉 테스트 - 4XX 실패")
    public void getCouponFeignTest_failure_4XX() {
        // given
        long userId = 1L;
        long couponUserId = 2L;

        String failureJson = """
                {
                    "success": "false",
                    "statusCode": "400",
                    "message": "사용이 불가능한 쿠폰입니다."
                }
                """;
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/api/internal/v4/coupons/" + couponUserId))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(failureJson)
                )
        );

        // when, then
        assertThrows(ApiException.class,
                () -> couponService.getValidCoupon(userId, couponUserId));
    }

    @Test
    @DisplayName("쿠폰 조회 페잉 테스트 - 5XX 실패")
    public void getCouponFeignTest_failure_5XX() {
        // given
        long userId = 1L;
        long couponUserId = 2L;

        String failureJson = """
                {
                    "success": "false",
                    "statusCode": "500",
                    "message": "잘못된 요청입니다."
                }
                """;
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/api/internal/v4/coupons/" + couponUserId))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(failureJson)
                )
        );

        // when, then
        assertThrows(RetryableException.class,
                () -> couponService.getValidCoupon(userId, couponUserId));
    }

}