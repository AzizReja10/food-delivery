package com.fooddelivery.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GatewayConfig {

    @Value("${app.user-service.url}")
    private String userServiceUrl;

    @Value("${app.restaurant-service.url}")
    private String restaurantServiceUrl;

    @Value("${app.order-service.url}")
    private String orderServiceUrl;

    @Value("${app.delivery-service.url}")
    private String deliveryServiceUrl;

    @Value("${app.notification-service.url}")
    private String notificationServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user-service")
                .route(RequestPredicates.path("/auth/**")
                                .or(RequestPredicates.path("/users/**")),
                        HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(userServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> restaurantServiceRoute() {
        return GatewayRouterFunctions.route("restaurant-service")
                .route(RequestPredicates.path("/restaurants/**"),
                        HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(restaurantServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route("order-service")
                .route(RequestPredicates.path("/orders/**"),
                        HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(orderServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> deliveryServiceRoute() {
        return GatewayRouterFunctions.route("delivery-service")
                .route(RequestPredicates.path("/deliveries/**"),
                        HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(deliveryServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceRoute() {
        return GatewayRouterFunctions.route("notification-service")
                .route(RequestPredicates.path("/notifications/**"),
                        HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(notificationServiceUrl))
                .build();
    }
}