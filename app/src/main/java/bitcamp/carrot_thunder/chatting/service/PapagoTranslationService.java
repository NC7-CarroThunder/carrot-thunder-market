package bitcamp.carrot_thunder.chatting.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PapagoTranslationService {

  @Value("${papago.client.id}")
  private String clientId;

  @Value("${papago.client.secret}")
  private String clientSecret;

  public String detectAndTranslate(String inputText, String targetLang) {
    try {
      // Step 1: 입력 텍스트의 언어 감지
      String detectedLang = detectLanguage(inputText);

      if ("und".equals(detectedLang) || detectedLang.equals(targetLang) || "unk".equals(
          detectedLang)) {
        return "";
      }

      // Step 2: 언어 감지 결과를 바탕으로 번역 수행
      String translatedText = translateText(inputText, detectedLang, targetLang);

      System.out.println("입력 텍스트: " + inputText);
      System.out.println("감지된 언어: " + detectedLang);
      System.out.println("번역 결과: " + translatedText);

      return translatedText;
    } catch (Exception e) {
      e.printStackTrace();
      return "번역에 실패했습니다.";
    }
  }

  public String detectLanguage(String inputText) {
    try {
      String query = URLEncoder.encode(inputText, "UTF-8");
      String apiURL = "https://naveropenapi.apigw.ntruss.com/langs/v1/dect";
      URL url = new URL(apiURL);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
      con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
      con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

      // POST 파라미터 설정
      String postParams = "query=" + query;

      con.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes(postParams);
      wr.flush();
      wr.close();

      int responseCode = con.getResponseCode();
      if (responseCode == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = br.readLine()) != null) {
          response.append(inputLine);
        }
        br.close();

        // 감지된 언어 정보를 추출
        String jsonResponse = response.toString();
        String detectedLang = extractDetectedLang(jsonResponse);

        return detectedLang;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "und";  // 언어 감지 실패
  }

  private String extractDetectedLang(String jsonResponse) {
    try {
      JSONObject jsonObject = new JSONObject(jsonResponse);
      return jsonObject.getString("langCode");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private String translateText(String inputText, String sourceLang, String targetLang) {
    try {
      String query = URLEncoder.encode(inputText, "UTF-8");
      String apiURL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";
      URL url = new URL(apiURL);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
      con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
      con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

      // POST 파라미터 설정
      String postParams = "source=" + sourceLang + "&target=" + targetLang + "&text=" + query;

      con.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes(postParams);
      wr.flush();
      wr.close();

      int responseCode = con.getResponseCode();
      if (responseCode == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = br.readLine()) != null) {
          response.append(inputLine);
        }
        br.close();

        // 번역 결과를 추출
        String jsonResponse = response.toString();
        String translatedText = extractTranslatedText(jsonResponse);

        // 번역된 텍스트가 null이거나 빈 문자열인 경우 원본 텍스트 반환
        if (translatedText == null || translatedText.trim().isEmpty()) {
          return inputText;
        }

        return translatedText;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("번역에 실패했습니다.");
    return inputText;  // 번역 실패시 원본 텍스트 반환
  }

  private String extractTranslatedText(String jsonResponse) {
    try {
      JSONObject jsonObject = new JSONObject(jsonResponse);
      return jsonObject.getJSONObject("message").getJSONObject("result")
          .getString("translatedText");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
