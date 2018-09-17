package model;

public class HttpHeader {
	private String type;
	private String url;
	
	public HttpHeader(String type, String url) {
		super();
		this.type = type;
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "HttpHeader [url=" + url + ", type=" + type + "]";
	}
}
