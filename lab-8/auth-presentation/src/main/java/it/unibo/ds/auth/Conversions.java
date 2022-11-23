package it.unibo.ds.auth;

import com.google.protobuf.Timestamp;
import it.unibo.ds.auth.grpc.Proto;

import java.time.LocalDate;

public class Conversions {
    public static Proto.Role toProto(Role value) {
        return Proto.Role.forNumber(value.ordinal());
    }

    public static Role toJava(Proto.Role role) {
        return Role.values()[role.ordinal()];
    }

    public static Proto.Token toProto(Token value) {
        return Proto.Token.newBuilder()
                .setUsername(value.getUsername())
                .setRole(toProto(value.getRole()))
                .build();
    }

    public static Token toJava(Proto.Token value) {
        return new Token(value.getUsername(), toJava(value.getRole()));
    }

    public static Proto.Credentials toProto(Credentials value) {
        return Proto.Credentials.newBuilder()
                .setId(value.getUserId())
                .setPassword(value.getPassword())
                .build();
    }

    public static Credentials toJava(Proto.Credentials value) {
        return new Credentials(value.getId(), value.getPassword());
    }

    private static final long SECONDS_PER_DAY = 60 * 60 * 24;

    public static LocalDate toJava(Timestamp value) {
        return LocalDate.ofEpochDay(value.getSeconds());
    }

    public static Timestamp toProto(LocalDate value) {
        return Timestamp.newBuilder()
                .setSeconds(value.toEpochDay()  * SECONDS_PER_DAY)
                .build();
    }

    public static Proto.User toProto(User value) {
        return Proto.User.newBuilder()
                .setFullName(value.getFullName())
                .setUsername(value.getUsername())
                .setPassword(value.getPassword())
                .setRole(toProto(value.getRole()))
                .addAllEmailAddresses(value.getEmailAddresses())
                .setBirthDate(toProto(value.getBirthDate()))
                .build();
    }

    public static User toJava(Proto.User value) {
        return new User(
                value.getFullName(),
                value.getUsername(),
                value.getPassword(),
                toJava(value.getBirthDate()),
                toJava(value.getRole()),
                value.getEmailAddressesList()
        );
    }
}
