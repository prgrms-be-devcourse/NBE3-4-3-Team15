package com.project.backend.global.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * redis Controller
 * 단순히 redis가 구현 되는지 테스트하기 위해 생성
 *
 *  @author 이광석
 *  @since 25.02.26
 */
@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {
    private final RedisService redisService;

    /**
     * Redis 데이터 추가
     * @param key
     * @param value
     * @return ResponseEntity
     *
     *
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveData(@RequestParam(value = "key") String key,
                                           @RequestParam(value = "value") String value){
       System.out.println(key+value);
        redisService.saveData(key, value);
        return ResponseEntity.ok("데이터 저장 성공");
    }

    /**
     * Redis 데이터 검색
     * @param key
     * @return ResponseEntity
     */
    @GetMapping("/get")
    public ResponseEntity<String> getData(@RequestParam(value ="key") String key){
        String value = redisService.getData(key);
        return ResponseEntity.ok(value != null ? value:"데이터 없음");
    }

    /**
     * Redis 데이터 삭제
     * @param key
     * @return ResponseEntity
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteData(@RequestParam(value = "key") String key){
        redisService.deleteData(key);
        return ResponseEntity.ok("데이터 삭제 성공");
    }

}
