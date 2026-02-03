package frb.axeron.shared;

public class AxeronApiConstant {
    public static class server {

        public static final String BINDER_DESCRIPTOR = "frb.axeron.server.IAxeronService";
        public static final String SHIZUKU_BINDER_DESCRIPTOR = "moe.shizuku.server.IShizukuService";
        public static final String VERSION_NAME = BuildConfig.VERSION_NAME;
        public static final long VERSION_CODE = BuildConfig.VERSION_CODE;
        public static final int TYPE_DEFAULT_ENV = -1;
        public static final int TYPE_ENV = 0;
        public static final int TYPE_NEW_ENV = 1;

        public static final int SHIZUKU_SERVER_VERSION = 13;
        public static final int SHIZUKU_SERVER_PATCH_VERSION = 5;

        // binder
        public static final int BINDER_TRANSACTION_transact = 1;

        // user service
//    @RestrictTo(LIBRARY_GROUP_PREFIX)
        public static final int USER_SERVICE_TRANSACTION_destroy = 16777115;

        public static final String USER_SERVICE_ARG_TAG = "shizuku:user-service-arg-tag";
        public static final String USER_SERVICE_ARG_COMPONENT = "shizuku:user-service-arg-component";
        public static final String USER_SERVICE_ARG_DEBUGGABLE = "shizuku:user-service-arg-debuggable";
        public static final String USER_SERVICE_ARG_VERSION_CODE = "shizuku:user-service-arg-version-code";
        public static final String USER_SERVICE_ARG_PROCESS_NAME = "shizuku:user-service-arg-process-name";
        public static final String USER_SERVICE_ARG_NO_CREATE = "shizuku:user-service-arg-no-create";
        public static final String USER_SERVICE_ARG_DAEMON = "shizuku:user-service-arg-daemon";
        public static final String USER_SERVICE_ARG_USE_32_BIT_APP_PROCESS = "shizuku:user-service-arg-use-32-bit-app-process";
        public static final String USER_SERVICE_ARG_REMOVE = "shizuku:user-service-remove";

        //    @RestrictTo(LIBRARY_GROUP_PREFIX)
        public static final String USER_SERVICE_ARG_TOKEN = "shizuku:user-service-arg-token";
        public static final String USER_SERVICE_ARG_PGID = "shizuku:user-service-arg-pgid";

        // bind application
        public static final String BIND_APPLICATION_SERVER_VERSION = "shizuku:attach-reply-version";
        public static final String BIND_APPLICATION_SERVER_PATCH_VERSION = "shizuku:attach-reply-patch-version";
        public static final String BIND_APPLICATION_SERVER_UID = "shizuku:attach-reply-uid";
        public static final String BIND_APPLICATION_SERVER_SECONTEXT = "shizuku:attach-reply-secontext";
        public static final String BIND_APPLICATION_PERMISSION_GRANTED = "shizuku:attach-reply-permission-granted";
        public static final String BIND_APPLICATION_SHOULD_SHOW_REQUEST_PERMISSION_RATIONALE = "shizuku:attach-reply-should-show-request-permission-rationale";

        // request permission
        public static final String REQUEST_PERMISSION_REPLY_ALLOWED = "shizuku:request-permission-reply-allowed";
        public static final String REQUEST_PERMISSION_REPLY_IS_ONETIME = "shizuku:request-permission-reply-is-onetime";

        // attach application
        public static final String ATTACH_APPLICATION_PACKAGE_NAME = "shizuku:attach-package-name";
        public static final String ATTACH_APPLICATION_API_VERSION = "shizuku:attach-api-version";

    }

    public interface folder {
        String ROOT = "/";
        String TMP = "data/local/tmp/";
        String ADB = "data/adb/";
        String ROOT_DE = "data/user_de/0/";
        String SHELL_DE = "data/user_de/0/com.android.shell/";
        String PARENT = "axeron/";
        String PLUGIN = "plugins/";
        String PLUGIN_UPDATE = "plugins_update/";
        String CACHE = "cache/";
        String LOG = "logs/";
        String BINARY = "bin/";
        String EXTERNAL_BINARY = "xbin/";
        String ZIP = "zip/";

        String PARENT_PLUGIN = PARENT + PLUGIN;
        String PARENT_CACHE = PARENT + CACHE;
        String PARENT_LOG = PARENT + LOG;
        String PARENT_BINARY = PARENT + BINARY;
        String PARENT_EXTERNAL_BINARY = PARENT + EXTERNAL_BINARY;
        String PARENT_ZIP = PARENT + ZIP;
        String PARENT_PLUGIN_UPDATE = PARENT + PLUGIN_UPDATE;
    }


    interface permission {

        interface ops {
            int OP_COARSE_LOCATION = 0;
            int OP_FINE_LOCATION = 1;
            int OP_GPS = 2;
            int OP_VIBRATE = 3;
            int OP_CAMERA = 26;
            int OP_RECORD_AUDIO = 27;
            int OP_SYSTEM_ALERT_WINDOW = 24;
            int OP_ACCESS_NOTIFICATION_POLICY = 25;
            int OP_WAKE_LOCK = 40;
            int OP_GET_USAGE_STATS = 43;
            int OP_ACTIVATE_VPN = 47;
            int OP_REQUEST_INSTALL_PACKAGES = 63;
            int OP_MANAGE_EXTERNAL_STORAGE = 92;
            int OP_ACCESS_MEDIA_LOCATION = 87;
            int OP_ACCESS_NOTIFICATIONS = 88;
            int OP_BODY_SENSORS = 56;
            int OP_READ_CONTACTS = 4;
            int OP_WRITE_CONTACTS = 5;
            int OP_READ_CALL_LOG = 6;
            int OP_WRITE_CALL_LOG = 7;
            int OP_READ_SMS = 14;
            int OP_RECEIVE_SMS = 16;
            int OP_SEND_SMS = 20;
            int OP_RECEIVE_MMS = 18;
            int OP_READ_EXTERNAL_STORAGE = 59;
            int OP_WRITE_EXTERNAL_STORAGE = 60;
            int OP_WRITE_SETTINGS = 23;
            int OP_RUN_ANY_IN_BACKGROUND = 70;

        }
    }
}
