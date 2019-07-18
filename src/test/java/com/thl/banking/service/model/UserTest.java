package com.thl.banking.service.model;


import com.thl.banking.model.Account;
import com.thl.banking.model.Transaction;
import com.thl.banking.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    @Test
    public void shouldCheckEquals() {
        User user = createUser();
        User user2 = createUser();
        assertThat(user.equals(user2)).isFalse();
        assertThat(user == user2).isFalse();
        user2.setId(user.getId());
        assertThat(user.equals(user2)).isTrue();
        assertThat(user == user2).isFalse();
        assertThat(user.hashCode() == user2.hashCode()).isTrue();

    }


    private User createUser() {
        return new User(UUID.randomUUID().toString(), "TestUser", "Bangalore");

    }
}
