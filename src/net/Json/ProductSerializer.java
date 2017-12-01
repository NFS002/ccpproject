package net.Json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.transactions.Contract;
import net.transactions.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ProductSerializer implements BiDirectionalJsonSerializer<Product> {

    private ArrayList<String> pids = new ArrayList<>();


    @Override
    public JsonElement serialize(Product product, Type type, JsonSerializationContext jsonSerializationContext) {
        String id = product.getId();
        JsonObject object = new JsonObject();
        object = serializeStub(object,product,jsonSerializationContext);
        if (!pids.contains(id)) {
            pids.add(id);
            object = serializeBody(object,product,jsonSerializationContext);
        }
        return object;
    }

    @Override
    public JsonObject serializeStub(JsonObject object, Product product, JsonSerializationContext jsonSerializationContext) {
        object.addProperty("id",product.getId());
        object.addProperty("owner",product.getOwner());
        object.addProperty("size",product.getSize());
        object.addProperty("date", product.getDc().toString());
        object.addProperty("description",product.getDescription());
        object.addProperty("rrp",product.getRRP());
        return object;
    }

    @Override
    public JsonObject serializeBody(JsonObject object, Product product, JsonSerializationContext jsonSerializationContext) {
    		ArrayList<Contract> contracts = product.getContracts();
        if (!contracts.isEmpty()) object.add("contracts",jsonSerializationContext.serialize(contracts));
        return object;
    }


}
