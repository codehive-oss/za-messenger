package io.frghackers.messenger.server.db;

import de.mkammerer.argon2.Argon2Factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class Argon2 {
    public static de.mkammerer.argon2.Argon2 INSTANCE =
            Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
    public static Logger logger = LoggerFactory.getLogger(Argon2.class);

    public static String hash(String password) {
        Instant start = Instant.now();
        char[] passwordArray = password.toCharArray();

        String hash = INSTANCE.hash(22, 65536, 1, passwordArray);
        INSTANCE.wipeArray(passwordArray);
        logger.info(
                "Hashing took %sms"
                        .formatted(Instant.now().minusMillis(start.toEpochMilli()).toEpochMilli()));
        return hash;
    }
}
