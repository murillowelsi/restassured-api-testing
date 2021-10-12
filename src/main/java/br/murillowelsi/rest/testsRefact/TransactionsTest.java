package br.murillowelsi.rest.testsRefact;

import br.murillowelsi.rest.core.BaseTest;
import br.murillowelsi.rest.tests.Transaction;
import br.murillowelsi.utils.DataUtils;
import io.restassured.RestAssured;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TransactionsTest extends BaseTest {

    @Test
    public void shouldCreateANewTransaction() {
        Transaction transaction = getValidTransaction();

        given()
                .body(transaction)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
        ;
    }

    @Test
    public void shouldValidateRequiredFields() {
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
    public void shouldNotCreateANewTransactionWithFutureDate() {
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
    public void shouldNotRemoveAccountWithTransaction() {
        Integer CONTA_ID = getAccountIdByName("Conta com movimentacao");

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
    public void shouldRemoveTransaction() {
        Integer MOV_ID = getIdTransactionByDescr("Movimentacao para exclusao");
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
        transaction.setConta_id(getAccountIdByName("Conta para movimentacoes"));
        transaction.setData_transacao(DataUtils.getFutureDate(-1));
        transaction.setData_pagamento(DataUtils.getFutureDate(5));
        transaction.setDescricao("Teste Movimentacao");
        transaction.setEnvolvido("Dunha");
        transaction.setValor(100.50f);
        transaction.setStatus(true);
        transaction.setTipo("REC");

        return transaction;
    }

    public Integer getAccountIdByName(String name) {
        return RestAssured.get("/contas?nome="+name).then().extract().path("id[0]");
    }

    public Integer getIdTransactionByDescr(String desc) {
        return RestAssured.get("/transacoes?descricao="+desc).then().extract().path("id[0]");
    }
}
