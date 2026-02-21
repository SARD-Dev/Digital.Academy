package com.example.digitalacademy.Common.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.digitalacademy.Common.StringUtils;
import com.google.firebase.database.Exclude;

public class UserInfo implements Parcelable {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;

    public UserInfo(){

    }
    protected UserInfo(Parcel in) {
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        password = in.readString();
        phoneNumber = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Exclude
    public Boolean isAnyInfoEmpty() {
        return StringUtils.isNullOrBlank(email)
                || StringUtils.isNullOrBlank(firstName)
                || StringUtils.isNullOrBlank(lastName)
                || StringUtils.isNullOrBlank(password)
                || StringUtils.isNullOrBlank(phoneNumber);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(password);
        dest.writeString(phoneNumber);
    }
}