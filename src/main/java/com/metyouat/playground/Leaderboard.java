package com.metyouat.playground;

import static java.util.Arrays.asList;
import static org.simpleframework.http.Protocol.CACHE_CONTROL;
import static org.simpleframework.http.Protocol.ETAG;
import static org.simpleframework.http.Protocol.IF_MODIFIED_SINCE;
import static org.simpleframework.http.Protocol.IF_NONE_MATCH;
import static org.simpleframework.http.Protocol.LAST_MODIFIED;
import static org.simpleframework.http.Status.NOT_MODIFIED;
import static org.simpleframework.http.Status.OK;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import com.google.common.base.Charsets;

public class Leaderboard implements Container, AutoCloseable{
	private static final int ONE_WEEK = 60 * 60 * 24 * 7; // 7 days in seconds
	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static MessageDigest MD2;
	static {
		try {
			MD2 = MessageDigest.getInstance("MD2");
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException("MD2 is a required message digest algorithm", e);
		}
	}
	
	public static String toHexString(byte[] bytes, int offset, int length) {
		char[] ch = new char[length * 2];
		for(int i = 0, j = offset; j < offset + length; j++) {
			ch[i++] = HEX[(bytes[j] >> 4) & 0xF];
			ch[i++] = HEX[bytes[j] & 0xF];
		}
		return new String(ch);
	}
	
	public static String toHexString(byte[] bytes){
		return toHexString(bytes, 0, bytes.length);
	}

	public static byte[] digest(byte[] bytes) {
		return MD2.digest(bytes);
	}

	private Connection connection;
	private Path root;
	private Database db;

	public void connect(String host, int port, Path root, Database db) throws IOException{
		this.root = root;
		this.db = db;
		Server server = new ContainerServer(this);
		connection = new SocketConnection(server);
		connection.connect(new InetSocketAddress(host, port));
	}
	
	public static String eTag(byte[] bytes) {
		return toHexString(digest(bytes), 0, 8);
	}

	protected static boolean isIn(final String etagList, String eTag) {
		if(etagList == null) {
			return false;
		}
		List<String> etags = asList(etagList.split(",\\s*"));
		return etags.contains(eTag) || etags.contains('"' + eTag + '"');
	}

	protected static boolean isModifiedSince(final long modifiedSince, long lastModified) {
		return modifiedSince < lastModified - 1000;
	}

	@Override
	public void handle(Request req, Response resp) {
		try {
			if(req.getPath().getPath(0, 1).equals("/api")){
				List<Map<String,Object>> list;
				switch(req.getPath().getPath(1, 1)){
					case "/leaderboard":
						list = db.getLeaderboard(req.getPath().getPath(2, 1).substring(1));
						break;
					case "/feed":
						list = db.getFeed(req.getPath().getPath(2, 1).substring(1));
						break;
					default:
						resp.setStatus(Status.NOT_FOUND);
						resp.close();
						return;
				}
				byte[] serial = serial(list);
			   resp.setValue(CACHE_CONTROL, "no-cache");
				serve(req, resp, serial, System.currentTimeMillis(), MimeType.json);
			}else{
				serveStatic(req, resp);
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static byte[] serial(List<Map<String, Object>> list) {
		StringBuilder builder = new StringBuilder("[");
		boolean first = true;
		for(Map<String,Object> o: list){
			if(!first){
				builder.append(",");
			}
			first = false;
			builder.append("{");
			boolean second = true;
			for(Map.Entry<String,Object> entry: o.entrySet()){
				if(!second){
					builder.append(",");
				}
				second = false;
				builder.append("\"").append(entry.getKey()).append("\":");
				if(entry.getValue() instanceof String){
					builder.append("\"").append(entry.getValue().toString().replaceAll("\\\\", "\\\\").replaceAll("\\s+", " ").replaceAll("\"", "\\\"")).append("\"");
				}else{
					builder.append(entry.getValue());
				}
			}
			builder.append("}");
		}
	   return builder.append("]").toString().getBytes(Charsets.UTF_8);
   }

	private void serveStatic(Request req, Response resp) throws IOException {
	   Path path = root.resolve(req.getPath().getPath().substring(1));
	   byte[] bytes = Files.readAllBytes(path);
	   long lastModified = Files.getLastModifiedTime(path).toMillis();
	   MimeType contentType = MimeType.of(path.toString());
	   resp.setValue(CACHE_CONTROL, "private, max-age=" + ONE_WEEK);
	   serve(req, resp, bytes, lastModified, contentType);
   }

	private static void serve(Request req, Response resp, byte[] bytes, long lastModified, MimeType contentType) throws IOException {
		String eTag = eTag(bytes);
	   resp.setContentType(contentType.toString());
	   resp.setValue(ETAG, '"' + eTag + '"');
	   resp.setDate(LAST_MODIFIED, lastModified);
	   if(isIn(req.getValue(IF_NONE_MATCH), eTag) || !isModifiedSince(req.getDate(IF_MODIFIED_SINCE), lastModified)) {
	   	resp.setStatus(NOT_MODIFIED);
	   	resp.close();
	   } else {
	   	resp.setStatus(OK);
	   	try(OutputStream stream = resp.getOutputStream()){
	   		stream.write(bytes);
	   	}
	   }
   }
	
	@Override
	public void close() throws Exception {
	   connection.close();
	}
}
