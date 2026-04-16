package com.semmtech.net;

public final class BasicFTPClientTest {

	private BasicFTPClientTest() { }

	/**
	 * @param args provided arguments
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		BasicFTPClient client = new BasicFTPClient();
		
		client.setHost("semmtech.com");
		client.setUsername("admin_idoro");
		client.setPassword("Ge2q1we4");
		client.setRemoteFilename("Temp/tekst.txt");
		
		client.connect();
		client.uploadFile("src/test/resources/tekst.txt");
		
		System.out.println("INFO:: client.getSuccessMessage() = " + client.getSuccessMessage());
		
		client.connect();
		client.downloadFile("D:\\tekst.txt");
		System.out.println("INFO:: client.getSuccessMessage() = " + client.getSuccessMessage());
		System.out.println("INFO:: client.getErrorMessage() = " + client.getErrorMessage());
	}

}
