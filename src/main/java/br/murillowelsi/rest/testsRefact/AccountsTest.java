package br.murillowelsi.rest.testsRefact;

import br.murillowelsi.rest.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class AccountsTest extends BaseTest {

    @Test
    public void shouldCreateAnAccountWithSuccess() {
        given()
                .body("{ \"nome\": \"Conta Inserida\" }")
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    public void shouldUpdateAnAccountWithSuccess() {
        Integer CONTA_ID = getAccountIdByName("Conta para alterar");

        given()
                .body("{ \"nome\": \"Conta alterada\" }")
                .pathParam("id", CONTA_ID)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", is("Conta alterada"))
        ;
    }

    @Test
    public void shouldNotCreateDuplicateAccountName() {
        given()
                .body("{ \"nome\": \"Conta mesmo nome\" }")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    public Integer getAccountIdByName(String name) {
        return RestAssured.get("/contas?nome="+name).then().extract().path("id[0]");
    }
}
