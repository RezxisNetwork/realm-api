package net.rezxis.mchosting;

import fi.iki.elonen.NanoHTTPD;
import net.rezxis.mchosting.database.Tables;

public class RezxisAPI extends NanoHTTPD {
    public RezxisAPI(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            String uri = session.getUri().trim();
            if (uri.startsWith("/onlineservers")){
                int os = Tables.getSTable().getOnlineServers().size();
                return newFixedLengthResponse(String.valueOf(os));
            } else if (uri.startsWith("/playingplayers")) {
            	return newFixedLengthResponse(String.valueOf(Tables.getPTable().getOnlinePlayers()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.getMessage());
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND,"text/plain", "");
    }
}
