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
        var builder = Proto.Credentials.newBuilder();
        if (value.getUserId() != null) builder.setId(value.getUserId());
        if (value.getPassword() != null) builder.setPassword(value.getPassword());
        return builder.build();
    }

    public static Credentials toJava(Proto.Credentials value) {
        return new Credentials(
                value.getId(),
                value.getPassword()
        );
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
        var builder = Proto.User.newBuilder();
        if (value.getFullName() != null) builder.setFullName(value.getFullName());
        if (value.getUsername() != null) builder.setUsername(value.getUsername());
        if (value.getPassword() != null) builder.setPassword(value.getPassword());
        if (value.getRole() != null) builder.setRole(toProto(value.getRole()));
        if (value.getEmailAddresses() != null) builder.addAllEmailAddresses(value.getEmailAddresses());
        if (value.getBirthDate() != null) builder.setBirthDate(toProto(value.getBirthDate()));
        return builder.build();
    }

    public static User toJava(Proto.User value) {
        return new User(
                value.hasBirthDate() ? value.getFullName() : null,
                value.hasUsername() ? value.getUsername() : null,
                value.hasPassword() ? value.getPassword() : null,
                value.hasBirthDate() ? toJava(value.getBirthDate()) : null,
                value.hasRole() ? toJava(value.getRole()) : null,
                value.getEmailAddressesList()
        );
    }
}
