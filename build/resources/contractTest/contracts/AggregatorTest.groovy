package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return full AggregatedResponse"
    request {
        method GET()
        url("/aggregation") {
            queryParameters {
                parameter("pricing", "CN,NL")
                parameter("track", "123456789,123456799")
                parameter("shipments", "123456789,123456799")
            }
        }
    }
    response {
        status 200
        body(file("AggregatorResponse.json"))
    }
}
