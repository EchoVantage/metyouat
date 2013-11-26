package com.metyouat.playground;

public enum MimeType {
	gif("image/gif"),
	jpg("image/jpeg"),
	jpeg("image/jpeg"),
	png("image/png"),
	tif("image/tiff"),
	tiff("image/tiff"),
	ico("image/x-icon"),
	js("application/javascript; charset=UTF-8"),
	html("text/html; charset=UTF-8"),
	css("text/css; charset=UTF-8"),
	xml("text/xml; charset=UTF-8"),
	pdf("application/pdf"),
	json("application/json; charset=UTF-8"),
	txt("text/plain; charset=UTF-8"),
	log("text/plain; charset=UTF-8");
	
	private String mime;

	private MimeType(String mime){
		this.mime = mime;
	}
	
	public static MimeType of(String path){
		try{
			return valueOf(path.substring(path.lastIndexOf('.') + 1).toLowerCase());
		}catch(Exception e){
			return txt;
		}
	}
	
	@Override
	public String toString() {
	   return mime;
	}
}
