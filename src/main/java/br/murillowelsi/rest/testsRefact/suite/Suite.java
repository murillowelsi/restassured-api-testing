package br.murillowelsi.rest.testsRefact.suite;

import br.murillowelsi.rest.core.BaseTest;
import br.murillowelsi.rest.testsRefact.AccountsTest;
import br.murillowelsi.rest.testsRefact.AuthTest;
import br.murillowelsi.rest.testsRefact.SaldoTest;
import br.murillowelsi.rest.testsRefact.TransactionsTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
        AccountsTest.class,
        TransactionsTest.class,
        SaldoTest.class,
        AuthTest.class
})
public class Suite extends BaseTest {
    @BeforeClass
    public static void login() {
        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "murillo.welsi@gmail.com");
        login.put("senha", "123456");

        String TOKEN = given()
                .log().all()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token");

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);

        RestAssured.get("/reset").then().statusCode(200);
    }
}
