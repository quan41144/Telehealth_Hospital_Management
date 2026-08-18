package ra.apigateway.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import ra.apigateway.security.jwt.JwtUtil;
import ra.apigateway.security.route.RouteValidator;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private final RouteValidator routeValidator;
    private final JwtUtil jwtUtil;
    private final ReactiveStringRedisTemplate redisTemplate;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (routeValidator.isSecured.test(request)) {
            if (!request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                log.warn("*************Không tìm thấy Header Authorization!**************");
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("*************Định dạng Token không đúng!****************");
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            return redisTemplate.hasKey("BL_" + token)
                    .flatMap(isBlacklisted -> {
                        if (Boolean.TRUE.equals(isBlacklisted)) {
                            log.warn("***************Token đã bị vô hiệu hóa****************");
                            return onError(exchange, HttpStatus.UNAUTHORIZED);
                        }
                        try {
                            jwtUtil.validateToken(token);
                            String userId = jwtUtil.getUserIdFromToken(token);
                            String roles = jwtUtil.getRolesFromToken(token);

                            ServerHttpRequest mutatedRequest = exchange.getRequest()
                                    .mutate()
                                    .header("X-User-Id", userId)
                                    .header("X-User-Roles", roles)
                                    .build();
                            return chain.filter(exchange.mutate().request(mutatedRequest).build());
                        }
                        catch (Exception e) {
                            log.error("Xác thực Token thất bại: {}", e.getMessage());
                            return onError(exchange, HttpStatus.UNAUTHORIZED);
                        }
                    });

        }
        return chain.filter(exchange);
    }
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
