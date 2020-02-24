package net.rezxis.mchosting;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.gson.Gson;

import fi.iki.elonen.NanoHTTPD;
import net.rezxis.mchosting.database.Tables;
import net.rezxis.mchosting.database.object.player.DBPlayer;
import net.rezxis.mchosting.returns.DiscordLinkReturn;
import net.rezxis.mchosting.returns.SystemRequestReturn;
import net.rezxis.mchosting.returns.VPNRequestReturn;

public class RezxisAPI extends NanoHTTPD {
    public RezxisAPI(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            String uri = session.getUri().trim();
            System.out.println(session.getRemoteIpAddress()+" : "+uri);
            if (uri.startsWith("/onlineservers")){
                int os = Tables.getSTable().getOnlineServers().size();
                return newFixedLengthResponse(Response.Status.OK,"text/plain",String.valueOf(os));
            } else if (uri.startsWith("/playingplayers")) {
            	return newFixedLengthResponse(Response.Status.OK,"text/plain",String.valueOf(Tables.getPTable().getOnlinePlayers()));
            } else if (uri.startsWith("/system")) {
            	SystemRequestReturn sys = new SystemRequestReturn();
            	Runtime rt = Runtime.getRuntime();
            	sys.mem = rt.maxMemory();
            	sys.memUsed = rt.maxMemory()-rt.freeMemory();
            	return newFixedLengthResponse(Response.Status.OK,"application/json",new Gson().toJson(sys));
            } else if (uri.startsWith("/vpn")) {
            	VPNRequestReturn ret = new VPNRequestReturn(-1, "Something went to worng");
            	UUID target = null;
            	String operator = null;
            	boolean value = false;
            	for (Entry<String,List<String>> entry : session.getParameters().entrySet()) {
            		if (entry.getKey().equalsIgnoreCase("target")) {
            			try {
            				target = UUID.fromString(entry.getValue().get(0));
            			} catch (Exception ex) {
            				ex.printStackTrace();
            			}
            		} else if (entry.getKey().equalsIgnoreCase("value")) {
            			try {
            				value = Boolean.valueOf(entry.getValue().get(0));
            			} catch (Exception ex) {
            				ex.printStackTrace();
            			}
            		} else if (entry.getKey().equalsIgnoreCase("operator")) {
            			operator = entry.getValue().get(0);
            		}
            	}
            	if (target == null) {
            		ret = new VPNRequestReturn(-1, "target is null");
            	} else if (operator == null) {
            		ret = new VPNRequestReturn(-1, "operator is null");
            	} else {
            		DBPlayer player = Tables.getPTable().get(target);
            		if (player == null) {
            			ret = new VPNRequestReturn(-1, "couldn't find the player");
            		} else {
            			player.setVpnBypass(value);
            			ret = new VPNRequestReturn(0, "success");
            		}
            	}
            	return newFixedLengthResponse(Response.Status.OK,"application/json", new Gson().toJson(ret));
            } else if (uri.startsWith("/discord")) {
            	long id = -1;
            	for (Entry<String,List<String>> entry : session.getParameters().entrySet()) {
            		if (entry.getKey().equalsIgnoreCase("id")) {
            			try {
            				id = Long.valueOf(entry.getValue().get(0));
            			} catch (Exception ex) {
            				ex.printStackTrace();
            			}
            		}
            	}
            	DiscordLinkReturn lReturn = new DiscordLinkReturn();
            	if (id == -1) {
            		lReturn.code = -1;
            		lReturn.message = "invaild request args";
            	} else {
            		DBPlayer player = Tables.getPTable().getByDiscordId(id);
            		if (player == null) {
            			lReturn.code = -2;
                		lReturn.message = "no user with the id";
            		} else {
            			lReturn.code = 0;
                		lReturn.message = player.getUUID().toString();
            		}
            	}
            	return newFixedLengthResponse(Response.Status.OK,"application/json", new Gson().toJson(lReturn));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", e.getMessage());
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND,"text/plain", "");
    }
}
