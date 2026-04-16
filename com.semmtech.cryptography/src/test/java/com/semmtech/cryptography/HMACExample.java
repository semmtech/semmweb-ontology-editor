package com.semmtech.cryptography;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class HMACExample {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String message = "This is a very secret message!";
		String privateKey = "mikehenrichs";

		String correctDigest = "da9c71e4083f422bcd16b7eac5d83138f5f28b39afc275fd1b8f4bf41a5f926f";
		String sha256 = SHA256(message);
		System.out.println("SHA256 = " + sha256 + " (" + (sha256.equals(correctDigest) ? "OK" : "Wrong") + ")");

		String correctHmacHex = "2026f23f299f2b750ecd97ce4b3238ae0db63da3a7d20cc5020f4f219440ee41";
		// Use Bouncy Castle
		try {
			HMac hmac = new HMac(new SHA256Digest());
			hmac.init(new KeyParameter(privateKey.getBytes("UTF-8")));
			byte[] bytes = message.getBytes("UTF-8");
			hmac.update(bytes, 0, bytes.length);
			byte[] mac = new byte[hmac.getMacSize()];
			hmac.doFinal(mac, 0);
			String signature = new String(Hex.encode(mac));
			System.out.println("mac alg: " + hmac.getAlgorithmName());
			System.out.println("dig alg: " + hmac.getUnderlyingDigest().getAlgorithmName());
			System.out.println("key: " + privateKey);
			System.out.println("message: " + message);
			System.out.println("unencoded: " + new String(mac));
			System.out.println("Signature (Hex): " + signature + " (" + (signature.equals(correctHmacHex) ? "OK" : "Wrong") + ")");
			System.out.println("Signature (Base64): " + new String(Base64.encode(mac)));
		} 
		catch (UnsupportedEncodingException e) {

		} 
		catch (Exception e) {

		}
		System.out.println("Done");
	}

	private static String SHA256(String base) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} 
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
