package misbah.naseer.mobilestore.helper;

import android.Manifest;

/**
 * Created by Devprovider on 29/04/2017.
 */

public class Constants {

    public static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static final String SERVICE_DATA_PASS_KEY = "service_data_pass_key";

    public static final String USER_TYPE_ADMIN = "admin";
    public static final String USER_TYPE_STORE = "store";
    public static final String USER_TYPE_DISTRIBUTOR = "distributor";

    public static final String USER_TYPE = "userType";
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String CONTACT="contact";

    public static final String MESSAGE_FROM = "from";
    public static final String MESSAGE_DATE = "time";
    public static final String MESSAGE_BODY = "message";
    public static final String MESSAGE_LOCATION = "location";
    public static final String MESSAGES_PASS_KEY = "messages_pass_key";

    public static final String ORDER_TEXT_KEY = "order";
    public static final String ORDER_STATUS_KEY = "status";

    public static final String ORDER_PENDING = "pending";
    public static final String ORDER_DONE = "done";

    public static final String LAT_KEY = "latitude";
    public static final String LONG_KEY = "longitude";

    public static final String DRIVERS_LIST_KEY = "drivers_list_key";

}
