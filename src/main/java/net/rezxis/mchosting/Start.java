package net.rezxis.mchosting;

import net.rezxis.mchosting.database.Database;

import java.io.IOException;

public class Start {
    public static Props props;

    public static void main(String[] args) throws InterruptedException, IOException {
        props = new Props("api.propertis");
        Database.init(props.DB_HOST,props.DB_USER,props.DB_PASS,props.DB_PORT,props.DB_NAME);

        RezxisAPI api = new RezxisAPI(8080);
        api.start();
        while(api.isAlive()){
            Thread.sleep(60000);
        }
    }
}
