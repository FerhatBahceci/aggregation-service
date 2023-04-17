package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return even when number input is even"
    request {
        method GET()
        url("/aggregation") {
            queryParameters {
                parameter("pricing", "SE,GB,ES")
                parameter("track", "123456789,123456799")
                parameter("shipments", "123456789,123456799")
            }
        }
    }
    response {
        status 200
    }
}