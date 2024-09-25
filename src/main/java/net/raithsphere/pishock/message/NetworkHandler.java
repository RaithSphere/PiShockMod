package net.raithsphere.pishock.message;

import net.raithsphere.pishock.config.PiShockModCommonConfigs;
import net.raithsphere.pishock.pishock;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.minecraft.client.Minecraft;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

public class NetworkHandler {

    private static final String ENDPOINT = "https://do.pishock.com/Api/apioperate";

    public static float last = 0;
    private static Date millis = new Date();

    public static void post(float damage, float now, float max, int isAlive) {
        if (!isCooldownOk())
            return;

        if (Minecraft.getInstance().player.isDeadOrDying() && !PiShockModCommonConfigs.TRIGGER.get())
            return;

        millis = new Date();
        millis.setSeconds(millis.getSeconds() + PiShockModCommonConfigs.COOLDOWN.get());

        byte mode = PiShockModCommonConfigs.MODE.get();
        double m0 = (damage / max) * 100f;
        double m1 = ((max - now) / max) * 100f;

        double intensity = mode == 0 ? m0 : m1;

        if (Minecraft.getInstance().player.isDeadOrDying())
            intensity = PiShockModCommonConfigs.DEATH_INTENSITY.get();

        try {
            HashMap<String, Object> args = new HashMap<>();
            args.put("Username", PiShockModCommonConfigs.USERNAME.get());
            args.put("Code", PiShockModCommonConfigs.CODE.get());
            args.put("ApiKey", PiShockModCommonConfigs.API_KEY.get());
            args.put("Op", 0);
            args.put("Name", "MineCraft");
            args.put("Duration", 1);
            args.put("Intensity", Math.round(intensity));
            args.put("Scale", true);

            Gson g = new GsonBuilder().setPrettyPrinting().create();
            String json = new ObjectMapper(g).writeValueAsString(args);
            byte[] out = json.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost request = new HttpPost(ENDPOINT);
            request.setEntity(new StringEntity(json, StandardCharsets.UTF_8));
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(request);
            pishock.LOGGER.info("Sent HTTP request (POST) with arguments: " + args);
            pishock.LOGGER.info("Request anwsered with response code: " + response.getStatusLine().getStatusCode());

            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            pishock.LOGGER.info(responseBody);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isCooldownOk() {
        return new Date().after(millis);
    }
}
