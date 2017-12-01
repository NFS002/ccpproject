package net.Json;

import com.google.gson.*;
import net.accounts.Account;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AccountSerializer implements BiDirectionalJsonSerializer<Account> {

    private ArrayList<String> accountNumbers = new ArrayList<>();

    @Override
    public JsonElement serialize(Account account, Type type, JsonSerializationContext jsonSerializationContext) {
        String id = account.getId();
        JsonObject object = new JsonObject();
        object = serializeStub(object,account,jsonSerializationContext);
        if (!accountNumbers.contains(id)) {
            accountNumbers.add(id);
            object = serializeBody(object,account,jsonSerializationContext);
        }
        return object;
    }

    @Override
    public JsonObject serializeStub(JsonObject object,Account account,JsonSerializationContext jsonSerializationContext) {
        object.addProperty("id",account.getId());
        object.addProperty("name",account.getName());
        object.addProperty("username",account.getUsername());
        object.addProperty("dob",account.getDob().toString());
        object.addProperty("phone",account.getPhoneNumber());
        object.addProperty("email",account.getEmail());
        object.addProperty("address",account.getAddress());
        object.addProperty("type",account.getType());
        return object;
    }

    @Override
    public JsonObject serializeBody(JsonObject object, Account account, JsonSerializationContext jsonSerializationContext) {
        object.add("contracts",jsonSerializationContext.serialize(account.getContracts()));
        object.add("products",jsonSerializationContext.serialize(account.getProducts()));
        return object;
    }
}
