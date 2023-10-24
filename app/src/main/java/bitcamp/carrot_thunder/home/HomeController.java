package bitcamp.carrot_thunder.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

  @GetMapping("/test")
  public String home() throws Exception {
    return "Server says Hello!";
  }
}
