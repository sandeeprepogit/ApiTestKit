
package com.apitestkit.logging;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RestAssuredMaskingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RestAssuredMaskingFilter.class);

    private final List<String> maskedHeaders;
    private final List<String> maskedJsonFields;

    public RestAssuredMaskingFilter(List<String> maskedHeaders, List<String> maskedJsonFields) {
        this.maskedHeaders = maskedHeaders == null ? List.of() : List.copyOf(maskedHeaders);
        this.maskedJsonFields = maskedJsonFields == null ? List.of("password", "secret", "token") : List.copyOf(maskedJsonFields);
    }

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {

        Map<String, String> headersMap = new LinkedHashMap<>();
        req.getHeaders().forEach(h -> headersMap.put(h.getName(), h.getValue()));

        String reqBody = req.getBody() == null ? null : String.valueOf(req.getBody());
        String maskedReqBody = MaskingUtils.maskJsonFields(reqBody, maskedJsonFields);

        log.info("=== HTTP REQUEST ===\n{} {}\n{}\nBody:\n{}\n",
                req.getMethod(),
                req.getURI(),
                MaskingUtils.headersToString(headersMap, maskedHeaders),
                maskedReqBody == null ? "<empty>" : maskedReqBody
        );

        long start = System.currentTimeMillis();
        Response response = ctx.next(req, res);
        long tookMs = System.currentTimeMillis() - start;

        String respBody = response.getBody() == null ? null : response.getBody().asPrettyString();
        String maskedRespBody = MaskingUtils.maskJsonFields(respBody, maskedJsonFields);

        log.info("=== HTTP RESPONSE ===\nStatus: {} ({} ms)\nHeaders:\n{}\nBody:\n{}\n",
                response.getStatusCode(),
                tookMs,
                response.getHeaders().toString(),
                maskedRespBody == null ? "<empty>" : maskedRespBody
        );

        return response;
    }
}
