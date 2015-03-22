package com.sypir.utils;

import android.content.Context;
import android.net.ParseException;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class ParseAPIUtils {
	public static ParseUser user;
	public static final String appID = "LJhYzRdheou1pCErZGmSfL1vW1hT0qOpKWMpSZs6";
	public static final String clientKey = "9Z0aFpWmhiN45i9JpnaxBeh18Rvl2tAAUwaeLNLQ";

	public static boolean getUser() {
		user = ParseUser.getCurrentUser();
		if (user == null) {
			return false;
		}
		return true;
	}

	public static void login(String un, String pw, String dn,final Context context) {
		user = new ParseUser();
		user.setUsername(dn);
		user.setPassword(pw);
		user.setEmail(un);
		ParseUser.logInInBackground(dn, pw, new LogInCallback() {

			@Override
			public void done(ParseUser usr, com.parse.ParseException e) {
				if (e == null) {
					Toast.makeText(context, "Login Success",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "Login Failed",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		getUser();
	}

	public static void register(String un, String pw, String dn, final Context context) {
		Toast.makeText(context, "register user", Toast.LENGTH_SHORT).show();
		user = new ParseUser();
		user.setUsername(dn);
		user.setPassword(pw);
		user.setEmail(un);

		user.signUpInBackground(new SignUpCallback() {
			@Override
			public void done(com.parse.ParseException e) {
				if (e == null) {
					Toast.makeText(context, "Register Success",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "Registration Failed",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
