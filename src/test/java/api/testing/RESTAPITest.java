package api.testing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.thl.banking.Application;
import com.thl.banking.model.Account;
import com.thl.banking.model.Transaction;
import com.thl.banking.model.User;
import org.junit.BeforeClass;
import org.junit.Test;
import io.restassured.RestAssured;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.get;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

public class RESTAPITest {

    static Gson gson;

    @BeforeClass
    public static void init() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        configureHost();
        configurePort();
        configureBasePath();
        startServer();
    }

    private static void configureHost() {
        String baseHost = System.getProperty("server.host");
        if (baseHost == null) {
            baseHost = "http://localhost";
        }
        RestAssured.baseURI = baseHost;
    }

    private static void configurePort() {
        String port = System.getProperty("server.port");
        if (port == null) {
            RestAssured.port = Integer.parseInt("4567");
        } else {
            RestAssured.port = Integer.parseInt(port);
        }
    }

    private static void configureBasePath() {
        String basePath = System.getProperty("server.base");
        if (basePath == null) {
            basePath = "/";
        }
        RestAssured.basePath = basePath;
    }

    private static void startServer() {
        Application.main(new String[]{});
    }


    @Test
    public void InvokeHealthCheckTest() {
        given()
                .when().get("/healthcheck")
                .then().statusCode(200);
    }

    @Test
    public void createAccountTest() {
        User user = new User(UUID.randomUUID().toString(), "TestUser", "");
        given().body(createAccount(user))
                .when().post("/account")
                .then().statusCode(201);
    }


    @Test
    public void accountWhenUserIsInvalidTest() {
        given().body(createAccount(null))
                .when().post("/account")
                .then().statusCode(500);
    }

    @Test
    public void accountWhenUserNameIsInvalidTest() {
        User user = new User(UUID.randomUUID().toString(), "", "");
        given().body(createAccount(user))
                .when().post("/account")
                .then().statusCode(500);
    }


    @Test
    public void deleteAccountTest() {
        User user = new User(UUID.randomUUID().toString(), "TestUser", "");
        String accountId = given().body(createAccount(user))
                .when().post("/account")
                .then().extract().path("accountId");
        delete("/account/".concat(accountId))
                .then().statusCode(202);
    }

    @Test
    public void tryToDeleteInvalidAccountTest() {
        delete("/account/invalid")
                .then().body("message", equalTo("NOT_FOUND"))
                .statusCode(404);
    }

    @Test
    public void tryToDeleteEmptyAccountTest() {
        delete("/account").then().statusCode(404);
    }

    @Test
    public void getOneAccountTest() {
        User user = new User(UUID.randomUUID().toString(), "TestUser", "");
        String accountId = given().body(createAccount(user))
                .when().post("/account")
                .then().extract().path("accountId");

        get("/account/".concat(accountId)).then().body("accountId", equalTo(accountId));
    }

    @Test
    public void notGetOneAccountIfItDoesNotExistTest() {
        get("/account/123")
                .then().body("message", equalTo("NOT_FOUND"));
    }

    @Test
    public void getAllAccountsTest() {
        User user = new User(UUID.randomUUID().toString(), "TestUser", "");
        String accountId = given().body(createAccount(user))
                .when().post("/account")
                .then().extract().path("accountId");

        get("/account")
                .then().body(accountId.concat(".accountId"), equalTo(accountId)).statusCode(200);
    }

    @Test
    public void commitTransactionTest() {
        User user1 = new User(UUID.randomUUID().toString(), "TestUser", "");
        User user2 = new User(UUID.randomUUID().toString(), "TestUser2", "");
        String accountId1 = given().body(createAccount(user1))
                .when().post("/account")
                .then().extract().path("accountId");

        String accountId2 = given().body(createAccount(user2))
                .when().post("/account")
                .then().extract().path("accountId");

        JsonObject res = new JsonObject();
        res.addProperty("sender", accountId1);
        res.addProperty("receiver", accountId2);
        res.addProperty("amount", "1000");
        res.addProperty("currency", "INR");

        given().body(gson.toJson(res))
                .when().post("/transaction")
                .then()
                .statusCode(200);
    }


    @Test
    public void getOneTransactionTest() {
        User user1 = new User(UUID.randomUUID().toString(), "TestUser", "");
        User user2 = new User(UUID.randomUUID().toString(), "TestUser2", "");
        String accountId1 = given().body(createAccount(user1))
                .when().post("/account")
                .then().extract().path("accountId");

        String accountId2 = given().body(createAccount(user2))
                .when().post("/account")
                .then().extract().path("accountId");

        JsonObject res = new JsonObject();
        res.addProperty("sender", accountId1);
        res.addProperty("receiver", accountId2);
        res.addProperty("amount", "1000");
        res.addProperty("currency", "INR");

        String transactionId = given().body(gson.toJson(res))
                .when().post("/transaction")
                .then()
                .extract().path("id");

        get("/transaction/".concat(transactionId))
                .then()
                .body("id", equalTo(transactionId));
    }

    @Test
    public void transactionIfOneAccountMissingTest() {
        User user1 = new User(UUID.randomUUID().toString(), "TestUser", "");
        String accountId1 = given().body(createAccount(user1))
                .when().post("/account")
                .then().extract().path("accountId");

        JsonObject res = new JsonObject();
        res.addProperty("sender", accountId1);
        res.addProperty("receiver", "Acc2");
        res.addProperty("amount", "1000");
        res.addProperty("currency", "INR");

        given().body(gson.toJson(res))
                .when().post("/transaction")
                .then().body("message", equalTo("ACCOUNT_NOT_FOUND")).statusCode(404);

    }

    @Test
    public void transactionBadRequestTest() {
        User user1 = new User(UUID.randomUUID().toString(), "TestUser", "");
        String accountId1 = given().body(createAccount(user1))
                .when().post("/account")
                .then().extract().path("accountId");

        JsonObject res = new JsonObject();
        res.addProperty("sender", accountId1);
        res.addProperty("amount", "1000");
        res.addProperty("currency", "INR");

        given().body(gson.toJson(res))
                .when().post("/transaction")
                .then().body("message", equalTo("BAD_REQUEST")).statusCode(400);

    }

    @Test
    public void transactionInsufficientFundExceptionTest() {
        User user1 = new User(UUID.randomUUID().toString(), "TestUser", "");
        User user2 = new User(UUID.randomUUID().toString(), "TestUser2", "");
        String accountId1 = given().body(createAccount(user1))
                .when().post("/account")
                .then().extract().path("accountId");

        String accountId2 = given().body(createAccount(user2))
                .when().post("/account")
                .then().extract().path("accountId");

        JsonObject res = new JsonObject();
        res.addProperty("sender", accountId1);
        res.addProperty("receiver", accountId2);
        res.addProperty("amount", "11000");
        res.addProperty("currency", "INR");

        given().body(gson.toJson(res))
                .when().post("/transaction")
                .then().body("message", equalTo("INSUFFICIENT_FUND")).statusCode(406);

    }

    @Test
    public void tryToGetOneTransactionForInvalidIdTest() {
        get("/transaction/invalid")
                .then()
                .statusCode(404);
    }

    private Account createAccount(User user) {
        Account account = new Account(user, new BigDecimal(10000), LocalDateTime.now().toString(), LocalDateTime.now().toString(), "INR");
        account.setAccountId(UUID.randomUUID().toString());
        return account;
    }

    private Transaction createTransaction(Account from, Account to, BigDecimal amount, String currencyUnit) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setAmount(amount);
        transaction.setCurrencyUnit(currencyUnit);
        transaction.setCreatedTime(LocalDateTime.now().toString());
        return transaction;
    }
}
