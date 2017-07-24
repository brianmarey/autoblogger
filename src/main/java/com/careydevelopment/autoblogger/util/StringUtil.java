package com.careydevelopment.autoblogger.util;

import java.util.StringTokenizer;

public class StringUtil {

	public static int countNumberOfWords(String str) {
		StringTokenizer st = new StringTokenizer(str);
		return st.countTokens();
	}
}
