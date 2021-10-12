package br.murillowelsi.rest.testsRefact;

import br.murillowelsi.rest.core.BaseTest;
import br.murillowelsi.rest.tests.Transaction;
import br.murillowelsi.utils.DataUtils;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SaldoTest extends BaseTest {

    @Test
    public void shouldCalculateAccountValues() {
        Integer CONTA_ID = getAccountIdByName("Conta para saldo");

        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
        ;
    }

    public Integer getAccountIdByName(String name) {
        return RestAssured.get("/contas?nome="+name).then().extract().path("id[0]");
    }
}
