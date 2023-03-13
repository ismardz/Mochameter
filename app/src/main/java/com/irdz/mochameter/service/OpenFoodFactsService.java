package com.irdz.mochameter.service;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.util.ExecutorUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenFoodFactsService {

    private static final String CATEGORY = "coffees";

    private static final String URL_GET_BARCODE = "https://world.openfoodfacts.org/api/v0/product/%s.json";

    private static final String URL_POST_PRODUCT_IMAGE = "https://world.openfoodfacts.org/cgi/product_image_upload.pl";
    private static final String URL_POST_PRODUCT_CREATE = "https://world.openfoodfacts.org/cgi/product_jqm2.pl?" +
        "code=%s&brands=%s&product_name=%s&categories=" + CATEGORY;
    private static final String URL_POST_PRODUCT_ADD_CATEGORY = "https://world.openfoodfacts.org/cgi/product_jqm2.pl?" +
        "code=%s&categories=" + CATEGORY;
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
        String urlWithBarcode = String.format(URL_GET_BARCODE, barcode);
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

    public void uploadProduct(
        final String barcode,
        final String brand,
        final String name,
        final Bitmap photo
    ) {
        ExecutorUtils.runCallables(() -> {
            createProduct(barcode, brand, name);
            uploadPhoto(barcode, photo);
            return null;
        });
    }

    private void uploadPhoto(final String barcode, final Bitmap photo) {
        Request.Builder builder = new Request.Builder()
//            .url("https://world.openfoodfacts.org/api/v0/product/" + barcode + "/images")
            .url(URL_POST_PRODUCT_IMAGE)
            .addHeader("User-Agent", "Mochameter/0.2")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/x-www-form-urlencoded");


        File file = mapBitmapToFile(photo);
        RequestBody fileToUpload = RequestBody.create(
            MediaType.parse("image/jpeg"),
            file
        );

        // Create the request body with the image file and parameters
        RequestBody body = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("imgupload_front", "image.jpg", fileToUpload)
            .addFormDataPart("code", barcode)
            .addFormDataPart("imagefield", "front")
            .build();

        // Set the request body and method, and execute the request
        Request request = builder.post(body).build();
        try {
            Response response = client.newCall(request).execute();

//             Print the response body
            Log.i(TAG, "upload photo response" + response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File mapBitmapToFile(final Bitmap photo) {
        File file = null;
        try {
            file = Files.createTempFile("photoMochameter", String.valueOf(System.currentTimeMillis())).toFile();
            // Open a file output stream for the file
            FileOutputStream fos = new FileOutputStream(file);

            // Compress the bitmap and save it to the file
            photo.compress(Bitmap.CompressFormat.PNG, 100, fos);

            // Close the output stream
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private void createProduct(final String barcode, final String brand, final String name) {
        String urlWithValues = String.format(URL_POST_PRODUCT_CREATE, barcode, brand, name);
        Request request = new Request.Builder()
            .url(urlWithValues)
            .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Process the response body
            String responseString = response.body().string();
            Log.i(TAG, "create product response" + responseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCoffeeCategory(final String barcode) {
        ExecutorUtils.runCallables(() -> {
            String urlWithValues = String.format(URL_POST_PRODUCT_ADD_CATEGORY, barcode);
            Request request = new Request.Builder()
                .url(urlWithValues)
                .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Process the response body
                String responseString = response.body().string();
                Log.i(TAG, "create product response" + responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });

    }

}
