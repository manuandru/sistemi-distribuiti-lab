package it.unibo.ds.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public class GsonUtils {
    public static Gson createGson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .serializeNulls()
                .registerTypeAdapter(User.class, new UserSerializer())
                .registerTypeAdapter(User.class, new UserDeserializer())
                .registerTypeAdapter(Credentials.class, new CredentialsSerializer())
                .registerTypeAdapter(Credentials.class, new CredentialsDeserializer())
                .registerTypeAdapter(Token.class, new TokenSerializer())
                .registerTypeAdapter(Token.class, new TokenDeserializer())
                .registerTypeAdapter(Role.class, new RoleSerializer())
                .registerTypeAdapter(Role.class, new RoleDeserializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();
    }
}
