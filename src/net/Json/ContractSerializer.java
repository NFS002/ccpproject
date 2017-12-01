package net.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.transactions.Contract;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ContractSerializer implements BiDirectionalJsonSerializer<Contract> {

    private ArrayList<String> cids = new ArrayList<>();

    @Override
    public JsonElement serialize(Contract contract, Type type, JsonSerializationContext jsonSerializationContext) {
        String id = contract.getId();
        JsonObject object = new JsonObject();
        object = serializeStub(object,contract,jsonSerializationContext);
        if (!cids.contains(id)) {
            cids.add(id);
            object = serializeBody(object,contract,jsonSerializationContext);
        }
        return object;
    }

    @Override
    public JsonObject serializeStub(JsonObject object, Contract contract, JsonSerializationContext jsonSerializationContext) {
        object.addProperty("id",contract.getId());
        object.addProperty("date",  contract.getCreated().toString());
        object.addProperty("aidTo",contract.getAcctNumTo());
        object.addProperty("aidFrom",contract.getAcctNumFrom());
        object.addProperty("value",contract.getValue());
        object.addProperty("confirmed",contract.getConfirmed());
        return object;
    }

    @Override
    public JsonObject serializeBody(JsonObject object, Contract contract, JsonSerializationContext jsonSerializationContext) {
        object.add( "products",jsonSerializationContext.serialize(contract.getProducts()));
        return object;
    }
}
