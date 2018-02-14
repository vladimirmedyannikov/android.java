package ru.mos.polls.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDataHelper extends SQLiteOpenHelper{
	private static final String CREATE_TRIGGER_INSERT_CAR = "CREATE TRIGGER IF NOT EXISTS insert_default_car " +
																" BEFORE INSERT ON "+ UserData.Cars.TABLE_NAME+" WHEN NEW."+ UserData.Cars.IS_DEFAULT+"=1 "+
																" BEGIN UPDATE "+ UserData.Cars.TABLE_NAME+" SET "+ UserData.Cars.IS_DEFAULT+"=0 ;END";
	private static final String CREATE_TRIGGER_SET_DEFAULT_CAR = "CREATE TRIGGER IF NOT EXISTS set_default_car " +
																" BEFORE UPDATE OF "+ UserData.Cars.IS_DEFAULT+" ON "+ UserData.Cars.TABLE_NAME+
																" BEGIN UPDATE "+ UserData.Cars.TABLE_NAME+" SET "+ UserData.Cars.IS_DEFAULT+"=0 ;END";

    private static final String CREATE_TRIGGER_SET_DEFAULT_FLAT = "CREATE TRIGGER IF NOT EXISTS set_default_flat " +
            " BEFORE UPDATE OF "+ UserData.Flats.IS_DEFAULT+" ON "+ UserData.Flats.TABLE_NAME+
            " BEGIN UPDATE "+ UserData.Flats.TABLE_NAME+" SET "+ UserData.Flats.IS_DEFAULT+"=0 ;END";
    private static final String CREATE_TRIGGER_INSERT_FLAT = "CREATE TRIGGER IF NOT EXISTS insert_default_flat " +
            " BEFORE INSERT ON "+ UserData.Flats.TABLE_NAME+" WHEN NEW."+ UserData.Flats.IS_DEFAULT+"=1 "+
            " BEGIN UPDATE "+ UserData.Flats.TABLE_NAME+" SET "+ UserData.Flats.IS_DEFAULT+"=0 ;END";

    private static final String CREATE_TRIGGER_SET_REGISTRATION_FLAT = "CREATE TRIGGER IF NOT EXISTS set_registration_flat " +
            " BEFORE UPDATE OF "+ UserData.Flats.IS_REGISTRATION+" ON "+ UserData.Flats.TABLE_NAME+
            " BEGIN UPDATE "+ UserData.Flats.TABLE_NAME+" SET "+ UserData.Flats.IS_REGISTRATION+"=0 ;END";
    private static final String CREATE_TRIGGER_INSERT_REG_FLAT = "CREATE TRIGGER IF NOT EXISTS insert_registration_flat " +
            " BEFORE INSERT ON "+ UserData.Flats.TABLE_NAME+" WHEN NEW."+ UserData.Flats.IS_REGISTRATION+"=1 "+
            " BEGIN UPDATE "+ UserData.Flats.TABLE_NAME+" SET "+ UserData.Flats.IS_REGISTRATION+"=0 ;END";

    private static final String CREATE_TRIGGER_SET_RESIDENCE_FLAT = "CREATE TRIGGER IF NOT EXISTS set_residence_flat " +
            " BEFORE UPDATE OF "+ UserData.Flats.IS_RESIDENCE+" ON "+ UserData.Flats.TABLE_NAME+
            " BEGIN UPDATE "+ UserData.Flats.TABLE_NAME+" SET "+ UserData.Flats.IS_RESIDENCE+"=0 ;END";
    private static final String CREATE_TRIGGER_INSERT_RES_FLAT = "CREATE TRIGGER IF NOT EXISTS insert_residence_flat " +
            " BEFORE INSERT ON "+ UserData.Flats.TABLE_NAME+" WHEN NEW."+ UserData.Flats.IS_RESIDENCE+"=1 "+
            " BEGIN UPDATE "+ UserData.Flats.TABLE_NAME+" SET "+ UserData.Flats.IS_RESIDENCE+"=0 ;END";

    private static final String CREATE_TRIGGER_SET_EDITING_BLOCKED_FLAT = "CREATE TRIGGER IF NOT EXISTS set_editing_blocked " +
            " BEFORE UPDATE OF "+ UserData.Flats.EDITING_BLOCKED+" ON "+ UserData.Flats.TABLE_NAME+
            " BEGIN UPDATE "+ UserData.Flats.TABLE_NAME+" SET "+ UserData.Flats.EDITING_BLOCKED+"=0 ;END";
    private static final String CREATE_TRIGGER_INSERT_EDITING_BLOCKED = "CREATE TRIGGER IF NOT EXISTS insert_editing_blocked " +
            " BEFORE INSERT ON "+ UserData.Flats.TABLE_NAME+" WHEN NEW."+ UserData.Flats.EDITING_BLOCKED+"=1 "+
            " BEGIN UPDATE "+ UserData.Flats.TABLE_NAME+" SET "+ UserData.Flats.EDITING_BLOCKED+"=0 ;END";

    private static final String CREATE_TRIGGER_SET_WORK_FLAT = "CREATE TRIGGER IF NOT EXISTS set_work_flat " +
            " BEFORE UPDATE OF "+ UserData.Flats.IS_WORK+" ON "+ UserData.Flats.TABLE_NAME+
            " BEGIN UPDATE "+ UserData.Flats.TABLE_NAME+" SET "+ UserData.Flats.IS_WORK+"=0 ;END";
    private static final String CREATE_TRIGGER_INSERT_WORK_FLAT = "CREATE TRIGGER IF NOT EXISTS insert_work_flat " +
            " BEFORE INSERT ON "+ UserData.Flats.TABLE_NAME+" WHEN NEW."+ UserData.Flats.IS_WORK+"=1 "+
            " BEGIN UPDATE "+ UserData.Flats.TABLE_NAME+" SET "+ UserData.Flats.IS_WORK+"=0 ;END";


    private static final String UPDATE_FLATS_ADD_REGISTRATION = "alter table " + UserData.Flats.TABLE_NAME + " add column "+ UserData.Flats.IS_REGISTRATION+" INTEGER;";
    private static final String UPDATE_FLATS_ADD_RESIDENCE = "alter table " + UserData.Flats.TABLE_NAME + " add column "+ UserData.Flats.IS_RESIDENCE+" INTEGER;";
    private static final String UPDATE_FLATS_ADD_EDITING_BLOCKED = "alter table " + UserData.Flats.TABLE_NAME + " add column "+ UserData.Flats.EDITING_BLOCKED+" INTEGER;";
    private static final String UPDATE_FLATS_ADD_WORK = "alter table " + UserData.Flats.TABLE_NAME + " add column "+ UserData.Flats.IS_WORK+" INTEGER;";
    private static final String UPDATE_FLATS_ADD_BUILDING_ID = "alter table " + UserData.Flats.TABLE_NAME + " add column "+ UserData.Flats.BUILDING_ID+" TEXT;";
    private static final String UPDATE_FLATS_ADD_CITY = "alter table " + UserData.Flats.TABLE_NAME + " add column "+ UserData.Flats.CITY+" TEXT;";
    /**
     * С версии 2.0.0
     */
    private static final String UPDATE_FLATS_ADD_DISTRICT = "alter table " + UserData.Flats.TABLE_NAME + " add column "+ UserData.Flats.DISTRICT+" TEXT;";
    private static final String UPDATE_FLATS_ADD_AREA = "alter table " + UserData.Flats.TABLE_NAME + " add column "+ UserData.Flats.AREA+" TEXT;";
    private static final String UPDATE_FLATS_ADD_AREA_ID = "alter table " + UserData.Flats.TABLE_NAME + " add column "+ UserData.Flats.AREA_ID+" TEXT;";

	public UserDataHelper(Context context) {
		super(context, UserSchema.DB_NAME, null, UserSchema.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		UserSchema.onCreate(db);
		db.execSQL(CREATE_TRIGGER_INSERT_CAR);
		db.execSQL(CREATE_TRIGGER_SET_DEFAULT_CAR);
        db.execSQL(CREATE_TRIGGER_INSERT_FLAT);
        db.execSQL(CREATE_TRIGGER_SET_DEFAULT_FLAT);

        //version 2
        db.execSQL(CREATE_TRIGGER_SET_REGISTRATION_FLAT);
        db.execSQL(CREATE_TRIGGER_INSERT_REG_FLAT);
        db.execSQL(CREATE_TRIGGER_SET_RESIDENCE_FLAT);
        db.execSQL(CREATE_TRIGGER_INSERT_RES_FLAT);

        db.execSQL(CREATE_TRIGGER_SET_WORK_FLAT);
        db.execSQL(CREATE_TRIGGER_INSERT_WORK_FLAT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) { // oldVersion=1 использовалось до версии 1.8
            db.execSQL(UPDATE_FLATS_ADD_REGISTRATION);
            db.execSQL(UPDATE_FLATS_ADD_RESIDENCE);

            db.execSQL(CREATE_TRIGGER_SET_REGISTRATION_FLAT);
            db.execSQL(CREATE_TRIGGER_INSERT_REG_FLAT);
            db.execSQL(CREATE_TRIGGER_SET_RESIDENCE_FLAT);
            db.execSQL(CREATE_TRIGGER_INSERT_RES_FLAT);
		}
        if (oldVersion < 3) { //переход на версию 1.8.1
            db.execSQL(UPDATE_FLATS_ADD_EDITING_BLOCKED);

            db.execSQL(CREATE_TRIGGER_SET_EDITING_BLOCKED_FLAT);
            db.execSQL(CREATE_TRIGGER_INSERT_EDITING_BLOCKED);
        }
        if (oldVersion < 4) { //переход на версию 1.9
            db.execSQL(UPDATE_FLATS_ADD_BUILDING_ID);
            db.execSQL(UPDATE_FLATS_ADD_WORK);

            db.execSQL(CREATE_TRIGGER_SET_WORK_FLAT);
            db.execSQL(CREATE_TRIGGER_INSERT_WORK_FLAT);
        }
        if (oldVersion < 5) { //переход на версию 1.9.1
            db.execSQL(UPDATE_FLATS_ADD_CITY);
        }
        if (oldVersion < 6) { //переход на версию 2.0.0
            db.execSQL(UPDATE_FLATS_ADD_DISTRICT);
            db.execSQL(UPDATE_FLATS_ADD_AREA);
            db.execSQL(UPDATE_FLATS_ADD_AREA_ID);
        }
	}
	
}
