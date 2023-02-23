package com.irdz.mochameter.service;

import com.google.gson.Gson;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.util.ExecutorUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenFoodFactsService {

    private static final String URL = "https://world.openfoodfacts.org/api/v0/product/%s.json";
    private static OpenFoodFactsService instance;

    public static OpenFoodFactsService getInstance() {
        if(instance == null) {
            instance = new OpenFoodFactsService();
        }
        return instance;
    }


    private static OkHttpClient client = new OkHttpClient();

    public OpenFoodFactsResponse findProductByBarcode(final String barcode) {
        final OpenFoodFactsResponse[] offResponse = new OpenFoodFactsResponse[1];
        String urlWithBarcode = String.format(URL, barcode);
        Request request = new Request.Builder()
            .url(urlWithBarcode)
            .build();
        ExecutorUtils.runCallables(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Process the response body
                String responseString = response.body().string();
                Gson gson = new Gson();
                offResponse[0] = gson.fromJson(responseString, OpenFoodFactsResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return offResponse[0];
        });
        return offResponse[0];
    }

}
