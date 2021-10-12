package br.murillowelsi.rest.tests;

import br.murillowelsi.rest.core.BaseTest;
import br.murillowelsi.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

    private static String CONTA_NAME = "Conta " + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

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
    }


    @Test
    public void t11_shouldNotAccessAPIWithoutToken() {
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");

        given()
        .when()
                .get("/contas")
        .then()
                .statusCode(401)
        ;
    }

    @Test
    public void t02_shouldCreateAAccountWithSuccess() {
        CONTA_ID =  given()
                .body("{ \"nome\": \""+CONTA_NAME+"\" }")
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    public void t03_shouldUpdateAAccountWithSuccess() {
        given()
                .body("{ \"nome\": \""+CONTA_NAME+ " alterada\" }")
                .pathParam("id", CONTA_ID)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", is(""+CONTA_NAME+" alterada"))
        ;
    }

    @Test
    public void t04_shouldNotCreateDuplicateAccountName() {
        given()
                .body("{ \"nome\": \""+CONTA_NAME+" alterada\" }")
        .when()
                .post("/contas")
        .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void t05_shouldValidateRequiredFields() {
        given()
                .body("{}")
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ))
        ;
    }

    @Test
    public void t06_shouldCreateANewTransaction() {
        Transaction transaction = getValidTransaction();

        MOV_ID = given()
                .body(transaction)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    public void t07_shouldNotCreateANewTransactionWithFutureDate() {
        Transaction transaction = getValidTransaction();
        transaction.setData_transacao(DataUtils.getFutureDate(2));

        given()
                .body(transaction)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"))
        ;
    }

    @Test
    public void t08_shouldNotRemoveAccountWithTransaction() {
        Transaction transaction = getValidTransaction();

        given()
                .pathParam("id", CONTA_ID)
        .when()
                .delete("/contas/{id}")
        .then()
                .statusCode(500)
                .body("constraint", is("transacoes_conta_id_foreign"))
        ;
    }

    @Test
    public void t09_shouldCalculateAccountValues() {
        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.50"))
        ;
    }

    @Test
    public void t10_shouldRemoveTransaction() {
        given()
                .pathParam("id", MOV_ID)
        .when()
                .delete("/transacoes/{id}")
        .then()
                .statusCode(204)
        ;
    }

    private Transaction getValidTransaction() {
        Transaction transaction = new Transaction();
        transaction.setConta_id(CONTA_ID);
        transaction.setData_transacao(DataUtils.getFutureDate(-1));
        transaction.setData_pagamento(DataUtils.getFutureDate(5));
        transaction.setDescricao("Teste Movimentacao");
        transaction.setEnvolvido("Dunha");
        transaction.setValor(100.50f);
        transaction.setStatus(true);
        transaction.setTipo("REC");

        return transaction;
    }
}

