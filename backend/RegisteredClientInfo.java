package backend;

public class RegisteredClientInfo {
	private String clientIP;
	private String port;
	private int interval;
	private double createdTime;
	
	public RegisteredClientInfo() {}
	
	public RegisteredClientInfo(String ip, String port, int interval) {
		this.clientIP = ip;
		this.port = port;
		this.interval = interval;
		this.createdTime = System.currentTimeMillis()/1000.d;
	}
	
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	
	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public double getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(double createdTime) {
		this.createdTime = createdTime;
	}

	@Override
	public int hashCode() {
		int code = 0;
		if (clientIP != null) code += clientIP.hashCode();
		if (port != null) code += port.hashCode();
		return code;
	}
	
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof RegisteredClientInfo)) {
			return false;
		}
		if (o == null) return false;
		RegisteredClientInfo info = (RegisteredClientInfo) o;
		return info.getClientIP().equals(info.getClientIP()) && info.getPort().equals(info.getPort());
	}
	
}
