package com.vnpt_cms.learn_spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RedisController {
    private final RedisTemplate<Object, Object> redisTemplate;

    @PostMapping("/opsForValue")
    public void opsForValue() {
        Boolean deleted = redisTemplate.delete("user");
        redisTemplate.opsForValue().set("user", "Gemini");
        String value = redisTemplate.opsForValue().get("user").toString();

        System.out.println(value);

        redisTemplate.opsForValue().set("otp", "9999", 50, TimeUnit.SECONDS);
    }

    @PostMapping("/opsForList")
    public void opsForList() {
        Boolean deleted = redisTemplate.delete("notifications");
        redisTemplate.opsForList().rightPush("notifications", "Thông báo 1");
        redisTemplate.opsForList().rightPush("notifications", "Thông báo 2");

        String firstTask = (String) redisTemplate.opsForList().leftPop("notifications");

        System.out.println(firstTask);

        redisTemplate.opsForList().set("notifications", 0, "Thông báo đã chỉnh sửa");

        String firstTask1 = (String) redisTemplate.opsForList().leftPop("notifications");
        System.out.println(firstTask1);

    }

    @PostMapping("/opsForHash")
    public void demoOpsForHash() {
        String key = "user:1001";

        // 1. THÊM TỪNG TRƯỜNG (HSET)
        // Cú pháp: put(key, hashKey, value)
        redisTemplate.opsForHash().put(key, "id", "1001");
        redisTemplate.opsForHash().put(key, "name", "Nguyen Van A");
        redisTemplate.opsForHash().put(key, "email", "vana@example.com");

        // 2. THÊM NHIỀU TRƯỜNG CÙNG LÚC (HMSET)
        // Dùng Map để đẩy một lúc nhiều field vào Redis
        Map<String, Object> fields = new HashMap<>();
        fields.put("age", 25);
        fields.put("role", "ADMIN");
        redisTemplate.opsForHash().putAll(key, fields);

        System.out.println("--- TRUY VẤN DỮ LIỆU ---");

        // 3. LẤY MỘT TRƯỜNG CỤ THỂ (HGET)
        String name = (String) redisTemplate.opsForHash().get(key, "name");
        System.out.println("Tên người dùng: " + name);

        // 4. KIỂM TRA TRƯỜNG CÓ TỒN TẠI KHÔNG (HEXISTS)
        Boolean hasEmail = redisTemplate.opsForHash().hasKey(key, "email");
        System.out.println("Có email không? " + hasEmail);

        // 5. LẤY TẤT CẢ CÁC KEY (HKEYS) VÀ VALUE (HVALS)
        Set<Object> allFields = redisTemplate.opsForHash().keys(key);
        List<Object> allValues = redisTemplate.opsForHash().values(key);
        System.out.println("Các thuộc tính đang có: " + allFields);
        System.out.println("Giá trị tương ứng: " + allValues);

        // 6. LẤY TOÀN BỘ OBJECT (HGETALL)
        // Trả về một Map chứa tất cả field và value
        Map<Object, Object> userFullInfo = redisTemplate.opsForHash().entries(key);
        System.out.println("Toàn bộ thông tin User: " + userFullInfo);

        System.out.println("\n--- THAO TÁC CẬP NHẬT ---");

        // 7. TĂNG GIÁ TRỊ SỐ (HINCRBY)
        // Tăng tuổi thêm 1
        redisTemplate.opsForHash().increment(key, "age", 1);

        // 8. THÊM NẾU CHƯA CÓ (HSETNX)
        // Chỉ thêm nếu field "status" chưa tồn tại
        redisTemplate.opsForHash().putIfAbsent(key, "status", "ACTIVE");

        // 9. XÓA TRƯỜNG CỤ THỂ (HDEL)
        redisTemplate.opsForHash().delete(key, "role");

        // 10. LẤY ĐỘ DÀI HASH (HLEN)
        Long size = redisTemplate.opsForHash().size(key);
        System.out.println("Số lượng thuộc tính còn lại: " + size);

        Map<Object, Object> userFullInfoModify = redisTemplate.opsForHash().entries(key);
        System.out.println("Toàn bộ thông tin User After Modify: " + userFullInfoModify);
    }

    @PostMapping("/opsForSet")
    public void demoOpsForSet() {
        String key1 = "languages:backend";
        String key2 = "languages:frontend";

        // 1. THÊM PHẦN TỬ (SADD)
        redisTemplate.opsForSet().add(key1, "java", "spring", "redis", "java", "python", "nodejs");
        redisTemplate.opsForSet().add(key2, "javascript", "typescript", "nodejs", "html", "css");

        System.out.println("--- THAO TÁC CƠ BẢN ---");

        // 2. LẤY KÍCH THƯỚC (SCARD)
        System.out.println("Số lượng phần tử trong key1: " + redisTemplate.opsForSet().size(key1));

        // 3. KIỂM TRA TỒN TẠI (SISMEMBER)
        System.out.println("Có 'java' trong key1 không? " + redisTemplate.opsForSet().isMember(key1, "java"));

        // 4. LẤY TẤT CẢ PHẦN TỬ (SMEMBERS)
        System.out.println("Tất cả phần tử trong key1: " + redisTemplate.opsForSet().members(key1));

        // 5. LẤY NGẪU NHIÊN 1 PHẦN TỬ (SRANDMEMBER) - Không xóa khỏi Set
        System.out.println("Lấy ngẫu nhiên 1 cái từ key1: " + redisTemplate.opsForSet().randomMember(key1));

        // 6. LẤY VÀ XÓA NGẪU NHIÊN (SPOP)
        Object poppedValue = redisTemplate.opsForSet().pop(key1);
        System.out.println("Đã lấy và xóa ngẫu nhiên: " + poppedValue);

        System.out.println("\n--- THAO TÁC TẬP HỢP (MULTI-SET) ---");

        // 7. PHÉP GIAO (SINTER) - Những gì cả 2 bên đều có
        // Kết quả mong đợi: ["nodejs"]
        Set<Object> intersect = redisTemplate.opsForSet().intersect(key1, key2);
        System.out.println("Giao giữa key1 và key2 (Bạn chung/Skill chung): " + intersect);

        // 8. PHÉP HỢP (SUNION) - Gộp tất cả, loại bỏ trùng
        Set<Object> union = redisTemplate.opsForSet().union(key1, key2);
        System.out.println("Hợp của key1 và key2 (Tất cả skill): " + union);

        // 9. PHÉP HIỆU (SDIFF) - Có ở key1 nhưng KHÔNG có ở key2
        Set<Object> diff = redisTemplate.opsForSet().difference(key1, key2);
        System.out.println("Phần tử chỉ có ở key1 (Backend only): " + diff);

        // 10. DI CHUYỂN PHẦN TỬ (SMOVE)
        // Chuyển "python" từ key1 sang key2
        redisTemplate.opsForSet().move(key1, "python", key2);
        System.out.println("Đã chuyển 'python' sang key2.");

        // 11. XÓA PHẦN TỬ CỤ THỂ (SREM)
        redisTemplate.opsForSet().remove(key2, "css", "html");
        System.out.println("Đã xóa css và html khỏi key2.");
    }

    @PostMapping("/opsForZSet")
    public void demoOpsForZSet() {
        String key = "game:leaderboard";

        // 1. THÊM PHẦN TỬ (ZADD)
        redisTemplate.opsForZSet().add(key, "PlayerA", 100.0);
        redisTemplate.opsForZSet().add(key, "PlayerB", 250.0);
        redisTemplate.opsForZSet().add(key, "PlayerC", 150.0);
        // Nếu thêm trùng "PlayerA" với score mới, nó sẽ cập nhật score của PlayerA
        redisTemplate.opsForZSet().add(key, "PlayerA", 120.0);

        System.out.println("--- TRUY VẤN CƠ BẢN ---");

        // 2. LẤY SCORE CỦA MỘT PHẦN TỬ (ZSCORE)
        Double score = redisTemplate.opsForZSet().score(key, "PlayerB");
        System.out.println("Điểm của PlayerB: " + score);

        // 3. TĂNG ĐIỂM CHO PHẦN TỬ (ZINCRBY)
        // Tăng thêm 50 điểm cho PlayerA
        redisTemplate.opsForZSet().incrementScore(key, "PlayerA", 50.0);

        // 4. LẤY THỨ HẠNG (ZRANK / ZREVRANK)
        // rank: Thứ hạng từ thấp đến cao (bắt đầu từ 0)
        Long rank = redisTemplate.opsForZSet().rank(key, "PlayerA");
        // reverseRank: Thứ hạng từ cao đến thấp (Top 1 sẽ là 0)
        Long topRank = redisTemplate.opsForZSet().reverseRank(key, "PlayerB");
        System.out.println("Thứ hạng (từ thấp): " + rank + ", Thứ hạng (Top): " + topRank);

        System.out.println("\n--- LẤY DANH SÁCH THEO THỨ TỰ ---");

        // 5. LẤY CÁC PHẦN TỬ TRONG KHOẢNG (ZRANGE / ZREVRANGE)
        // Lấy Top 3 người cao điểm nhất (từ index 0 đến 2)
        Set<Object> top3 = redisTemplate.opsForZSet().reverseRange(key, 0, 2);
        System.out.println("Top 3 người chơi: " + top3);

        // 6. LẤY KÈM CẢ SCORE (ZRANGE ... WITHSCORES)
        // Trả về một Set các TypedTuple (chứa cả value và score)
        Set<ZSetOperations.TypedTuple<Object>> top3WithScores =
                redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 2);

        top3WithScores.forEach(tuple ->
                System.out.println(tuple.getValue() + " có điểm: " + tuple.getScore())
        );

        System.out.println("\n--- LỌC VÀ XÓA ---");

        // 7. ĐẾM SỐ PHẦN TỬ TRONG KHOẢNG ĐIỂM (ZCOUNT)
        // Đếm xem có bao nhiêu người từ 100 đến 200 điểm
        Long count = redisTemplate.opsForZSet().count(key, 100.0, 200.0);
        System.out.println("Số người có điểm từ 100-200: " + count);

        // 8. XÓA PHẦN TỬ (ZREM)
        redisTemplate.opsForZSet().remove(key, "PlayerC");

        // 9. XÓA THEO KHOẢNG HẠNG (ZREMRANGEBYRANK)
        // Xóa những người bét bảng (từ vị trí 0 đến 0 - người thấp điểm nhất)
        redisTemplate.opsForZSet().removeRange(key, 0, 0);
    }

}
