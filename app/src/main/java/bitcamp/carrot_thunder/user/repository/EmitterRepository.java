package bitcamp.carrot_thunder.user.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {

  private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

  public SseEmitter get(Long userId) {
    return emitterMap.get(userId);
  }

  public void save(Long userId, SseEmitter emitter) {
    emitterMap.put(userId, emitter);
  }

  public void delete(Long userId) {
    emitterMap.remove(userId);
  }
}
