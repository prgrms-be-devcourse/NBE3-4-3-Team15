//package com.project.backend.global.SSE;
//
//import com.project.backend.global.authority.CustomUserDetails;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/sse")
//public class SseController {
//    private final SSeEmitterService sSeEmitterService;
//
//
//
//    @PostMapping("/broadcast")
//    public void broadcast(@AuthenticationPrincipal CustomUserDetails userDetail,
//                          @RequestBody EventPayLoad){
//        sSeEmitterService.broadcast()
//    }
//
//}
