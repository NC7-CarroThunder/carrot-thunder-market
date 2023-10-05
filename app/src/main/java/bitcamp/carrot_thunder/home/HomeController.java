package bitcamp.carrot_thunder.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/")
  public String home() throws Exception {
    //System.out.println("여기 들어옵니까?");
    return "/index.html";
  }
}
