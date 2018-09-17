package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import model.HttpHeader;
import model.User;

public class HttpRequestUtils {
	private static final Logger log = LoggerFactory.getLogger(HttpRequestUtils.class);
	
	public static HttpHeader getHttp(String firstLine) {
		String[] splited = firstLine.split(" ");
		HttpHeader httpHeader = new HttpHeader(splited[0], splited[1]);
		return httpHeader;
	}
	
	public static User getGetUser(String url) {
		int index = url.indexOf("?");
		String queryString = url.substring(index + 1);
		Map<String, String> params = parseQueryString(queryString);
		User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
		log.debug("user : {} ", user);
		
		return user;
	}
	
	public static User getPostUser(BufferedReader br, String line) throws IOException {
		Map<String, String> headers = new HashMap<>();
		while(!"".equals(line)) {
			log.debug("header : {}", line);
			line = br.readLine();
			String[] headerTokens = line.split(": ");
			if(headerTokens.length == 2) {
				headers.put(headerTokens[0], headerTokens[1]);
			}
		}
		log.debug("Content-Length : {} ", headers.get("Content-Length"));
		
		String requestBody = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
		
		log.debug("RequestBody : {} ", requestBody);
		Map<String, String> params = HttpRequestUtils.parseQueryString(requestBody);
		User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
		log.debug("user : {} ", user);
		
		return user;
	}
	
	/**
	 * @param queryString은 URL에서 ? 이후에 전달되는 name=value 임
	 * @return
	 */
	public static Map<String, String> parseQueryString(String queryString) {
		if (Strings.isNullOrEmpty(queryString)) {
			return Maps.newHashMap();
		}
		
		String[] tokens = queryString.split("&");
		return Arrays.stream(tokens)
					.map(t -> getKeyValue(t, "="))
					.filter(p -> p != null)
					.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
	}
	
	static Pair getKeyValue(String keyValue, String regex) {
		if (Strings.isNullOrEmpty(keyValue)) {
			return null;
		}
		
		String[] tokens = keyValue.split(regex);
		if (tokens.length != 2) {
			return null;
		}
		
		return new Pair(tokens[0], tokens[1]);
	}
	
	public static Pair parseHeader(String header) {
		return getKeyValue(header, ": ");
	}
	
	public static class Pair {
		String key;
		String value;
		
		Pair(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public String getKey() {
			return key;
		}
		
		public String getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Pair [key=" + key + ", value=" + value + "]";
		}
	}
}
