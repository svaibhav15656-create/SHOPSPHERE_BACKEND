package com.shopsphere.api_gateway.filter;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RateLimitFilter implements HandlerInterceptor{
    private final ConcurrentHashMap<String ,Bucket> buckets = new ConcurrentHashMap<>();
    private Bucket createBucket(){
        return Bucket.builder()
        .addLimit(Bandwidth.builder()
        .capacity(10)
        .refillIntervally(10, Duration.ofMinutes(1))
        .build()
)
  .build();
    }
    @Override
    public boolean preHandle(HttpServletRequest request , HttpServletResponse response , Object handler)throws Exception{
        String ip_address = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip_address, k->createBucket());
        boolean consume = bucket.tryConsume(1);
        if(consume){
            return true;
        }
        response.setStatus(429);
        return false;

    }
}
