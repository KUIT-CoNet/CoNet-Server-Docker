package com.kuit.conet.config;

import com.kuit.conet.utils.StringToPlanPeriodConverter;
import com.kuit.conet.utils.auth.BearerAuthInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private final MemberIdResolver memberIdResolver;
    private final ClientIpResolver clientIpResolver;
    private final RefreshTokenResolver refreshTokenResolver;
    private final BearerAuthInterceptor bearerAuthInterceptor;

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberIdResolver);
        resolvers.add(clientIpResolver);
        resolvers.add(refreshTokenResolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToPlanPeriodConverter());
    }

    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Interceptor 등록");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/auth/term");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/auth/regenerate-token");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/member");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/member/name");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/member/image");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/member/bookmark");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/{teamId}");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/{teamId}/members");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/create");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/join");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/leave");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/bookmark/delete");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/plan");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/plan/fixed");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/plan/{planId}");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/plan/update/fixed");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/plan/update/waiting");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/plan/available-time-slot");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/plan/{planId}/available-time-slot/my");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/home/plan/month");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/home/plan/day");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/home/plan/waiting");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/notice");
    }
}