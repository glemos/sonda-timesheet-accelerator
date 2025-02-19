package br.com.sondait.timesheet.controllers;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sondait.timesheet.config.SeleniumHelper;

@RestController
public class RegistrationController {

	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity register(@RequestParam("userId") String userId, @RequestParam("password") String password,
			@RequestParam("action") String action) {

		Map<String, String> retorno = new HashMap<String, String>();
		SeleniumHelper helper = new SeleniumHelper();

		try {

			if (userId == null || userId.isEmpty()) {
				throw new RuntimeException("invalid userId");
			}

			if (password == null || password.isEmpty()) {
				throw new RuntimeException("invalid password");
			}

			if (action == null || action.isEmpty()) {
				throw new RuntimeException("invalid action");
			}

			if (!"ponto".equals(action) && !"intervalo".equals(action)) {
				throw new RuntimeException("invalid action");
			}

			WebDriver driver = helper.getDriver();

			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			String passwordEncoded = URLEncoder.encode(password);

			StringBuilder url = new StringBuilder();
			url.append("https://");
			url.append(userId);
			url.append(":");
			url.append(passwordEncoded);
			url.append("@pontoeletronico.sondait.com.br/Point/Index");

			driver.get(url.toString());

			// System.out.println(driver.getPageSource());

			driver.findElement(By.id("createNewPoint")).click();

			if ("ponto".equals(action)) {
				driver.findElement(By.id("pointImage")).click();
			}

			if ("intervalo".equals(action)) {
				driver.findElement(By.id("mealImage")).click();
			}

			driver.findElement(By.id("Save")).click();

			// driver.wait(5000);

			retorno.put("success", "true");

			driver.close();
			// retorno.put("code", driver.getPageSource());

			// System.out.println(driver.getPageSource());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			retorno.put("success", "false");
			if (e.getMessage() != null) {
				retorno.put("msg", e.getMessage());
			}
			return new ResponseEntity<Map<String, String>>(retorno, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		// helper.navigateTo("www.google.com");
		return new ResponseEntity<Map<String, String>>(retorno, HttpStatus.OK);
	}

}
