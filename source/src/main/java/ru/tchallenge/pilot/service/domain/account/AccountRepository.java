package ru.tchallenge.pilot.service.domain.account;

import org.bson.Document;

import ru.tchallenge.pilot.service.utility.data.GenericRepository;

public final class AccountRepository extends GenericRepository {

    public static final AccountRepository INSTANCE = new AccountRepository();

    private AccountRepository() {
        super("accounts");
    }

    public Document findByEmail(final String email) {
        return documents()
                .find()
                .filter(new Document("email", email))
                .first();
    }
}
