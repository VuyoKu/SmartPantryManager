package com.example.smartpantrymanager.network;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    /**
     * "localhost" works because the app is tested over USB with:
     *     adb reverse tcp:3000 tcp:3000
     * which forwards the phone's localhost:3000 to the PC's localhost:3000.
     * For an emulator the host would be 10.0.2.2; for Wi-Fi, the PC's LAN IP.
     */
    private static final String BASE_URL = "http://localhost:3000/"; // trailing slash is mandatory

    private static ApiService service;

    private ApiClient() { }

    public static synchronized ApiService get() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(ApiService.class);
        }
        return service;
    }
}
