package com.thl.banking.repository.inmemory;

import com.thl.banking.exception.AccountAlreadyExistsException;
import com.thl.banking.exception.CurrentcyMismatchException;
import com.thl.banking.exception.InvalidAccountException;
import com.thl.banking.model.Account;
import com.thl.banking.model.User;
import com.thl.banking.repository.AccountDAO;
import com.thl.banking.validator.AccountValidation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryAccountDAOImplTest {

    private AccountDAO accountRepository;

    @Mock
    private AccountValidation accountValidation;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        accountRepository = new InMemoryAccountDAOImpl(accountValidation);
        accountRepository.clear();
    }

    @Test
    public void emptyResultWhenAccountDoesNotExistTest() {
        String invalidNumber = "invalidNumber";
        Optional<Account> optional = accountRepository.getAccountByAccountNumber(invalidNumber);
        assertThat(optional.isPresent()).isFalse();
    }

    @Test
    public void accountWhenItExistsTest() throws Exception {
        Account account = createAccount();
        String accountNumber = account.getAccountId();
        accountRepository.create(account);
        Optional<Account> optional = accountRepository.getAccountByAccountNumber(accountNumber);
        assertThat(optional.isPresent()).isTrue();
    }

    @Test
    public void getAllAccountsTest() throws Exception {
        accountRepository.create(createAccount());
        accountRepository.create(createAccount());
        Map<String, Account> accounts = accountRepository.getAllAccounts();
        assertThat(accounts.size()).isEqualTo(2);
    }

    @Test
    public void createNewAccountTest() throws Exception {
        Account account = createAccount();
        when(accountValidation.validate(account)).thenReturn(null);
        accountRepository.create(account);
        Account createdAccount = accountRepository.getAccountByAccountNumber(account.getAccountId()).get();
        assertThat(accountRepository.getAllAccounts().isEmpty()).isFalse();
        assertThat(createdAccount).isEqualTo(account);
        assertThat(createdAccount.getUser()).isEqualTo(account.getUser());
        assertThat(createdAccount.getUser().getId()).isEqualTo(account.getUser().getId());
        assertThat(createdAccount.getUser().getName()).isEqualTo(account.getUser().getName());
        assertThat(createdAccount.getUser().getAddress()).isEqualTo(account.getUser().getAddress());
        assertThat(createdAccount.getAccountId()).isEqualTo(account.getAccountId());
        assertThat(createdAccount.getBalance()).isEqualTo(account.getBalance());
    }

    @Test
    public void createNewAccountWithNumberWhichAlreadyExistsTest() throws Exception {
        Account account = createAccount();
        accountRepository.create(account);
        expectedException.expect(AccountAlreadyExistsException.class);
        expectedException.expectMessage(new AccountAlreadyExistsException(account.getAccountId()).getMessage());
        accountRepository.create(account);
    }

    @Test
    public void createAccountIfErrorOccurredTest() throws Exception {
        Account account = createAccount();
        when(accountValidation.validate(account)).thenReturn(new InvalidAccountException("EMPTY_ACCOUNT_ID"));
        expectedException.expect(InvalidAccountException.class);
        accountRepository.create(account);
    }

    @Test
    public void updateAccountTest() throws Exception {
        Account account = createAccount();
        Account account2 = createAccount();
        account2.setAccountId(account.getAccountId());
        accountRepository.create(account);
        accountRepository.updateByAccountNumber(account.getAccountId(), account2);
        assertThat(accountRepository.getAccountByAccountNumber(account.getAccountId()).get().getAccountId())
                .isEqualTo(account2.getAccountId());
    }


    @Test
    public void updateAccountIfErrorOccurredTest() throws Exception {
        Account account = createAccount();
        when(accountValidation.validate(account)).thenReturn(new InvalidAccountException("EMPTY_ACCOUNT_ID"));
        expectedException.expect(InvalidAccountException.class);
        accountRepository.create(account);
        accountRepository.updateByAccountNumber(account.getAccountId(), account);
    }


    @Test(expected = InvalidAccountException.class)
    public void updateAccountIfAccountDoesntExistTest() throws Exception {
        Account account = createAccount();
        accountRepository.updateByAccountNumber(account.getAccountId(), account);
    }

    @Test
    public void withdrawMoneyTest() throws Exception {
        Account account = createAccount();
        accountRepository.create(account);
        accountRepository.withdrawMoney(account, new BigDecimal(1000), "INR");
        assertThat(accountRepository.getAccountByAccountNumber(account.getAccountId()).get().getBalance())
                .isEqualTo(new BigDecimal(9000));
    }

    @Test(expected = InvalidAccountException.class)
    public void withdrawMoneyIfAccountDoesNotExistTest() {
        Account account = createAccount();
        accountRepository.withdrawMoney(account, new BigDecimal(1000), "INR");
    }


    @Test(expected = CurrentcyMismatchException.class)
    public void withdrawMoneyIfDiffCurrencyTest() throws Exception {
        Account account = createAccount();
        accountRepository.create(account);
        accountRepository.withdrawMoney(account, new BigDecimal(1000), "USD");
    }


    @Test
    public void depositeMoneyTest() throws Exception {
        Account account = createAccount();
        accountRepository.create(account);
        accountRepository.depositMoney(account, new BigDecimal(1000), "INR");
        assertThat(accountRepository.getAccountByAccountNumber(account.getAccountId()).get().getBalance())
                .isEqualTo(new BigDecimal(11000));
    }

    @Test(expected = InvalidAccountException.class)
    public void depositeMoneyAccountDoesNotExistTest() {
        Account account = createAccount();
        accountRepository.depositMoney(account, new BigDecimal(1000), "INR");
    }

    @Test(expected = CurrentcyMismatchException.class)
    public void depositeMoneyIfErrorOccurredTest() throws Exception {
        Account account = createAccount();
        accountRepository.create(account);
        accountRepository.depositMoney(account, new BigDecimal(1000), "USD");
    }

    @Test
    public void deleteAccountTest() throws Exception {
        Account account = createAccount();
        accountRepository.create(account);
        accountRepository.deleteAccount(account.getAccountId());
        assertThat(accountRepository.getAccountByAccountNumber(account.getAccountId()).isPresent()).isFalse();
    }

    @Test
    public void deleteAccountIfItDoesNotExistTest() {
        String dummy = "dummy";
        expectedException.expect(InvalidAccountException.class);
        accountRepository.deleteAccount(dummy);
    }

    @Test
    public void clearAccountsTest() throws Exception {
        accountRepository.create(createAccount());
        accountRepository.create(createAccount());
        assertThat(accountRepository.getAllAccounts().size()).isEqualTo(2);
        accountRepository.clear();
        assertThat(accountRepository.getAllAccounts().isEmpty()).isTrue();
    }

    private Account createAccount() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setAddress("");
        user.setName("TestUser");
        Account account = new Account(user,new BigDecimal(10000),LocalDateTime.now(),LocalDateTime.now(),"INR");
        account.setAccountId(UUID.randomUUID().toString());
        return account;
    }

}
