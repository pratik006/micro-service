package com.pega.platform.integrationcore.utils;

import java.io.File;
import java.io.FileInputStream;

public class IntegrationTestUtils {
	
	public static byte[] getFileData(String aFilePath) {
		byte[] buffer = null;
		File a_file = new File(aFilePath);
		try (FileInputStream fis = new FileInputStream(aFilePath)) {
			int length = (int) a_file.length();
			buffer = new byte[length];
			fis.read(buffer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return buffer;
	}
 
}
