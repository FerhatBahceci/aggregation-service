package com.fedex.aggregation.service;

import com.fedex.aggregation.service.AggregationBase;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.restassured.specification.RequestSpecification;
import io.restassured.response.Response;

import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.*;
import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static io.restassured.RestAssured.*;

@SuppressWarnings("rawtypes")
public class ContractVerifierTest extends AggregationBase {

	@Test
	public void validate_aggregatorError() throws Exception {
		// given:
			RequestSpecification request = given();


		// when:
			Response response = given().spec(request)
					.queryParam("pricing","CN,NL")
					.queryParam("track","123456789,123456799")
					.queryParam("shipments","123456789,123456799")
					.get("/aggregation");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['pricing']").isNull();
			assertThatJson(parsedJson).field("['track']").isNull();
			assertThatJson(parsedJson).field("['shipments']").isNull();
	}

}
